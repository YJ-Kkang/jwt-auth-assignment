package com.yj.jwtauth.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme; // 어노테이션용
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT"
)
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new Components()
				.addSecuritySchemes("bearerAuth",
					new io.swagger.v3.oas.models.security.SecurityScheme() // 경로 직접 명시
						.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
						.name("Authorization")
				)
			)
			.info(new Info()
				.title("JWT 인증 API")
				.version("1.0")
				.description("JWT를 활용한 인증 API 명세서입니다."));
	}
}