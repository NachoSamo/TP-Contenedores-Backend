package entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un camión dentro de la flota.
 * Cada camión puede estar asignado a un transportista y tener una tarifa base asociada.
 */
@Entity
@Table(name = "camion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patente;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private Double capacidadToneladas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportista_id")
    private Transportista transportista;
}
