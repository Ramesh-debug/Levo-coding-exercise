package com.project.coding_exercise.db.mapper;

import com.project.coding_exercise.db.model.Schema;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SchemaMapper {
    void insert(Schema schema);
    Schema findLatestByApplicationAndService(@Param("applicationId") Long applicationId, @Param("serviceId") Long serviceId);
    Schema findByApplicationServiceAndVersion(@Param("applicationId") Long applicationId, @Param("serviceId") Long serviceId, @Param("version") Integer version);
    Integer findMaxVersionByApplicationAndService(@Param("applicationId") Long applicationId, @Param("serviceId") Long serviceId);
    List<Schema> findAllByApplicationAndService(@Param("applicationId") Long applicationId, @Param("serviceId") Long serviceId);
}
