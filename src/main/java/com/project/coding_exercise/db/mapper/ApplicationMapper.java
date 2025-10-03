package com.project.coding_exercise.db.mapper;

import com.project.coding_exercise.db.model.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper {
    void insert(Application application);
    Application findByName(String name);
    Application findById(Long id);
}
