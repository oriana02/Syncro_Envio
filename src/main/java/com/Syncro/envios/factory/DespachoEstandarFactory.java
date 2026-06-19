package com.Syncro.envios.factory;

import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Fabrica concreta para envios de tipo ESTANDAR.
 * Tiempo estimado: 3 a 5 dias habiles.
 * Costo base: $3.990
 */
@Component
public class DespachoEstandarFactory implements DespachoFactory {

    private static final String TIPO = "ESTANDAR";
    private static final BigDecimal COSTO_BASE = new BigDecimal("3990");

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
