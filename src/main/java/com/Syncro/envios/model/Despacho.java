package com.Syncro.envios.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "despacho")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "destinatario_nombre", nullable = true, length = 150)
    private String destinatarioNombre;

    @Column(name = "destinatario_email", length = 150)
    private String destinatarioEmail;

    @Column(name = "destinatario_tel", length = 20)
    private String destinatarioTel;

    @Column(name = "direccion_calle", nullable = false, length = 200)
    private String direccionCalle;

    @Column(name = "direccion_numero", length = 20)
    private String direccionNumero;

    @Column(name = "direccion_depto", length = 30)
    private String direccionDepto;

    @Column(name = "direccion_ciudad", nullable = false, length = 100)
    private String direccionCiudad;

    @Column(name = "direccion_region", nullable = false, length = 100)
    private String direccionRegion;

    @Column(name = "direccion_pais", nullable = false, length = 60)
    private String direccionPais;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "tipo_envio", nullable = false, length = 30)
    private String tipoEnvio;

    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "costo_envio", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoEnvio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) estado = "PENDIENTE_RETIRO";
        if (tipoEnvio == null) tipoEnvio = "ESTANDAR";
        if (costoEnvio == null) costoEnvio = BigDecimal.ZERO;
        if (direccionPais == null) direccionPais = "Chile";
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
