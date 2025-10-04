package com.project.coding_exercise.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.project.coding_exercise.db.mapper")
public class MyBatisConfig {
    // MyBatis configuration is handled by the starter
    // This class ensures proper scanning of mapper interfaces
}
