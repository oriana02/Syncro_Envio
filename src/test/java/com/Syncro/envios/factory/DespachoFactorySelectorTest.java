package com.Syncro.envios.factory;

import com.Syncro.envios.dto.PedidoCreadoEvent;
import com.Syncro.envios.model.Despacho;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class DespachoFactorySelectorTest {

    private DespachoFactorySelector selector;

    private PedidoCreadoEvent eventoBase() {
        PedidoCreadoEvent e = new PedidoCreadoEvent();
        e.setPedidoId(1L);
        e.setEmpresaId(1L);
        e.setDestinatarioNombre("Oriana Test");
        e.setDestinatarioEmail("test@syncro.cl");
        e.setDireccionCalle("Av. Providencia");
        e.setDireccionNumero("1234");
        e.setDireccionCiudad("Santiago");
        e.setDireccionRegion("Region Metropolitana");
        e.setDireccionPais("Chile");
        return e;
    }

    @BeforeEach
    void setUp() {
        selector = new DespachoFactorySelector(List.of(
                new DespachoEstandarFactory(),
                new DespachoExpressFactory(),
                new DespachoMismoDiaFactory()
        ));
    }

    @Test
    @DisplayName("Factory ESTANDAR - crea despacho con costo 3990")
    void factoryEstandar_creaDespachoConCostoCorrecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setTipoEnvio("ESTANDAR");

        Despacho despacho = selector.seleccionar("ESTANDAR").crear(evento);

        assertThat(despacho).isNotNull();
        assertThat(despacho.getTipoEnvio()).isEqualTo("ESTANDAR");
        assertThat(despacho.getCostoEnvio()).isEqualByComparingTo("3990");
        assertThat(despacho.getPedidoId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Factory EXPRESS - crea despacho con costo 7990")
    void factoryExpress_creaDespachoConCostoCorrecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setTipoEnvio("EXPRESS");

        Despacho despacho = selector.seleccionar("EXPRESS").crear(evento);

        assertThat(despacho.getTipoEnvio()).isEqualTo("EXPRESS");
        assertThat(despacho.getCostoEnvio()).isEqualByComparingTo("7990");
    }

    @Test
    @DisplayName("Factory MISMO_DIA - crea despacho con costo 14990")
    void factoryMismoDia_creaDespachoConCostoCorrecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setTipoEnvio("MISMO_DIA");

        Despacho despacho = selector.seleccionar("MISMO_DIA").crear(evento);

        assertThat(despacho.getTipoEnvio()).isEqualTo("MISMO_DIA");
        assertThat(despacho.getCostoEnvio()).isEqualByComparingTo("14990");
    }

    @Test
    @DisplayName("Factory con tipo null - usa ESTANDAR por defecto")
    void factoryConTipoNull_usaEstandarPorDefecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setTipoEnvio(null);

        Despacho despacho = selector.seleccionar(null).crear(evento);

        assertThat(despacho.getTipoEnvio()).isEqualTo("ESTANDAR");
    }

    @Test
    @DisplayName("Factory con tipo desconocido - usa ESTANDAR por defecto")
    void factoryConTipoDesconocido_usaEstandarPorDefecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setTipoEnvio("SUPER_RAPIDO");

        Despacho despacho = selector.seleccionar("SUPER_RAPIDO").crear(evento);

        assertThat(despacho.getTipoEnvio()).isEqualTo("ESTANDAR");
    }

    @Test
    @DisplayName("Factory - destinatario null usa valor por defecto Pendiente")
    void factory_destinatarioNullUsaValorPorDefecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setDestinatarioNombre(null);

        Despacho despacho = selector.seleccionar("ESTANDAR").crear(evento);

        assertThat(despacho.getDestinatarioNombre()).isEqualTo("Pendiente");
    }

    @Test
    @DisplayName("Factory - pais null usa Chile por defecto")
    void factory_paisNullUsaChilePorDefecto() {
        PedidoCreadoEvent evento = eventoBase();
        evento.setDireccionPais(null);

        Despacho despacho = selector.seleccionar("ESTANDAR").crear(evento);

        assertThat(despacho.getDireccionPais()).isEqualTo("Chile");
    }
}
