package com.Syncro.envios.service;

import com.Syncro.envios.dto.CostoEnvioRequest;
import com.Syncro.envios.dto.CostoEnvioResponse;
import com.Syncro.envios.exception.ResourceNotFoundException;
import com.Syncro.envios.model.TarifaEnvio;
import com.Syncro.envios.repository.TarifaEnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class CostoEnvioService {

    private final TarifaEnvioRepository tarifaRepository;

    public CostoEnvioResponse calcular(CostoEnvioRequest request) {
        TarifaEnvio tarifa = tarifaRepository
                .findFirstByTipoEnvioAndRegionDestinoAndActivoTrueAndPesoMinKgLessThanEqualAndPesoMaxKgGreaterThan(
                        request.getTipoEnvio(),
                        request.getRegionDestino(),
                        request.getPesoKg(),
                        request.getPesoKg())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay tarifa disponible para: " + request.getTipoEnvio() + " / " + request.getRegionDestino()));

        BigDecimal costo = tarifa.getCostoBase();
        if (request.getPesoKg().compareTo(tarifa.getPesoMinKg()) > 0) {
            BigDecimal exceso = request.getPesoKg().subtract(tarifa.getPesoMinKg());
            costo = costo.add(exceso.multiply(tarifa.getCostoKgExtra()));
        }

        log.info("Costo calculado: {} para {} kg en {}", costo, request.getPesoKg(), request.getRegionDestino());

        return CostoEnvioResponse.builder()
                .tipoEnvio(request.getTipoEnvio())
                .regionDestino(request.getRegionDestino())
                .pesoKg(request.getPesoKg())
                .costoTotal(costo)
                .transportista(tarifa.getTransportista().getNombre())
                .build();
    }
}
