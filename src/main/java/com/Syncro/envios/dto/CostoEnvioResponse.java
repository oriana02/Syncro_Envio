package com.Syncro.envios.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CostoEnvioResponse {
    private String tipoEnvio;
    private String regionDestino;
    private BigDecimal pesoKg;
    private BigDecimal costoTotal;
    private String transportista;
}
