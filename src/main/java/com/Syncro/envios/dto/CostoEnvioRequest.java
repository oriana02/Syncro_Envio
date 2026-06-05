package com.Syncro.envios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CostoEnvioRequest {
    @NotBlank
    private String tipoEnvio;
    @NotBlank
    private String regionDestino;
    @NotNull
    @Positive
    private BigDecimal pesoKg;
}
