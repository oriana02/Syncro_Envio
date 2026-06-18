package com.Syncro.envios.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class EmailNotificacionServiceTest {

    @InjectMocks
    EmailNotificacionService emailService;

    @Test
    @DisplayName("enviarConfirmacionPedido - omite si email es nulo")
    void omiteEmailNulo() {
        ReflectionTestUtils.setField(emailService, "apiKey", "test-key");
        ReflectionTestUtils.setField(emailService, "fromEmail", "no-reply@test.com");
        ReflectionTestUtils.setField(emailService, "fromName", "Test");

        assertDoesNotThrow(() ->
            emailService.enviarConfirmacionPedido(null, "Juan", 1L)
        );
    }

    @Test
    @DisplayName("enviarConfirmacionPedido - omite si email es blanco")
    void omiteEmailBlanco() {
        ReflectionTestUtils.setField(emailService, "apiKey", "test-key");
        ReflectionTestUtils.setField(emailService, "fromEmail", "no-reply@test.com");
        ReflectionTestUtils.setField(emailService, "fromName", "Test");

        assertDoesNotThrow(() ->
            emailService.enviarConfirmacionPedido("   ", "Juan", 1L)
        );
    }
}
