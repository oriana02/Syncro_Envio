package com.Syncro.envios.dto;

import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // Ignorar campos desconocidos al deserializar
public class PedidoCreadoEvent {

    private Long pedidoId;
    private Long empresaId;
    private String destinatarioNombre;
    private String destinatarioEmail;
    private String destinatarioTel;
    private String direccionCalle;
    private String direccionNumero;
    private String direccionDepto;
    private String direccionCiudad;
    private String direccionRegion;
    private String direccionPais;
    private String codigoPostal;
    private String tipoEnvio;
    private List<ItemEvento> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemEvento {

        private String sku;
        private String nombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
    }

}
