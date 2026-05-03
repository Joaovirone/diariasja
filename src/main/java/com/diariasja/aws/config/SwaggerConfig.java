package com.diariasja.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Diárias Já API").version("1.0.0")
                .description("Documentação oficial da API RESTful para o sistema de agendamento de autônomos. Esta especificação está formatada para futura integração com o Amazon API Gateway."))
                // Adiciona o cadeado de segurança em todos os endpoints

                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                // Configura o formato do Token (JWT)
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}