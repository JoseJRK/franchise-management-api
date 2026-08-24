output "ecs_cluster_name" {
  value       = aws_ecs_cluster.this.name
  description = "ECS cluster name"
}

output "ecs_service_name" {
  value       = aws_ecs_service.this.name
  description = "ECS service name"
}

output "vpc_id" {
  value       = data.aws_vpc.default.id
  description = "VPC ID used by ECS"
}

output "effective_mongodb_uri" {
  value       = local.mongodb_uri
  description = "MongoDB URI configured for the task"
  sensitive   = true
}

output "documentdb_endpoint" {
  value       = var.create_documentdb ? aws_docdb_cluster.this[0].endpoint : null
  description = "DocumentDB endpoint when enabled"
}

