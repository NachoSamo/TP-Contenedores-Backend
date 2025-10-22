package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Representa una ruta logística completa, compuesta por varios tramos.
 */
@Entity
@Table(name = "rutas")
@Data
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuta;

    @Column(nullable = false)
    private Integer nroSolicitud; // FK hacia el microservicio de solicitudes

    private Integer cantTramos;
    private Integer cantDepositos;

    // Relación uno a muchos: una ruta tiene varios tramos
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
    private List<Tramo> tramos;
}
