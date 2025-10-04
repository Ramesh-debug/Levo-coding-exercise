# API Schema Management Service

A Spring Boot application for uploading, validating, and retrieving OpenAPI schemas with automatic versioning.

## What's Implemented

- **Upload OpenAPI schemas** (JSON/YAML) with validation
- **Automatic versioning** for each application/service
- **Get latest schema** or specific version
- **File storage** in organized directory structure
- **H2 database** for metadata storage
- **Swagger UI** for API testing

## Prerequisites

- Java 17+
- Maven 3.6+

## How to Run

### 1. Build the project
```bash
mvn clean install
```

### 2. Start the application
```bash
mvn spring-boot:run
```

Application starts at: http://localhost:8080

### 3. Access Swagger UI
http://localhost:8080/swagger-ui.html

## API Endpoints

### Upload Schema
```bash
POST /schemas/upload
Content-Type: multipart/form-data

Parameters:
- applicationName (required)
- serviceName (optional)
- file (required) - OpenAPI JSON/YAML file
```

### Get Latest Schema
```bash
GET /schemas/{applicationName}/{serviceName}/latest
GET /schemas/{applicationName}/latest
```

### Get Schema by Version
```bash
GET /schemas/{applicationName}/{serviceName}/{version}
GET /schemas/{applicationName}/{version}
```

## Example Usage

### Upload a schema
```bash
curl -X POST "http://localhost:8080/schemas/upload" \
  -F "applicationName=my-app" \
  -F "serviceName=user-service" \
  -F "file=@openapi.json"
```

### Get latest schema
```bash
curl "http://localhost:8080/schemas/my-app/user-service/latest"
```

## Database & Storage

### Database (H2)
- **Type**: H2 file-based database (not in-memory)
- **Location**: `D:/h2_schemas_db/schema_management.mv.db`
- **Tables**: applications, services, schemas (auto-created on startup)
- **Console Access**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:D:/h2_schemas_db/schema_management`
  - Username: `sa`
  - Password: `password`

### File Storage
- **Schema Files**: `{user.home}/schemas/{application}/{service}/{version}/`
- **Example**: `C:\Users\venka\schemas\my-app\user-service\1\schema.json`
- **Structure**: Each version gets its own folder with auto-incrementing version numbers

## Run Tests
```bash
mvn test
```

## Configuration

Key settings in `application.properties`:
- Database path: `spring.datasource.url=jdbc:h2:file:D:/h2_schemas_db/schema_management`
- File storage: `schema.storage.path=${user.home}/schemas`
- Swagger UI: `springdoc.swagger-ui.path=/swagger-ui.html`