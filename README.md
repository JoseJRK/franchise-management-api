# Franchise Management API

API backend reactiva para gestionar franquicias, sucursales y productos con stock, diseñada para una prueba tecnica con foco en Clean Architecture, SOLID y despliegue en AWS.

## Arquitectura

```text
src/main/java/com/test/franchise_management_api
│
├── domain
│   ├── model
│   ├── repository
│   └── service
├── application
│   ├── dto
│   ├── mapper
│   └── usecase
├── infrastructure
│   ├── configuration
│   ├── persistence
│   │   ├── adapter
│   │   ├── document
│   │   └── repository
│   └── web
└── FranchiseManagementApiApplication.java
```

- `domain`: reglas y contratos de negocio sin dependencias de framework.
- `application`: casos de uso, DTOs y mapeo para exponer la API.
- `infrastructure`: Mongo reactivo, controladores HTTP, OpenAPI y configuracion.

## Decision de persistencia

Se eligio **modelo por colecciones separadas** (`franchises`, `branches`, `products`) en lugar de un solo documento anidado.

Ventajas:
- mejor escalabilidad para sucursales/productos grandes.
- actualizaciones puntuales mas simples (renombre, stock, borrado).
- indices compuestos por scope (`franchiseId + normalizedName`, `branchId + normalizedName`).
- consulta eficiente para "producto con mayor stock por sucursal".

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring WebFlux
- Spring Data Reactive MongoDB
- Maven
- Springdoc OpenAPI (Swagger)
- JUnit 5 + Mockito + WebTestClient
- Docker + Docker Compose
- Terraform + AWS (ECS Fargate + ECR + DocumentDB opcional)

## Endpoints principales

- `POST /api/v1/franchises`
- `POST /api/v1/franchises/{franchiseId}/branches`
- `POST /api/v1/franchises/{franchiseId}/branches/{branchId}/products`
- `DELETE /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}`
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock`
- `PATCH /api/v1/franchises/{franchiseId}`
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}`
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}`
- `GET /api/v1/franchises/{franchiseId}/products/max-stock`

## Variables de entorno

- `SERVER_PORT` (default: `8080`)
- `MONGODB_URI` (default local: `mongodb://localhost:27017/franchise_management`)

## Requisitos

- Java 21
- Maven 3.9+
- Docker
- Docker Compose
- Terraform 1.6+
- AWS CLI (para despliegue cloud)

## Ejecucion local

```bash
mvn clean verify
mvn spring-boot:run
```

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Tests

```bash
mvn test
```

Cobertura enfocada en:
- casos de uso (exitos y errores de negocio)
- controlador reactivo con WebTestClient
- validaciones y manejo global de errores

## Docker

Construir imagen:

```bash
docker build -t franchise-management-api:local .
```

Levantar API + MongoDB:

```bash
docker compose up --build
```

## Terraform (AWS)

Archivos en `infra/terraform`.

```bash
cd infra/terraform
terraform init
terraform plan -var="aws_region=us-east-1" -var="image_uri=<tu-ecr-image-uri>"
terraform apply -var="aws_region=us-east-1" -var="image_uri=<tu-ecr-image-uri>"
```

## Arquitectura cloud propuesta

Flujo recomendado:

`Codigo -> Imagen Docker -> ECR -> ECS Fargate -> CloudWatch`

Persistencia:
- Opcion basica: `MONGODB_URI` externa gestionada por entorno.
- Opcion administrada AWS: DocumentDB (habilitable por variable Terraform).

## Observabilidad

- Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`)
- Request correlation ID via `X-Request-Id`
- logging con request id en patron de log

## Seguridad y buenas practicas

- Sin secretos hardcodeados.
- Configuracion sensible via variables de entorno.
- Validaciones en DTOs + validaciones de negocio.
- Manejo global de errores con estructura consistente.
- Cadena reactiva end-to-end sin `.block()` ni `.subscribe()` manual.

## SOLID y Clean Code

- SRP: controladores delegan en casos de uso; persistencia encapsulada en adapters.
- OCP/DIP: casos de uso dependen de interfaces (`*RepositoryPort`).
- ISP: puertos pequenos por agregado (`Franchise`, `Branch`, `Product`).
- Cohesion alta y bajo acoplamiento por capa.

