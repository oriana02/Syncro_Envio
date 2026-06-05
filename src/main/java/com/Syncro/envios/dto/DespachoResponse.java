package com.Syncro.envios.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DespachoResponse {
    private Long id;
    private Long pedidoId;
    private Long empresaId;
    private String estado;
    private String destinatarioNombre;
    private String destinatarioEmail;
    private String direccionCalle;
    private String direccionNumero;
    private String direccionCiudad;
    private String direccionRegion;
    private String tipoEnvio;
    private BigDecimal pesoKg;
    private BigDecimal costoEnvio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
    private List<HistorialResponse> historial;

    @Data
    @Builder
    public static class HistorialResponse {
        private String estadoAnterior;
        private String estadoNuevo;
        private String actorTipo;
        private LocalDateTime fechaCambio;
        private String observacion;
    }
}
