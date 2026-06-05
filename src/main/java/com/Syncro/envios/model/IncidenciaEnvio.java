package com.Syncro.envios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencia_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "despacho_id", nullable = false)
    private Despacho despacho;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "reportado_por")
    private Long reportadoPor;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @PrePersist
    public void prePersist() {
        fechaReporte = LocalDateTime.now();
        if (estado == null) estado = "ABIERTA";
    }
}
