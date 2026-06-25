package com.Syncro.envios.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.Syncro.envios.model.Despacho;

public class DespachoTest {

    @Test
    void testPrePersist_setsDefaultValues() {
        Despacho d = new Despacho();
        d.prePersist();

        assertNotNull(d.getFechaCreacion());
        assertNotNull(d.getFechaActualizacion());
        assertEquals("PENDIENTE_RETIRO", d.getEstado());
        assertEquals("ESTANDAR", d.getTipoEnvio());
        assertEquals(BigDecimal.ZERO, d.getCostoEnvio());
        assertEquals("Chile", d.getDireccionPais());
    }

    @Test
    void testPrePersist_noSobreescribeValoresExistentes() {
        Despacho d = new Despacho();
        d.setEstado("DESPACHADO");
        d.setTipoEnvio("EXPRESS");
        d.setCostoEnvio(new BigDecimal("5000"));
        d.setDireccionPais("Argentina");
        d.prePersist();

        // Los valores que ya tenía NO deben sobreescribirse
        assertEquals("DESPACHADO", d.getEstado());
        assertEquals("EXPRESS", d.getTipoEnvio());
        assertEquals(new BigDecimal("5000"), d.getCostoEnvio());
        assertEquals("Argentina", d.getDireccionPais());
    }

    @Test
    void testPreUpdate_actualizaFechaActualizacion() {
        Despacho d = new Despacho();
        d.preUpdate();
        assertNotNull(d.getFechaActualizacion());
    }

}
