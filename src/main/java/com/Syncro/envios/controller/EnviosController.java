package com.Syncro.envios.controller;

import com.Syncro.envios.dto.ActualizarEstadoRequest;
import com.Syncro.envios.dto.CostoEnvioRequest;
import com.Syncro.envios.dto.CostoEnvioResponse;
import com.Syncro.envios.dto.DespachoResponse;
import com.Syncro.envios.service.CostoEnvioService;
import com.Syncro.envios.service.DespachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
public class EnviosController {

    private final DespachoService despachoService;
    private final CostoEnvioService costoEnvioService;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<DespachoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(despachoService.obtenerPorPedidoId(pedidoId));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<DespachoResponse>> obtenerPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(despachoService.obtenerPorEmpresa(empresaId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, request));
    }

    @PostMapping("/costo")
    public ResponseEntity<CostoEnvioResponse> calcularCosto(
            @Valid @RequestBody CostoEnvioRequest request) {
        return ResponseEntity.ok(costoEnvioService.calcular(request));
    }
}