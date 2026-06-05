package com.Syncro.envios.event;

import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;
import com.Syncro.envios.service.DespachoService;
import com.Syncro.envios.service.EmailNotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PedidoCreadoConsumer {

    private final DespachoService despachoService;
    private final EmailNotificacionService emailNotificacionService;

    @RabbitListener(queues = "${rabbitmq.queue.envio}")
    public void consumir(PedidoCreadoEvent evento) {
        log.info("Evento recibido para pedidoId={}", evento.getPedidoId());

        Despacho despacho = despachoService.crearDesdeEvento(evento);

        emailNotificacionService.enviarConfirmacionPedido(
                evento.getDestinatarioEmail(),
                evento.getDestinatarioNombre(),
                evento.getPedidoId()
        );

        log.info("Despacho id={} creado y email enviado para pedidoId={}",
                despacho.getId(), evento.getPedidoId());
    }
}