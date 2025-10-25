package entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una tarifa aplicable a un traslado.
 * Puede depender del tipo de contenedor, la distancia y el peso total.
 */
@Entity
@Table(name = "tarifa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de contenedor (20 pies, 40 pies, refrigerado, etc.)
    @Column(nullable = false)
    private String tipoContenedor;

    // Precio base en pesos argentinos
    @Column(nullable = false)
    private Double precioBase;

    // Precio por kilómetro
    @Column(nullable = false)
    private Double precioPorKm;

    // Precio por tonelada adicional
    @Column(nullable = false)
    private Double precioPorTonelada;
}
