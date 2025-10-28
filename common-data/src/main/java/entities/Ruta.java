package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Representa una ruta logística completa, compuesta por varios tramos
 * y asociada a una solicitud específica de transporte.
 */
@Entity
@Table(name = "rutas")
@Data
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta; // PK autoincremental

    // 🔹 Relación con Solicitud (FK)
    @Column(name = "nro_solicitud", nullable = false)
    private Integer nroSolicitud; // FK hacia el microservicio de solicitudes

    // 🔹 Atributos propios de la ruta
    @Column(name = "cant_tramos")
    private Integer cantTramos;

    @Column(name = "cant_depositos")
    private Integer cantDepositos;

    // 🔹 Relación Uno a Muchos → una Ruta tiene varios Tramos
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tramo> tramos;
}
