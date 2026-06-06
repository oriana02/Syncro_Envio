package com.Syncro.envios.service;

import com.Syncro.envios.dto.ActualizarEstadoRequest;
import com.Syncro.envios.dto.DespachoResponse;
import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.exception.ResourceNotFoundException;
import com.Syncro.envios.model.Despacho;
import com.Syncro.envios.model.HistorialEstadoEnvio;
import com.Syncro.envios.repository.DespachoRepository;
import com.Syncro.envios.repository.HistorialEstadoEnvioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

    @Mock DespachoRepository despachoRepository;
    @Mock HistorialEstadoEnvioRepository historialRepository;
    @InjectMocks DespachoService despachoService;

    private PedidoCreadoEvent evento;
    private Despacho despacho;

    @BeforeEach
    void setUp() {
        evento = new PedidoCreadoEvent();
        evento.setPedidoId(1L);
        evento.setEmpresaId(1L);
        evento.setDestinatarioNombre("Juan Perez");
        evento.setDestinatarioEmail("juan@test.cl");
        evento.setDireccionCalle("Av. Providencia");
        evento.setDireccionNumero("1234");
        evento.setDireccionCiudad("Santiago");
        evento.setDireccionRegion("Region Metropolitana");
        evento.setDireccionPais("Chile");

        despacho = Despacho.builder()
            .id(1L)
            .pedidoId(1L)
            .empresaId(1L)
            .destinatarioNombre("Juan Perez")
            .destinatarioEmail("juan@test.cl")
            .direccionCalle("Av. Providencia")
            .direccionNumero("1234")
            .direccionCiudad("Santiago")
            .direccionRegion("Region Metropolitana")
            .direccionPais("Chile")
            .estado("PENDIENTE_RETIRO")
            .tipoEnvio("ESTANDAR")
            .costoEnvio(BigDecimal.ZERO)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("crearDesdeEvento - crea despacho correctamente")
    void crearDesdeEvento_ok() {
        when(despachoRepository.existsByPedidoId(1L)).thenReturn(false);
        when(despachoRepository.save(any())).thenReturn(despacho);

        Despacho result = despachoService.crearDesdeEvento(evento);

        assertThat(result).isNotNull();
        assertThat(result.getPedidoId()).isEqualTo(1L);
        assertThat(result.getDestinatarioNombre()).isEqualTo("Juan Perez");
        verify(despachoRepository).save(any());
    }

    @Test
    @DisplayName("crearDesdeEvento - si ya existe retorna el existente")
    void crearDesdeEvento_yaExiste() {
        when(despachoRepository.existsByPedidoId(1L)).thenReturn(true);
        when(despachoRepository.findByPedidoId(1L)).thenReturn(Optional.of(despacho));

        Despacho result = despachoService.crearDesdeEvento(evento);

        assertThat(result.getPedidoId()).isEqualTo(1L);
        verify(despachoRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerPorPedidoId - retorna despacho existente")
    void obtenerPorPedidoId_ok() {
        when(despachoRepository.findByPedidoId(1L)).thenReturn(Optional.of(despacho));
        when(historialRepository.findByDespachoIdOrderByFechaCambioAsc(1L)).thenReturn(List.of());

        DespachoResponse result = despachoService.obtenerPorPedidoId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPedidoId()).isEqualTo(1L);
        assertThat(result.getDestinatarioNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("obtenerPorPedidoId - lanza excepcion si no existe")
    void obtenerPorPedidoId_notFound() {
        when(despachoRepository.findByPedidoId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> despachoService.obtenerPorPedidoId(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerPorEmpresa - retorna lista")
    void obtenerPorEmpresa_ok() {
        when(despachoRepository.findByEmpresaId(1L)).thenReturn(List.of(despacho));
        when(historialRepository.findByDespachoIdOrderByFechaCambioAsc(1L)).thenReturn(List.of());

        List<DespachoResponse> result = despachoService.obtenerPorEmpresa(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpresaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("actualizarEstado - cambia estado y registra historial")
    void actualizarEstado_ok() {
        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("EN_PREPARACION");
        request.setObservacion("Preparando paquete");

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        when(despachoRepository.save(any())).thenReturn(despacho);
        when(historialRepository.findByDespachoIdOrderByFechaCambioAsc(1L)).thenReturn(List.of());

        DespachoResponse result = despachoService.actualizarEstado(1L, request);

        assertThat(result).isNotNull();
        verify(historialRepository).save(any(HistorialEstadoEnvio.class));
        verify(despachoRepository).save(any(Despacho.class));
    }

    @Test
    @DisplayName("actualizarEstado - lanza excepcion si despacho no existe")
    void actualizarEstado_notFound() {
        ActualizarEstadoRequest request = new ActualizarEstadoRequest();
        request.setEstado("EN_PREPARACION");

        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> despachoService.actualizarEstado(99L, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}