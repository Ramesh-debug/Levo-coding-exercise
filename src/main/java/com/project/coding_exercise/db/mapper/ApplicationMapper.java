package com.project.coding_exercise.db.mapper;

import com.project.coding_exercise.db.model.Application;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ApplicationMapper {
    
    @Insert("INSERT INTO applications (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Application application);
    
    @Select("SELECT * FROM applications WHERE name = #{name}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "createdAt", column = "created_at")
    })
    Application findByName(String name);
    
    @Select("SELECT * FROM applications WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "createdAt", column = "created_at")
    })
    Application findById(Long id);
}
