package com.Syncro.envios.service;

import com.Syncro.envios.dto.ActualizarEstadoRequest;
import com.Syncro.envios.dto.DespachoResponse;
import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.exception.ResourceNotFoundException;
import com.Syncro.envios.model.Despacho;
import com.Syncro.envios.model.HistorialEstadoEnvio;
import com.Syncro.envios.repository.DespachoRepository;
import com.Syncro.envios.repository.HistorialEstadoEnvioRepository;
import com.Syncro.envios.factory.DespachoFactorySelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final HistorialEstadoEnvioRepository historialRepository;
    private final DespachoFactorySelector factorySelector;

    @Transactional
    public Despacho crearDesdeEvento(PedidoCreadoEvent evento) {
        if (despachoRepository.existsByPedidoId(evento.getPedidoId())) {
            log.warn("Despacho ya existe para pedidoId={}", evento.getPedidoId());
            return despachoRepository.findByPedidoId(evento.getPedidoId()).get();
        }

        // Factory Method: selecciona la fabrica segun el tipo de envio del evento
        String tipoEnvio = evento.getTipoEnvio() != null ? evento.getTipoEnvio() : "ESTANDAR";
        Despacho despacho = factorySelector.seleccionar(tipoEnvio).crear(evento);

        Despacho guardado = despachoRepository.save(despacho);
        log.info("Despacho creado id={} para pedidoId={} tipo={}",
                guardado.getId(), guardado.getPedidoId(), guardado.getTipoEnvio());
        return guardado;
    }

    @Transactional
    public DespachoResponse actualizarEstado(Long despachoId, ActualizarEstadoRequest request) {
        Despacho despacho = despachoRepository.findById(despachoId)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado: " + despachoId));

        String estadoAnterior = despacho.getEstado();
        despacho.setEstado(request.getEstado());
        despachoRepository.save(despacho);

        HistorialEstadoEnvio historial = HistorialEstadoEnvio.builder()
                .despacho(despacho)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(request.getEstado())
                .observacion(request.getObservacion())
                .ubicacion(request.getUbicacion())
                .actorTipo("USUARIO")
                .build();
        historialRepository.save(historial);

        return toResponse(despacho);
    }

    @Transactional(readOnly = true)
    public DespachoResponse obtenerPorPedidoId(Long pedidoId) {
        Despacho despacho = despachoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado para pedido: " + pedidoId));
        return toResponse(despacho);
    }

    @Transactional(readOnly = true)
    public List<DespachoResponse> obtenerPorEmpresa(Long empresaId) {
        return despachoRepository.findByEmpresaId(empresaId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private DespachoResponse toResponse(Despacho d) {
        List<DespachoResponse.HistorialResponse> historial = historialRepository
                .findByDespachoIdOrderByFechaCambioAsc(d.getId())
                .stream()
                .map(h -> DespachoResponse.HistorialResponse.builder()
                        .estadoAnterior(h.getEstadoAnterior())
                        .estadoNuevo(h.getEstadoNuevo())
                        .actorTipo(h.getActorTipo())
                        .fechaCambio(h.getFechaCambio())
                        .observacion(h.getObservacion())
                        .build())
                .collect(Collectors.toList());

        return DespachoResponse.builder()
                .id(d.getId())
                .pedidoId(d.getPedidoId())
                .empresaId(d.getEmpresaId())
                .estado(d.getEstado())
                .destinatarioNombre(d.getDestinatarioNombre())
                .destinatarioEmail(d.getDestinatarioEmail())
                .direccionCalle(d.getDireccionCalle())
                .direccionNumero(d.getDireccionNumero())
                .direccionCiudad(d.getDireccionCiudad())
                .direccionRegion(d.getDireccionRegion())
                .tipoEnvio(d.getTipoEnvio())
                .pesoKg(d.getPesoKg())
                .costoEnvio(d.getCostoEnvio())
                .fechaCreacion(d.getFechaCreacion())
                .fechaDespacho(d.getFechaDespacho())
                .fechaEntrega(d.getFechaEntrega())
                .historial(historial)
                .build();
    }
}
