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
            email.setSubject("✅ Tu pedido #" + pedidoId + " fue confirmado - Syncro");
            email.setPlain("Hola " + destinatarioNombre + ", tu pedido #" + pedidoId + " fue confirmado.");
            email.setHtml(buildHtml(destinatarioNombre, pedidoId));

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

    private String buildHtml(String nombre, Long pedidoId) {
        return "<!DOCTYPE html>" +
            "<html lang='es'><head><meta charset='UTF-8'>" +
            "<style>" +
            "  body { margin:0; padding:0; background:#f4f4f4; font-family: Arial, sans-serif; }" +
            "  .wrapper { max-width:600px; margin:30px auto; background:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1); }" +
            "  .header { background:#4F46E5; padding:30px 20px; text-align:center; }" +
            "  .header h1 { color:#ffffff; margin:0; font-size:26px; }" +
            "  .header p { color:#c7d2fe; margin:6px 0 0; font-size:14px; }" +
            "  .body { padding:30px; }" +
            "  .body h2 { color:#1f2937; font-size:20px; margin-bottom:8px; }" +
            "  .body p { color:#4b5563; font-size:15px; line-height:1.6; }" +
            "  .order-box { background:#f9fafb; border:1px solid #e5e7eb; border-radius:6px; padding:20px; margin:20px 0; }" +
            "  .order-box table { width:100%; border-collapse:collapse; }" +
            "  .order-box td { padding:10px 6px; font-size:14px; color:#374151; border-bottom:1px solid #e5e7eb; }" +
            "  .order-box td:last-child { text-align:right; font-weight:bold; }" +
            "  .order-box tr:last-child td { border-bottom:none; color:#4F46E5; font-size:16px; }" +
            "  .status { display:inline-block; background:#d1fae5; color:#065f46; padding:6px 16px; border-radius:20px; font-size:13px; font-weight:bold; margin-bottom:20px; }" +
            "  .btn { display:block; width:fit-content; margin:24px auto 0; background:#4F46E5; color:#ffffff; text-decoration:none; padding:12px 32px; border-radius:6px; font-size:15px; font-weight:bold; }" +
            "  .footer { background:#f9fafb; padding:20px; text-align:center; color:#9ca3af; font-size:12px; border-top:1px solid #e5e7eb; }" +
            "</style></head><body>" +
            "<div class='wrapper'>" +
            "  <div class='header'>" +
            "    <h1>¡Pedido Confirmado! 🎉</h1>" +
            "    <p>Gracias por confiar en Syncro</p>" +
            "  </div>" +
            "  <div class='body'>" +
            "    <h2>Hola, " + nombre + "</h2>" +
            "    <p>Tu pedido ha sido confirmado exitosamente y ya está siendo preparado por nuestro equipo.</p>" +
            "    <span class='status'>✅ EN PREPARACIÓN</span>" +
            "    <div class='order-box'>" +
            "      <table>" +
            "        <tr><td>Número de pedido</td><td>#" + pedidoId + "</td></tr>" +
            "        <tr><td>Estado</td><td>Confirmado</td></tr>" +
            "        <tr><td>Próximo paso</td><td>En preparación</td></tr>" +
            "      </table>" +
            "    </div>" +
            "    <p>Te notificaremos cuando tu pedido sea despachado con el número de seguimiento.</p>" +
            "  </div>" +
            "  <div class='footer'>" +
            "    <p>© 2026 Syncro · Plataforma de Gestión Logística</p>" +
            "    <p>Este es un email automático, por favor no respondas a este mensaje.</p>" +
            "  </div>" +
            "</div>" +
            "</body></html>";
    }
}
