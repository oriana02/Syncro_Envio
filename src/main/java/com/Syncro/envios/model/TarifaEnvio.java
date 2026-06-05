package com.Syncro.envios.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarifa_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id", nullable = false)
    private Transportista transportista;

    @Column(name = "tipo_envio", nullable = false, length = 30)
    private String tipoEnvio;

    @Column(name = "region_destino", nullable = false, length = 100)
    private String regionDestino;

    @Column(name = "peso_min_kg", nullable = false, precision = 8, scale = 3)
    private BigDecimal pesoMinKg;

    @Column(name = "peso_max_kg", nullable = false, precision = 8, scale = 3)
    private BigDecimal pesoMaxKg;

    @Column(name = "costo_base", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoBase;

    @Column(name = "costo_kg_extra", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoKgExtra;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        if (activo == null) activo = true;
        if (costoKgExtra == null) costoKgExtra = BigDecimal.ZERO;
    }
}
