package com.Syncro.envios.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificacionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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
            ObjectNode from = objectMapper.createObjectNode();
            from.put("email", fromEmail);
            from.put("name", fromName);

            ObjectNode to = objectMapper.createObjectNode();
            to.put("email", destinatarioEmail);
            to.put("name", destinatarioNombre);
            ArrayNode toArray = objectMapper.createArrayNode();
            toArray.add(to);

            ObjectNode payload = objectMapper.createObjectNode();
            payload.set("from", from);
            payload.set("to", toArray);
            payload.put("subject", "Tu pedido " + pedidoId + " fue confirmado - Syncro");
            payload.put("text", "Hola " + destinatarioNombre + ", tu pedido " + pedidoId + " fue confirmado y esta siendo preparado.");
            payload.put("html", "<h2>Hola " + destinatarioNombre + "</h2><p>Pedido <strong>" + pedidoId + "</strong> confirmado y en preparacion.</p>");

            String body = objectMapper.writeValueAsString(payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.mailersend.com/v1/email", request, String.class);
            log.info("Email enviado a {} para pedido {} status: {}", destinatarioEmail, pedidoId, response.getStatusCode());
        } catch (Exception e) {
            log.error("Error enviando email para pedido {}: {}", pedidoId, e.getMessage());
        }
    }
}