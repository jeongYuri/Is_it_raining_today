package com.example.crudw.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.nio.file.Path;
import java.nio.file.Paths;
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableJpaAuditing
public class CrudwApplication {
	public static void main(String[] args) {
		SpringApplication.run(CrudwApplication.class, args);

		Path path = Paths.get("storage/init/중기예보코드.csv");
		//System.out.println(path.toAbsolutePath().toString());
	}

}
