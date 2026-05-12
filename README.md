# TaskHarbor Backend

A reactive, enterprise-grade project and task management system built with Spring WebFlux.

## Technology Stack

### Backend

| Category   | Technology        | Version  |
|------------|-------------------|----------|
| Language   | Java              | 21 LTS   |
| Framework  | Spring WebFlux    | 4.0+     |
| Database   | PostgreSQL        | 16+      |
| ORM        | Spring Data R2DBC | Reactive |
| Migration  | Flyway            | -        |
| Cache      | Redis (Reactive)  | -        |
| API Docs   | SpringDoc OpenAPI | 2.7.0    |
| Resilience | Resilience4j      | 2.2.0    |
| Build      | Maven             | 3.9+     |

### Architecture

Clean Architecture with 4 layers:

```
src/main/java/com/task/harbor/
├── domain/           # Entities, repository interfaces
├── application/      # DTOs, use cases
├── infrastructure/   # Security, persistence
└── interface/        # REST controllers
```

### Modules (Planned)

| Module              | Description                         |
|---------------------|-------------------------------------|
| Foundation          | Auth, ABAC, GDPR, Audit             |
| Task Management     | Tasks, Kanban, Comments, Documents  |
| Project Management  | Projects, WBS, Gantt, Budget, Risks |
| Collaboration       | Workspaces, Forums, Notifications   |
| Reports & Analytics | Dashboards, Reports, Alerts         |
| Integrations        | Google Calendar, Drive, Git         |
| Customization       | Custom Fields, Workflows, Templates |

## Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 16+
- Redis

## Configuration

### Environment Variables

```bash
# Database
DB_R2DBC_URL=r2dbc:postgresql://localhost:5432/taskharbor
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_HOST_FLYWAY=jdbc:postgresql://localhost:5432/taskharbor

# Redis
SPRING_DATA_REDIS_HOST_DEV=localhost
SPRING_DATA_REDIS_PORT_DEV=6379
```

### Profiles

| Profile | Description                   |
|---------|-------------------------------|
| `dev`   | Development (default)         |
| `test`  | Testing with in-memory config |
| `prod`  | Production with optimizations |

Run with profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Build & Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run

# Run tests
./mvnw test
```

## API Endpoints

### Actuator Endpoints

| Endpoint                     | Method | Description                |
|------------------------------|--------|----------------------------|
| `/actuator/health`           | GET    | Health check               |
| `/actuator/health/liveness`  | GET    | Kubernetes liveness probe  |
| `/actuator/health/readiness` | GET    | Kubernetes readiness probe |
| `/actuator/info`             | GET    | Application info           |
| `/actuator/metrics`          | GET    | Metrics                    |
| `/actuator/prometheus`       | GET    | Prometheus format          |
| `/actuator/caches`           | GET    | Cache metrics              |
| `/actuator/loggers`          | GET    | Logger configuration       |

### API Documentation

- Swagger UI: `http://localhost:8080/webjars/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

## Project Structure

```
task_harbor_backend/
├── src/main/java/com/task/harbor/
│   └── TaskHarborApplication.java
├── src/main/resources/
│   ├── application.yaml           # Base config
│   ├── application-dev.yaml       # Dev profile
│   ├── application-test.yaml      # Test profile
│   ├── application-prod.yaml      # Prod profile
│   └── db/migration/              # Flyway migrations
├── docs/
│   ├── architecture-overview.md
│   ├── technical-specification.md
└── pom.xml
```

## License

MIT
