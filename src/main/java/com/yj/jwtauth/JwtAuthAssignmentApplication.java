package com.yj.jwtauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JwtAuthAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthAssignmentApplication.class, args);
	}

}
