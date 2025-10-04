package com.project.coding_exercise.api.controller;

import com.project.coding_exercise.db.dto.SchemaResponse;
import com.project.coding_exercise.db.dto.SchemaUploadRequest;
import com.project.coding_exercise.service.SchemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/schemas")
@Tag(name = "Schema Management", description = "API for managing OpenAPI schemas")
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload OpenAPI Schema", 
               description = "Upload and validate an OpenAPI specification file (JSON or YAML)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schema uploaded successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SchemaResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid schema or request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SchemaResponse> uploadSchema(
            @Parameter(description = "Application name", required = true)
            @RequestParam("applicationName") String applicationName,
            
            @Parameter(description = "Service name (optional)")
            @RequestParam(value = "serviceName", required = false) String serviceName,
            
            @Parameter(description = "OpenAPI schema file (JSON or YAML)", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            SchemaUploadRequest request = new SchemaUploadRequest();
            request.setApplicationName(applicationName);
            request.setServiceName(serviceName);
            request.setFile(file);
            
            SchemaResponse response = schemaService.uploadSchema(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{applicationName}/{serviceName}/latest")
    @Operation(summary = "Get Latest Schema", 
               description = "Retrieve the latest version of a schema for a specific application and service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schema retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SchemaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Schema not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SchemaResponse> getLatestSchema(
            @Parameter(description = "Application name", required = true)
            @PathVariable String applicationName,
            
            @Parameter(description = "Service name", required = true)
            @PathVariable String serviceName) {
        
        try {
            SchemaResponse response = schemaService.getLatestSchema(applicationName, serviceName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{applicationName}/{serviceName}/{version}")
    @Operation(summary = "Get Schema by Version", 
               description = "Retrieve a specific version of a schema for a specific application and service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schema retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SchemaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Schema not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SchemaResponse> getSchemaByVersion(
            @Parameter(description = "Application name", required = true)
            @PathVariable String applicationName,
            
            @Parameter(description = "Service name", required = true)
            @PathVariable String serviceName,
            
            @Parameter(description = "Schema version", required = true)
            @PathVariable Integer version) {
        
        try {
            SchemaResponse response = schemaService.getSchemaByVersion(applicationName, serviceName, version);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{applicationName}/latest")
    @Operation(summary = "Get Latest Application Schema", 
               description = "Retrieve the latest version of a schema for a specific application (no service)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schema retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SchemaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Schema not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SchemaResponse> getLatestApplicationSchema(
            @Parameter(description = "Application name", required = true)
            @PathVariable String applicationName) {
        
        try {
            SchemaResponse response = schemaService.getLatestSchema(applicationName, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{applicationName}/{version}")
    @Operation(summary = "Get Application Schema by Version", 
               description = "Retrieve a specific version of a schema for a specific application (no service)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schema retrieved successfully",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SchemaResponse.class))),
        @ApiResponse(responseCode = "404", description = "Schema not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SchemaResponse> getApplicationSchemaByVersion(
            @Parameter(description = "Application name", required = true)
            @PathVariable String applicationName,
            
            @Parameter(description = "Schema version", required = true)
            @PathVariable Integer version) {
        
        try {
            SchemaResponse response = schemaService.getSchemaByVersion(applicationName, null, version);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
