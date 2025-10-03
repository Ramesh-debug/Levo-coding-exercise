package com.project.coding_exercise.db.model;

import java.time.LocalDateTime;

public class Service {
    private Long id;
    private Long applicationId;
    private String name;
    private LocalDateTime createdAt;

    public Service() {}

    public Service(Long applicationId, String name) {
        this.applicationId = applicationId;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
