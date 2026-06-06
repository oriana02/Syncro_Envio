package com.Syncro.envios.controller;

import com.Syncro.envios.dto.CostoEnvioRequest;
import com.Syncro.envios.dto.CostoEnvioResponse;
import com.Syncro.envios.dto.DespachoResponse;
import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;
import com.Syncro.envios.service.CostoEnvioService;
import com.Syncro.envios.service.DespachoService;
import com.Syncro.envios.service.EmailNotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnviosController.class)
class EnviosControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean DespachoService despachoService;
    @MockBean CostoEnvioService costoEnvioService;
    @MockBean EmailNotificacionService emailNotificacionService;

    private DespachoResponse mockDespachoResponse() {
        return DespachoResponse.builder()
            .id(1L).pedidoId(1L).empresaId(1L)
            .estado("PENDIENTE_RETIRO")
            .destinatarioNombre("Juan Perez")
            .destinatarioEmail("juan@test.cl")
            .direccionCalle("Av. Providencia")
            .direccionCiudad("Santiago")
            .direccionRegion("Region Metropolitana")
            .tipoEnvio("ESTANDAR")
            .costoEnvio(BigDecimal.ZERO)
            .fechaCreacion(LocalDateTime.now())
            .historial(List.of())
            .build();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /envios/pedido/{id} - retorna 200")
    void obtenerPorPedido_ok() throws Exception {
        when(despachoService.obtenerPorPedidoId(1L)).thenReturn(mockDespachoResponse());

        mockMvc.perform(get("/envios/pedido/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pedidoId").value(1))
            .andExpect(jsonPath("$.estado").value("PENDIENTE_RETIRO"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /envios/empresa/{id} - retorna lista")
    void obtenerPorEmpresa_ok() throws Exception {
        when(despachoService.obtenerPorEmpresa(1L)).thenReturn(List.of(mockDespachoResponse()));

        mockMvc.perform(get("/envios/empresa/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /envios/costo - retorna costo calculado")
    void calcularCosto_ok() throws Exception {
        CostoEnvioRequest req = new CostoEnvioRequest();
        req.setTipoEnvio("ESTANDAR");
        req.setRegionDestino("Region Metropolitana");
        req.setPesoKg(new BigDecimal("2"));

        CostoEnvioResponse resp = CostoEnvioResponse.builder()
            .tipoEnvio("ESTANDAR")
            .regionDestino("Region Metropolitana")
            .pesoKg(new BigDecimal("2"))
            .costoTotal(new BigDecimal("3990"))
            .transportista("Starken")
            .build();

        when(costoEnvioService.calcular(any())).thenReturn(resp);

        mockMvc.perform(post("/envios/costo").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.costoTotal").value(3990));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /envios/test/evento - procesa evento y retorna despachoId")
    void testEvento_ok() throws Exception {
        PedidoCreadoEvent evento = new PedidoCreadoEvent();
        evento.setPedidoId(5L);
        evento.setEmpresaId(1L);
        evento.setDestinatarioNombre("Ana Lopez");
        evento.setDestinatarioEmail("ana@test.cl");
        evento.setDireccionCalle("Calle Falsa");
        evento.setDireccionCiudad("Santiago");
        evento.setDireccionRegion("Region Metropolitana");
        evento.setDireccionPais("Chile");

        Despacho despacho = Despacho.builder()
            .id(10L).pedidoId(5L).empresaId(1L)
            .estado("PENDIENTE_RETIRO")
            .destinatarioNombre("Ana Lopez")
            .destinatarioEmail("ana@test.cl")
            .direccionCalle("Calle Falsa")
            .direccionCiudad("Santiago")
            .direccionRegion("Region Metropolitana")
            .direccionPais("Chile")
            .tipoEnvio("ESTANDAR")
            .costoEnvio(BigDecimal.ZERO)
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        when(despachoService.crearDesdeEvento(any())).thenReturn(despacho);
        doNothing().when(emailNotificacionService).enviarConfirmacionPedido(any(), any(), any());

        mockMvc.perform(post("/envios/test/evento").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(evento)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.despachoId").value(10))
            .andExpect(jsonPath("$.pedidoId").value(5));
    }
}