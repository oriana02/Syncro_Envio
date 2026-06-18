package com.Syncro.envios.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuracion general de la aplicacion.
 * Los beans de RestTemplate y ObjectMapper fueron eliminados
 * al migrar EmailNotificacionService al SDK oficial de MailerSend.
 */
@Configuration
public class AppConfig {
    // reservado para futuros beans de infraestructura
}
