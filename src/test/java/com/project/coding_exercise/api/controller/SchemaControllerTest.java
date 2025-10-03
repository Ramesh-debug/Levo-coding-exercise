package com.project.coding_exercise.api.controller;

import com.project.coding_exercise.db.dto.SchemaResponse;
import com.project.coding_exercise.service.SchemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SchemaController.class)
class SchemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchemaService schemaService;

    @Test
    void testUploadSchema() throws Exception {
        // Arrange
        SchemaResponse mockResponse = new SchemaResponse();
        mockResponse.setId(1L);
        mockResponse.setApplicationName("test-app");
        mockResponse.setServiceName("test-service");
        mockResponse.setVersion(1);
        mockResponse.setFilePath("/path/to/schema.json");
        mockResponse.setUploadedAt(LocalDateTime.now());

        when(schemaService.uploadSchema(any())).thenReturn(mockResponse);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                "{\"openapi\": \"3.0.0\"}".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/schemas/upload")
                .file(file)
                .param("applicationName", "test-app")
                .param("serviceName", "test-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("test-app"))
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void testGetLatestSchema() throws Exception {
        // Arrange
        SchemaResponse mockResponse = new SchemaResponse();
        mockResponse.setId(1L);
        mockResponse.setApplicationName("test-app");
        mockResponse.setServiceName("test-service");
        mockResponse.setVersion(2);
        mockResponse.setContent("{\"openapi\": \"3.0.0\"}");

        when(schemaService.getLatestSchema("test-app", "test-service")).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/schemas/test-app/test-service/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("test-app"))
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.version").value(2));
    }

    @Test
    void testGetSchemaByVersion() throws Exception {
        // Arrange
        SchemaResponse mockResponse = new SchemaResponse();
        mockResponse.setId(1L);
        mockResponse.setApplicationName("test-app");
        mockResponse.setServiceName("test-service");
        mockResponse.setVersion(1);
        mockResponse.setContent("{\"openapi\": \"3.0.0\"}");

        when(schemaService.getSchemaByVersion("test-app", "test-service", 1)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/schemas/test-app/test-service/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("test-app"))
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void testGetLatestApplicationSchema() throws Exception {
        // Arrange
        SchemaResponse mockResponse = new SchemaResponse();
        mockResponse.setId(1L);
        mockResponse.setApplicationName("test-app");
        mockResponse.setServiceName(null);
        mockResponse.setVersion(1);
        mockResponse.setContent("{\"openapi\": \"3.0.0\"}");

        when(schemaService.getLatestSchema("test-app", null)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/schemas/test-app/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationName").value("test-app"))
                .andExpect(jsonPath("$.serviceName").isEmpty())
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void testUploadSchema_InvalidSchema() throws Exception {
        // Arrange
        when(schemaService.uploadSchema(any())).thenThrow(new IllegalArgumentException("Invalid schema"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                "{\"invalid\": \"json\"}".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/schemas/upload")
                .file(file)
                .param("applicationName", "test-app")
                .param("serviceName", "test-service"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetLatestSchema_NotFound() throws Exception {
        // Arrange
        when(schemaService.getLatestSchema("nonexistent-app", "nonexistent-service"))
                .thenThrow(new IllegalArgumentException("Schema not found"));

        // Act & Assert
        mockMvc.perform(get("/schemas/nonexistent-app/nonexistent-service/latest"))
                .andExpect(status().isNotFound());
    }
}
