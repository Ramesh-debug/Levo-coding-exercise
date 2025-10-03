package com.project.coding_exercise.service;

import com.project.coding_exercise.db.dto.SchemaResponse;
import com.project.coding_exercise.db.dto.SchemaUploadRequest;
import org.springframework.web.multipart.MultipartFile;

public interface SchemaService {
    SchemaResponse uploadSchema(SchemaUploadRequest request) throws Exception;
    SchemaResponse getLatestSchema(String applicationName, String serviceName) throws Exception;
    SchemaResponse getSchemaByVersion(String applicationName, String serviceName, Integer version) throws Exception;
    boolean validateOpenApiSpec(MultipartFile file) throws Exception;
}
