package com.Syncro.envios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {
    @NotBlank
    private String estado;
    private String observacion;
    private String ubicacion;
}
