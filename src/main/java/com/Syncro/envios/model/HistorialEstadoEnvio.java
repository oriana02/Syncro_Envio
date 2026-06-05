package com.Syncro.envios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstadoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "despacho_id", nullable = false)
    private Despacho despacho;

    @Column(name = "estado_anterior", length = 30)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private String estadoNuevo;

    @Column(length = 200)
    private String ubicacion;

    @Column(name = "actor_tipo", nullable = false, length = 20)
    private String actorTipo;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(length = 300)
    private String observacion;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @PrePersist
    public void prePersist() {
        fechaCambio = LocalDateTime.now();
        if (actorTipo == null) actorTipo = "SISTEMA";
    }
}
