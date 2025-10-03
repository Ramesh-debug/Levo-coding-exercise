# API Schema Management Service

A Spring Boot application for managing OpenAPI schemas with versioning support. This service allows you to upload, validate, and retrieve OpenAPI specifications (JSON/YAML) with automatic versioning and file storage.

## Features

- **Schema Upload**: Upload OpenAPI specifications (JSON or YAML) with validation
- **Version Management**: Automatic versioning for schemas per application/service
- **Schema Retrieval**: Get latest or specific versions of schemas
- **File Storage**: Persistent storage of schema files in organized directory structure
- **Database Support**: H2 in-memory database with MyBatis for data persistence
- **API Documentation**: Swagger UI for interactive API documentation
- **Validation**: OpenAPI specification validation before storage

## Technology Stack

- **Spring Boot 3.4.10**
- **Java 17**
- **Maven**
- **H2 Database** (in-memory)
- **MyBatis** (ORM)
- **Swagger/OpenAPI 3** (API documentation)
- **JUnit 5** (Testing)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### 1. Clone and Build

```bash
git clone <repository-url>
cd levo-coding-exercise
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the Services

- **API Documentation (Swagger UI)**: http://localhost:8080/swagger-ui.html
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## API Endpoints

### Upload Schema
```
POST /schemas/upload
Content-Type: multipart/form-data

Parameters:
- applicationName (required): Name of the application
- serviceName (optional): Name of the service
- file (required): OpenAPI specification file (JSON or YAML)
```

### Get Latest Schema
```
GET /schemas/{applicationName}/{serviceName}/latest
GET /schemas/{applicationName}/latest  (for application-level schemas)
```

### Get Schema by Version
```
GET /schemas/{applicationName}/{serviceName}/{version}
GET /schemas/{applicationName}/{version}  (for application-level schemas)
```

## Usage Examples

### 1. Upload a Schema

```bash
curl -X POST "http://localhost:8080/schemas/upload" \
  -F "applicationName=my-app" \
  -F "serviceName=user-service" \
  -F "file=@openapi.json"
```

### 2. Get Latest Schema

```bash
curl "http://localhost:8080/schemas/my-app/user-service/latest"
```

### 3. Get Specific Version

```bash
curl "http://localhost:8080/schemas/my-app/user-service/1"
```

## Database Schema

The application uses three main tables:

- **applications**: Stores application information
- **services**: Stores service information (linked to applications)
- **schemas**: Stores schema metadata with versioning

## File Storage

Schema files are stored in the following directory structure:
```
schemas/
├── {applicationName}/
│   ├── {serviceName}/
│   │   ├── 1/
│   │   │   └── schema.json
│   │   ├── 2/
│   │   │   └── schema.json
│   │   └── ...
│   └── latest/  (for application-level schemas)
└── ...
```

## Testing

Run the test suite:

```bash
mvn test
```

The test suite includes:
- Unit tests for service layer
- Integration tests for controller layer
- Schema validation tests
- Version management tests

## Configuration

Key configuration properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# File Storage
schema.storage.path=./schemas

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

## Development

### Project Structure

```
src/main/java/com/project/coding_exercise/
├── api/controller/          # REST controllers
├── service/                 # Service interfaces
├── serviceImpl/             # Service implementations
├── db/
│   ├── mapper/              # MyBatis mappers
│   ├── dto/                 # DTOs for requests/responses
│   └── model/               # Entity models
└── CodingExerciseApplication.java
```

### Adding New Features

1. Create entity models in `db/model/`
2. Create MyBatis mappers in `db/mapper/`
3. Implement service logic in `serviceImpl/`
4. Create REST endpoints in `api/controller/`
5. Add comprehensive tests

## License

This project is part of a coding exercise for Levo.ai.
