package com.project.coding_exercise.serviceImpl;

import com.project.coding_exercise.db.dto.SchemaResponse;
import com.project.coding_exercise.db.dto.SchemaUploadRequest;
import com.project.coding_exercise.db.mapper.ApplicationMapper;
import com.project.coding_exercise.db.mapper.SchemaMapper;
import com.project.coding_exercise.db.mapper.ServiceMapper;
import com.project.coding_exercise.db.model.Application;
import com.project.coding_exercise.db.model.Schema;
import com.project.coding_exercise.service.SchemaService;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class SchemaServiceImpl implements SchemaService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private SchemaMapper schemaMapper;

    @Value("${schema.storage.path:./schemas}")
    private String storagePath;

    @Override
    public SchemaResponse uploadSchema(SchemaUploadRequest request) throws Exception {
        // Validate the OpenAPI spec
        if (!validateOpenApiSpec(request.getFile())) {
            throw new IllegalArgumentException("Invalid OpenAPI specification");
        }

        // Get or create application
        Application application = applicationMapper.findByName(request.getApplicationName());
        if (application == null) {
            application = new Application(request.getApplicationName());
            applicationMapper.insert(application);
        }

        // Get or create service (if provided)
        com.project.coding_exercise.db.model.Service service = null;
        if (request.getServiceName() != null && !request.getServiceName().trim().isEmpty()) {
            service = serviceMapper.findByApplicationIdAndName(application.getId(), request.getServiceName());
            if (service == null) {
                service = new com.project.coding_exercise.db.model.Service(application.getId(), request.getServiceName());
                serviceMapper.insert(service);
            }
        }

        // Get next version number
        Integer nextVersion = schemaMapper.findMaxVersionByApplicationAndService(application.getId(), service != null ? service.getId() : null) + 1;

        // Create storage directory structure
        String directoryPath = createDirectoryStructure(application.getName(), service != null ? service.getName() : null, nextVersion);
        
        // Save file
        String fileName = request.getFile().getOriginalFilename();
        String filePath = directoryPath + File.separator + fileName;
        request.getFile().transferTo(new File(filePath));

        // Save schema metadata to database
        Schema schema = new Schema(application.getId(), service != null ? service.getId() : null, nextVersion, filePath);
        schemaMapper.insert(schema);

        // Return response
        return createSchemaResponse(schema, application.getName(), service != null ? service.getName() : null, filePath);
    }

    @Override
    public SchemaResponse getLatestSchema(String applicationName, String serviceName) throws Exception {
        Application application = applicationMapper.findByName(applicationName);
        if (application == null) {
            throw new IllegalArgumentException("Application not found: " + applicationName);
        }

        com.project.coding_exercise.db.model.Service service = null;
        if (serviceName != null && !serviceName.trim().isEmpty()) {
            service = serviceMapper.findByApplicationIdAndName(application.getId(), serviceName);
            if (service == null) {
                throw new IllegalArgumentException("Service not found: " + serviceName);
            }
        }

        Schema schema = schemaMapper.findLatestByApplicationAndService(application.getId(), service != null ? service.getId() : null);
        if (schema == null) {
            throw new IllegalArgumentException("No schema found for application: " + applicationName + 
                (serviceName != null ? " and service: " + serviceName : ""));
        }

        String content = Files.readString(Paths.get(schema.getFilePath()));
        return createSchemaResponse(schema, applicationName, serviceName, content);
    }

    @Override
    public SchemaResponse getSchemaByVersion(String applicationName, String serviceName, Integer version) throws Exception {
        Application application = applicationMapper.findByName(applicationName);
        if (application == null) {
            throw new IllegalArgumentException("Application not found: " + applicationName);
        }

        com.project.coding_exercise.db.model.Service service = null;
        if (serviceName != null && !serviceName.trim().isEmpty()) {
            service = serviceMapper.findByApplicationIdAndName(application.getId(), serviceName);
            if (service == null) {
                throw new IllegalArgumentException("Service not found: " + serviceName);
            }
        }

        Schema schema = schemaMapper.findByApplicationServiceAndVersion(application.getId(), 
            service != null ? service.getId() : null, version);
        if (schema == null) {
            throw new IllegalArgumentException("Schema not found for application: " + applicationName + 
                (serviceName != null ? " and service: " + serviceName : "") + " version: " + version);
        }

        String content = Files.readString(Paths.get(schema.getFilePath()));
        return createSchemaResponse(schema, applicationName, serviceName, content);
    }

    @Override
    public boolean validateOpenApiSpec(MultipartFile file) throws Exception {
        try {
            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            ParseOptions options = new ParseOptions();
            options.setResolve(true);
            
            // Read the InputStream content as String
            String content = new String(file.getInputStream().readAllBytes());
            SwaggerParseResult result = parser.readContents(content, null, options);
            return result.getOpenAPI() != null && result.getMessages().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private String createDirectoryStructure(String applicationName, String serviceName, Integer version) throws IOException {
        String basePath = storagePath + File.separator + applicationName;
        if (serviceName != null && !serviceName.trim().isEmpty()) {
            basePath += File.separator + serviceName;
        }
        basePath += File.separator + version;
        
        Path path = Paths.get(basePath);
        Files.createDirectories(path);
        return basePath;
    }

    private SchemaResponse createSchemaResponse(Schema schema, String applicationName, String serviceName, String content) {
        SchemaResponse response = new SchemaResponse();
        response.setId(schema.getId());
        response.setApplicationName(applicationName);
        response.setServiceName(serviceName);
        response.setVersion(schema.getVersion());
        response.setFilePath(schema.getFilePath());
        response.setUploadedAt(schema.getUploadedAt());
        response.setContent(content);
        return response;
    }
}
