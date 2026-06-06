package com.Syncro.envios.config;

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
                .title("Syncro - MS Envios API")
                .version("1.0.0")
                .description("Microservicio de gestion de envios y despachos. Consume eventos pedido.creado desde RabbitMQ y envia notificaciones por email via MailerSend.")
                .contact(new Contact()
                    .name("Syncro Team")
                    .email("oriana@pyme-demo.cl")));
    }
}