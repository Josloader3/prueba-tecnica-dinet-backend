package com.jbautista.solicitudbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local de Desarrollo")
                ))
                .info(new Info()
                        .title("API Reactiva de Gestión de Solicitudes")
                        .version("1.0.0")
                        .description("Microservicio reactivo desarrollado con Spring WebFlux y PostgreSQL para el registro, consulta, filtrado y exportación de solicitudes y contactos.")
                        .contact(new Contact()
                                .name("Solicitudes")
                                .email("desarrollo@jbautista.com.pe"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io")));
    }
}
