package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Representa un tramo individual dentro de una ruta logística.
 */
@Entity
@Table(name = "tramos")
@Data
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTramo;

    private String origen;
    private String destino;
    private String tipoTramo; // terrestre, marítimo, etc.

    @Enumerated(EnumType.STRING)
    private EstadoTramo estadoTramo; // Enum con valores PREVISTO, EN_CURSO, COMPLETADO

    private Double costoEstimado;
    private Double costoFinal;
    private Double tiempoEstimado;
    private Double tiempoReal;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    // FK hacia el microservicio de flota
    private String dominioCamion;

    // FK hacia la ruta principal
    @ManyToOne
    @JoinColumn(name = "id_ruta")
    private Ruta ruta;
}
