package com.Syncro.envios.factory;

import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;

/**
 * Interfaz base del patron Factory Method.
 * Define el contrato para la creacion de despachos segun el tipo de envio.
 * Cada implementacion concreta decide como construir el Despacho.
 */
public interface DespachoFactory {

    /**
     * Metodo fabrica: crea un Despacho a partir del evento recibido.
     *
     * @param evento datos del pedido confirmado recibido desde RabbitMQ
     * @return Despacho construido segun el tipo de envio
     */
    Despacho crear(PedidoCreadoEvent evento);

    /**
     * Retorna el tipo de envio que maneja esta fabrica.
     *
     * @return String con el tipo de envio (ESTANDAR, EXPRESS, MISMO_DIA)
     */
    String getTipoEnvio();
}
