package com.project.coding_exercise.db.model;

import java.time.LocalDateTime;

public class Schema {
    private Long id;
    private Long applicationId;
    private Long serviceId;
    private Integer version;
    private String filePath;
    private LocalDateTime uploadedAt;

    public Schema() {}

    public Schema(Long applicationId, Long serviceId, Integer version, String filePath) {
        this.applicationId = applicationId;
        this.serviceId = serviceId;
        this.version = version;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
