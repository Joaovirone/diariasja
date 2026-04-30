package com.diariasja.aws.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API DiáriasJá")
                        .version("1.0.0")
                        .description("Documentação oficial da API RESTful para o sistema de agendamento de autônomos. " +
                                     "Esta especificação está formatada para futura integração com o Amazon API Gateway.")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("admin@diariasja.com")));
    }
}