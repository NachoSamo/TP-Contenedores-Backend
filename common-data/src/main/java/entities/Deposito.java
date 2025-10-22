package entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Representa un depósito intermedio o final dentro de una ruta.
 */
@Entity
@Table(name = "depositos")
@Data
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDeposito;

    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;

    private Double costoPorDia;
    private Boolean activo = true;

    // FK hacia la ruta
    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;
}
