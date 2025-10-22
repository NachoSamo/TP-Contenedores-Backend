package com.tpi.backend.msflota.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa a un transportista o chofer responsable de uno o varios camiones.
 */
@Entity
@Table(name = "transportista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String licencia;

    // Estado actual del transportista (disponible, en ruta, inactivo)
    @Column(nullable = false)
    private String estado;
}
