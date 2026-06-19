package com.Syncro.envios.factory;

import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Fabrica concreta para envios de tipo MISMO_DIA.
 * Tiempo estimado: mismo dia antes de las 20:00 hrs.
 * Costo base: $14.990
 */
@Component
public class DespachoMismoDiaFactory implements DespachoFactory {

    private static final String TIPO = "MISMO_DIA";
    private static final BigDecimal COSTO_BASE = new BigDecimal("14990");

    @Override
    public Despacho crear(PedidoCreadoEvent evento) {
        return Despacho.builder()
                .pedidoId(evento.getPedidoId())
                .empresaId(evento.getEmpresaId())
                .destinatarioNombre(evento.getDestinatarioNombre() != null
                        ? evento.getDestinatarioNombre() : "Pendiente")
                .destinatarioEmail(evento.getDestinatarioEmail())
                .destinatarioTel(evento.getDestinatarioTel())
                .direccionCalle(evento.getDireccionCalle())
                .direccionNumero(evento.getDireccionNumero())
                .direccionDepto(evento.getDireccionDepto())
                .direccionCiudad(evento.getDireccionCiudad())
                .direccionRegion(evento.getDireccionRegion())
                .direccionPais(evento.getDireccionPais() != null
                        ? evento.getDireccionPais() : "Chile")
                .codigoPostal(evento.getCodigoPostal())
                .tipoEnvio(TIPO)
                .costoEnvio(COSTO_BASE)
                .build();
    }

    @Override
    public String getTipoEnvio() {
        return TIPO;
    }
}
