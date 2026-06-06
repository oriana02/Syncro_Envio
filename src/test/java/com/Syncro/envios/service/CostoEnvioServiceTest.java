package com.Syncro.envios.service;

import com.Syncro.envios.dto.CostoEnvioRequest;
import com.Syncro.envios.dto.CostoEnvioResponse;
import com.Syncro.envios.exception.ResourceNotFoundException;
import com.Syncro.envios.model.TarifaEnvio;
import com.Syncro.envios.model.Transportista;
import com.Syncro.envios.repository.TarifaEnvioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CostoEnvioServiceTest {

    @Mock TarifaEnvioRepository tarifaRepository;
    @InjectMocks CostoEnvioService costoEnvioService;

    @Test
    @DisplayName("calcular - retorna costo correctamente")
    void calcular_ok() {
        Transportista t = Transportista.builder()
            .id(1L).nombre("Starken").codigo("STARKEN").activo(true).build();

        TarifaEnvio tarifa = TarifaEnvio.builder()
            .id(1L)
            .transportista(t)
            .tipoEnvio("ESTANDAR")
            .regionDestino("Region Metropolitana")
            .pesoMinKg(BigDecimal.ZERO)
            .pesoMaxKg(new BigDecimal("5"))
            .costoBase(new BigDecimal("3990"))
            .costoKgExtra(new BigDecimal("500"))
            .activo(true)
            .build();

        when(tarifaRepository
            .findFirstByTipoEnvioAndRegionDestinoAndActivoTrueAndPesoMinKgLessThanEqualAndPesoMaxKgGreaterThan(
                anyString(), anyString(), any(), any()))
            .thenReturn(Optional.of(tarifa));

        CostoEnvioRequest request = new CostoEnvioRequest();
        request.setTipoEnvio("ESTANDAR");
        request.setRegionDestino("Region Metropolitana");
        request.setPesoKg(new BigDecimal("2"));

        CostoEnvioResponse result = costoEnvioService.calcular(request);

        assertThat(result).isNotNull();
        assertThat(result.getCostoTotal()).isEqualByComparingTo(new BigDecimal("4990"));
        assertThat(result.getTransportista()).isEqualTo("Starken");
    }

    @Test
    @DisplayName("calcular - lanza excepcion si no hay tarifa")
    void calcular_sinTarifa() {
        when(tarifaRepository
            .findFirstByTipoEnvioAndRegionDestinoAndActivoTrueAndPesoMinKgLessThanEqualAndPesoMaxKgGreaterThan(
                anyString(), anyString(), any(), any()))
            .thenReturn(Optional.empty());

        CostoEnvioRequest request = new CostoEnvioRequest();
        request.setTipoEnvio("EXPRESS");
        request.setRegionDestino("Region de Atacama");
        request.setPesoKg(new BigDecimal("3"));

        assertThatThrownBy(() -> costoEnvioService.calcular(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("calcular - aplica costo por kg extra")
    void calcular_conKgExtra() {
        Transportista t = Transportista.builder()
            .id(1L).nombre("Starken").codigo("STARKEN").activo(true).build();

        TarifaEnvio tarifa = TarifaEnvio.builder()
            .id(1L)
            .transportista(t)
            .tipoEnvio("ESTANDAR")
            .regionDestino("Region Metropolitana")
            .pesoMinKg(new BigDecimal("5"))
            .pesoMaxKg(new BigDecimal("20"))
            .costoBase(new BigDecimal("5990"))
            .costoKgExtra(new BigDecimal("400"))
            .activo(true)
            .build();

        when(tarifaRepository
            .findFirstByTipoEnvioAndRegionDestinoAndActivoTrueAndPesoMinKgLessThanEqualAndPesoMaxKgGreaterThan(
                anyString(), anyString(), any(), any()))
            .thenReturn(Optional.of(tarifa));

        CostoEnvioRequest request = new CostoEnvioRequest();
        request.setTipoEnvio("ESTANDAR");
        request.setRegionDestino("Region Metropolitana");
        request.setPesoKg(new BigDecimal("8"));

        CostoEnvioResponse result = costoEnvioService.calcular(request);

        BigDecimal esperado = new BigDecimal("5990").add(new BigDecimal("3").multiply(new BigDecimal("400")));
        assertThat(result.getCostoTotal()).isEqualByComparingTo(esperado);
    }
}