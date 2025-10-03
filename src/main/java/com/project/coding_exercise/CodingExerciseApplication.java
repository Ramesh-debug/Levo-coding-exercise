package com.project.coding_exercise;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.project.coding_exercise.db.mapper")
public class CodingExerciseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodingExerciseApplication.class, args);
	}

}
