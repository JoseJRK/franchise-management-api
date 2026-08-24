variable "project_name" {
  type        = string
  description = "Project name prefix for resources"
  default     = "franchise-management-api"
}

variable "aws_region" {
  type        = string
  description = "AWS region"
}

variable "image_uri" {
  type        = string
  description = "Container image URI in ECR"
}

variable "container_port" {
  type        = number
  description = "Application container port"
  default     = 8080
}

variable "desired_count" {
  type        = number
  description = "Desired number of ECS tasks"
  default     = 1
}

variable "task_cpu" {
  type        = string
  description = "Fargate CPU units"
  default     = "256"
}

variable "task_memory" {
  type        = string
  description = "Fargate memory"
  default     = "512"
}

variable "mongodb_uri" {
  type        = string
  description = "External MongoDB URI used when create_documentdb is false"
  default     = "mongodb://localhost:27017/franchise_management"
}

variable "create_documentdb" {
  type        = bool
  description = "If true, provisions Amazon DocumentDB"
  default     = false
}

variable "docdb_master_username" {
  type        = string
  description = "DocumentDB master username"
  default     = "franchise_admin"
}

variable "docdb_master_password" {
  type        = string
  description = "DocumentDB master password"
  sensitive   = true
  default     = ""
}

variable "docdb_instance_class" {
  type        = string
  description = "DocumentDB instance class"
  default     = "db.t3.medium"
}

