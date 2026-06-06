package com.Syncro.envios.controller;

import com.Syncro.envios.dto.ActualizarEstadoRequest;
import com.Syncro.envios.dto.CostoEnvioRequest;
import com.Syncro.envios.dto.CostoEnvioResponse;
import com.Syncro.envios.dto.DespachoResponse;
import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;
import com.Syncro.envios.service.CostoEnvioService;
import com.Syncro.envios.service.DespachoService;
import com.Syncro.envios.service.EmailNotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
@Tag(name = "Envios", description = "Gestion de despachos y envios")
public class EnviosController {

    private final DespachoService despachoService;
    private final CostoEnvioService costoEnvioService;
    private final EmailNotificacionService emailNotificacionService;

    @Operation(summary = "Obtener despacho por pedido")
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<DespachoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(despachoService.obtenerPorPedidoId(pedidoId));
    }

    @Operation(summary = "Listar despachos por empresa")
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<DespachoResponse>> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(despachoService.obtenerPorEmpresa(empresaId));
    }

    @Operation(summary = "Actualizar estado del despacho")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, request));
    }

    @Operation(summary = "Calcular costo de envio")
    @PostMapping("/costo")
    public ResponseEntity<CostoEnvioResponse> calcularCosto(
            @Valid @RequestBody CostoEnvioRequest request) {
        return ResponseEntity.ok(costoEnvioService.calcular(request));
    }

    @Operation(
        summary = "[TEST] Simular evento pedido.creado",
        description = "Simula la recepcion del evento RabbitMQ pedido.creado. Crea el despacho y envia email de confirmacion al destinatario. Util para demostrar el flujo EDA sin necesitar RabbitMQ activo."
    )
    @PostMapping("/test/evento")
    public ResponseEntity<Map<String, Object>> testEvento(@RequestBody PedidoCreadoEvent evento) {
        Despacho despacho = despachoService.crearDesdeEvento(evento);
        emailNotificacionService.enviarConfirmacionPedido(
            evento.getDestinatarioEmail(),
            evento.getDestinatarioNombre(),
            evento.getPedidoId()
        );
        return ResponseEntity.ok(Map.of(
            "mensaje", "Evento procesado correctamente",
            "despachoId", despacho.getId(),
            "pedidoId", despacho.getPedidoId(),
            "estado", despacho.getEstado(),
            "emailEnviado", evento.getDestinatarioEmail() != null ? evento.getDestinatarioEmail() : "sin email"
        ));
    }
}