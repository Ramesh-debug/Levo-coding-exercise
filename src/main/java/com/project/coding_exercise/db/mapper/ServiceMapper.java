package com.project.coding_exercise.db.mapper;

import com.project.coding_exercise.db.model.Service;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ServiceMapper {
    
    @Insert("INSERT INTO services (application_id, name) VALUES (#{applicationId}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Service service);
    
    @Select("SELECT * FROM services WHERE application_id = #{applicationId} AND name = #{name}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "applicationId", column = "application_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "createdAt", column = "created_at")
    })
    Service findByApplicationIdAndName(@Param("applicationId") Long applicationId, @Param("name") String name);
    
    @Select("SELECT * FROM services WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "applicationId", column = "application_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "createdAt", column = "created_at")
    })
    Service findById(Long id);
}
