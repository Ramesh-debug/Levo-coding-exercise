package com.project.coding_exercise.db.mapper;

import com.project.coding_exercise.db.model.Service;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServiceMapper {
    void insert(Service service);
    Service findByApplicationIdAndName(@Param("applicationId") Long applicationId, @Param("name") String name);
    Service findById(Long id);
}
