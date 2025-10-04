package com.project.coding_exercise.db.mapper;

import com.project.coding_exercise.db.model.Schema;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SchemaMapper {
    
    @Insert("INSERT INTO schemas (application_id, service_id, version, file_path, uploaded_at) VALUES (#{applicationId}, #{serviceId}, #{version}, #{filePath}, #{uploadedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Schema schema);

    @Select("""
    SELECT * FROM schemas
    WHERE application_id = #{applicationId}
      AND (#{serviceId} IS NULL OR service_id = #{serviceId})
    ORDER BY version DESC
    LIMIT 1
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "serviceId", column = "service_id"),
            @Result(property = "version", column = "version"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "uploadedAt", column = "uploaded_at")
    })
    Schema findLatestByApplicationAndService(@Param("applicationId") Long applicationId,
                                             @Param("serviceId") Long serviceId);

    @Select("""
    SELECT * FROM schemas
    WHERE application_id = #{applicationId}
      AND version = #{version}
      AND (#{serviceId} IS NULL OR service_id = #{serviceId})
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "applicationId", column = "application_id"),
            @Result(property = "serviceId", column = "service_id"),
            @Result(property = "version", column = "version"),
            @Result(property = "filePath", column = "file_path"),
            @Result(property = "uploadedAt", column = "uploaded_at")
    })
    Schema findByApplicationServiceAndVersion(@Param("applicationId") Long applicationId,
                                              @Param("serviceId") Long serviceId,
                                              @Param("version") Integer version);

    @Select("SELECT COALESCE(MAX(version), 0) FROM schemas WHERE application_id = #{applicationId} AND " +
            "((#{serviceId} IS NULL AND service_id IS NULL) OR (#{serviceId} IS NOT NULL AND service_id = #{serviceId}))")
    Integer findMaxVersionByApplicationAndService(@Param("applicationId") Long applicationId, @Param("serviceId") Long serviceId);
    
    @Select("SELECT * FROM schemas WHERE application_id = #{applicationId} AND " +
            "((#{serviceId} IS NULL AND service_id IS NULL) OR (#{serviceId} IS NOT NULL AND service_id = #{serviceId})) " +
            "ORDER BY version DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "applicationId", column = "application_id"),
        @Result(property = "serviceId", column = "service_id"),
        @Result(property = "version", column = "version"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "uploadedAt", column = "uploaded_at")
    })
    List<Schema> findAllByApplicationAndService(@Param("applicationId") Long applicationId, @Param("serviceId") Long serviceId);
}
