package com.project.coding_exercise.service;

import com.project.coding_exercise.db.dto.SchemaResponse;
import com.project.coding_exercise.db.dto.SchemaUploadRequest;
import com.project.coding_exercise.db.mapper.ApplicationMapper;
import com.project.coding_exercise.db.mapper.SchemaMapper;
import com.project.coding_exercise.db.mapper.ServiceMapper;
import com.project.coding_exercise.db.model.Application;
import com.project.coding_exercise.db.model.Schema;
import com.project.coding_exercise.db.model.Service;
import com.project.coding_exercise.serviceImpl.SchemaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchemaServiceTest {

    @Mock
    private ApplicationMapper applicationMapper;

    @Mock
    private ServiceMapper serviceMapper;

    @Mock
    private SchemaMapper schemaMapper;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private SchemaServiceImpl schemaService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(schemaService, "storagePath", "./test-schemas");
    }

    @Test
    void testUploadSchema_NewApplicationAndService() throws Exception {
        // Arrange
        String applicationName = "test-app";
        String serviceName = "test-service";
        String openApiContent = """
            {
                "openapi": "3.0.0",
                "info": {
                    "title": "Test API",
                    "version": "1.0.0"
                },
                "paths": {}
            }
            """;

        when(multipartFile.getOriginalFilename()).thenReturn("test.json");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(openApiContent.getBytes()));
        when(applicationMapper.findByName(applicationName)).thenReturn(null);
        when(serviceMapper.findByApplicationIdAndName(any(), eq(serviceName))).thenReturn(null);
        when(schemaMapper.findMaxVersionByApplicationAndService(any(), any())).thenReturn(0);

        // Mock file operations
        doNothing().when(applicationMapper).insert(any(Application.class));
        doNothing().when(serviceMapper).insert(any(Service.class));
        doNothing().when(schemaMapper).insert(any(Schema.class));

        SchemaUploadRequest request = new SchemaUploadRequest();
        request.setApplicationName(applicationName);
        request.setServiceName(serviceName);
        request.setFile(multipartFile);

        // Act
        SchemaResponse response = schemaService.uploadSchema(request);

        // Assert
        assertNotNull(response);
        assertEquals(applicationName, response.getApplicationName());
        assertEquals(serviceName, response.getServiceName());
        assertEquals(1, response.getVersion());
        assertNotNull(response.getFilePath());
        assertNotNull(response.getUploadedAt());

        verify(applicationMapper).insert(any(Application.class));
        verify(serviceMapper).insert(any(Service.class));
        verify(schemaMapper).insert(any(Schema.class));
    }

    @Test
    void testUploadSchema_ExistingApplication() throws Exception {
        // Arrange
        String applicationName = "existing-app";
        String serviceName = "test-service";
        Application existingApp = new Application(applicationName);
        existingApp.setId(1L);

        when(multipartFile.getOriginalFilename()).thenReturn("test.json");
        String validOpenApiContent = """
            {
                "openapi": "3.0.0",
                "info": {
                    "title": "Test API",
                    "version": "1.0.0"
                },
                "paths": {
                    "/test": {
                        "get": {
                            "responses": {
                                "200": {
                                    "description": "Success"
                                }
                            }
                        }
                    }
                }
            }
            """;
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(validOpenApiContent.getBytes()));
        when(applicationMapper.findByName(applicationName)).thenReturn(existingApp);
        when(serviceMapper.findByApplicationIdAndName(1L, serviceName)).thenReturn(null);
        when(schemaMapper.findMaxVersionByApplicationAndService(1L, null)).thenReturn(2);

        doNothing().when(serviceMapper).insert(any(Service.class));
        doNothing().when(schemaMapper).insert(any(Schema.class));

        SchemaUploadRequest request = new SchemaUploadRequest();
        request.setApplicationName(applicationName);
        request.setServiceName(serviceName);
        request.setFile(multipartFile);

        // Act
        SchemaResponse response = schemaService.uploadSchema(request);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.getVersion()); // Should increment from 2 to 3
        verify(applicationMapper, never()).insert(any(Application.class));
    }

    @Test
    void testGetLatestSchema() throws Exception {
        // Arrange
        String applicationName = "test-app";
        String serviceName = "test-service";
        Application app = new Application(applicationName);
        app.setId(1L);
        Service service = new Service(1L, serviceName);
        service.setId(1L);
        Schema schema = new Schema(1L, 1L, 2, "/path/to/schema.json");
        schema.setId(1L);
        schema.setUploadedAt(LocalDateTime.now());

        when(applicationMapper.findByName(applicationName)).thenReturn(app);
        when(serviceMapper.findByApplicationIdAndName(1L, serviceName)).thenReturn(service);
        when(schemaMapper.findLatestByApplicationAndService(1L, 1L)).thenReturn(schema);

        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test-schema", ".json");
        Files.write(tempFile, "{\"openapi\": \"3.0.0\"}".getBytes());
        schema.setFilePath(tempFile.toString());

        // Act
        SchemaResponse response = schemaService.getLatestSchema(applicationName, serviceName);

        // Assert
        assertNotNull(response);
        assertEquals(applicationName, response.getApplicationName());
        assertEquals(serviceName, response.getServiceName());
        assertEquals(2, response.getVersion());
        assertNotNull(response.getContent());

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testGetSchemaByVersion() throws Exception {
        // Arrange
        String applicationName = "test-app";
        String serviceName = "test-service";
        Integer version = 1;
        Application app = new Application(applicationName);
        app.setId(1L);
        Service service = new Service(1L, serviceName);
        service.setId(1L);
        Schema schema = new Schema(1L, 1L, version, "/path/to/schema.json");
        schema.setId(1L);
        schema.setUploadedAt(LocalDateTime.now());

        when(applicationMapper.findByName(applicationName)).thenReturn(app);
        when(serviceMapper.findByApplicationIdAndName(1L, serviceName)).thenReturn(service);
        when(schemaMapper.findByApplicationServiceAndVersion(1L, 1L, version)).thenReturn(schema);

        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test-schema", ".json");
        Files.write(tempFile, "{\"openapi\": \"3.0.0\"}".getBytes());
        schema.setFilePath(tempFile.toString());

        // Act
        SchemaResponse response = schemaService.getSchemaByVersion(applicationName, serviceName, version);

        // Assert
        assertNotNull(response);
        assertEquals(applicationName, response.getApplicationName());
        assertEquals(serviceName, response.getServiceName());
        assertEquals(version, response.getVersion());
        assertNotNull(response.getContent());

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testValidateOpenApiSpec_ValidJson() throws Exception {
        // Arrange
        String validOpenApiJson = """
            {
                "openapi": "3.0.0",
                "info": {
                    "title": "Test API",
                    "version": "1.0.0"
                },
                "paths": {
                    "/test": {
                        "get": {
                            "responses": {
                                "200": {
                                    "description": "Success"
                                }
                            }
                        }
                    }
                }
            }
            """;

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(validOpenApiJson.getBytes()));

        // Act
        boolean isValid = schemaService.validateOpenApiSpec(multipartFile);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateOpenApiSpec_InvalidJson() throws Exception {
        // Arrange
        String invalidJson = "{\"invalid\": \"json\"}";

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(invalidJson.getBytes()));

        // Act
        boolean isValid = schemaService.validateOpenApiSpec(multipartFile);

        // Assert
        assertFalse(isValid);
    }
}
