package com.Syncro.envios.service;

import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailNotificacionService {

    @Value("${mailersend.api-key}")
    private String apiKey;

    @Value("${mailersend.from-email}")
    private String fromEmail;

    @Value("${mailersend.from-name}")
    private String fromName;

    public void enviarConfirmacionPedido(String destinatarioEmail, String destinatarioNombre, Long pedidoId) {
        if (destinatarioEmail == null || destinatarioEmail.isBlank()) {
            log.warn("Email no disponible para pedido {}, se omite", pedidoId);
            return;
        }

        try {
            Email email = new Email();
            email.setFrom(fromName, fromEmail);
            email.addRecipient(destinatarioNombre != null ? destinatarioNombre : "Cliente", destinatarioEmail);
            email.setSubject("Tu pedido #" + pedidoId + " fue confirmado - Syncro");
            email.setPlain("Hola " + destinatarioNombre + ", tu pedido #" + pedidoId + " fue confirmado y esta siendo preparado.");
            email.setHtml("<h2>Hola " + destinatarioNombre + "</h2><p>Tu pedido <strong>#" + pedidoId + "</strong> fue confirmado y esta en preparacion.</p><p>Te notificaremos cuando sea despachado.</p><br><p>Equipo Syncro</p>");

            MailerSend ms = new MailerSend();
            ms.setToken(apiKey);

            MailerSendResponse response = ms.emails().send(email);
            log.info("Email enviado correctamente a {} para pedido {}. MessageId: {}",
                    destinatarioEmail, pedidoId, response.messageId);

        } catch (MailerSendException e) {
            log.error("Error MailerSend al enviar email para pedido {}. Codigo: {}. Errores: {}",
                    pedidoId, e.code, e.errors);
        } catch (Exception e) {
            log.error("Error inesperado al enviar email para pedido {}: {}", pedidoId, e.getMessage());
        }
    }
}
