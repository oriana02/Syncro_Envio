package com.Syncro.envios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
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
    private List<ItemEvento> items;

    @Data
    @NoArgsConstructor
    public static class ItemEvento {
        private String sku;
        private String nombre;
        private Integer cantidad;
    }
}
