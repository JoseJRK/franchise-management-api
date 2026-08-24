terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

resource "aws_cloudwatch_log_group" "api" {
  name              = "/ecs/${var.project_name}"
  retention_in_days = 14
}

resource "aws_ecs_cluster" "this" {
  name = "${var.project_name}-cluster"
}

resource "aws_security_group" "ecs_service" {
  name        = "${var.project_name}-ecs-sg"
  description = "Allow HTTP access to API"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = var.container_port
    to_port     = var.container_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.project_name}-task-exec-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_task_role" {
  name = "${var.project_name}-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_security_group" "documentdb" {
  count       = var.create_documentdb ? 1 : 0
  name        = "${var.project_name}-docdb-sg"
  description = "Allow ECS access to DocumentDB"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_service.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_docdb_subnet_group" "this" {
  count      = var.create_documentdb ? 1 : 0
  name       = "${var.project_name}-docdb-subnet-group"
  subnet_ids = data.aws_subnets.default.ids
}

resource "aws_docdb_cluster" "this" {
  count               = var.create_documentdb ? 1 : 0
  cluster_identifier  = "${var.project_name}-docdb"
  engine              = "docdb"
  master_username     = var.docdb_master_username
  master_password     = var.docdb_master_password
  db_subnet_group_name = aws_docdb_subnet_group.this[0].name
  vpc_security_group_ids = [aws_security_group.documentdb[0].id]
  skip_final_snapshot = true
}

resource "aws_docdb_cluster_instance" "this" {
  count              = var.create_documentdb ? 1 : 0
  identifier         = "${var.project_name}-docdb-1"
  cluster_identifier = aws_docdb_cluster.this[0].id
  instance_class     = var.docdb_instance_class
}

locals {
  mongodb_uri = var.create_documentdb
    ? "mongodb://${var.docdb_master_username}:${var.docdb_master_password}@${aws_docdb_cluster.this[0].endpoint}:27017/franchise_management?tls=true&retryWrites=false"
    : var.mongodb_uri
}

resource "aws_ecs_task_definition" "this" {
  family                   = "${var.project_name}-task"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "${var.project_name}-api"
      image     = var.image_uri
      essential = true
      portMappings = [
        {
          containerPort = var.container_port
          hostPort      = var.container_port
          protocol      = "tcp"
        }
      ]
      environment = [
        {
          name  = "SERVER_PORT"
          value = tostring(var.container_port)
        },
        {
          name  = "MONGODB_URI"
          value = local.mongodb_uri
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.api.name
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "this" {
  name            = "${var.project_name}-service"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.this.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.ecs_service.id]
    assign_public_ip = true
  }

  depends_on = [aws_iam_role_policy_attachment.ecs_task_execution_role_policy]
}

