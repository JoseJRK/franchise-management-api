# Terraform AWS

Provisionamiento minimo para desplegar la API en AWS con ECS Fargate.

## Recursos

- ECS Cluster
- ECS Task Definition + Service
- IAM roles para ejecucion
- Security Group para API
- CloudWatch Log Group
- DocumentDB opcional (`create_documentdb=true`)

## Uso

```bash
terraform init
terraform plan -var="aws_region=us-east-1" -var="image_uri=<ecr-image-uri>"
terraform apply -var="aws_region=us-east-1" -var="image_uri=<ecr-image-uri>"
```

## Variables importantes

- `image_uri`: imagen docker publicada en ECR
- `mongodb_uri`: URI externa MongoDB cuando no se crea DocumentDB
- `create_documentdb`: habilita aprovisionamiento de DocumentDB
- `docdb_master_password`: obligatorio si `create_documentdb=true`

