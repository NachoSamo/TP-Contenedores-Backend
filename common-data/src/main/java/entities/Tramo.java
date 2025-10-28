package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramos")
@Data
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramo")
    private Long idTramo;

    // Relación con Ruta (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    // Relaciones geográficas (origen y destino)
    @Column(name = "origen_geo")
    private Integer origenGeo; // FK a tabla Geografía (si existe en otro microservicio)

    @Column(name = "destino_geo")
    private Integer destinoGeo;

    // Depósitos asociados
    @Column(name = "origen_deposito_id")
    private Integer origenDepositoId;

    @Column(name = "destino_deposito_id")
    private Integer destinoDepositoId;

    // Tipo de tramo
    @Column(name = "tipo_tramo")
    private Integer tipoTramo; // FK al catálogo de tipos de tramo (tabla referencial)

    // Estado actual del tramo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    private Estado estado;

    @Column(name = "orden")
    private Integer orden;

    // Fechas previstas
    @Column(name = "fechahora_inicio_estimada")
    private LocalDateTime fechaHoraInicioEstimada;

    @Column(name = "fechahora_fin_estimada")
    private LocalDateTime fechaHoraFinEstimada;

    // Fechas reales
    @Column(name = "fechahora_inicio_real")
    private LocalDateTime fechaHoraInicioReal;

    @Column(name = "fechahora_fin_real")
    private LocalDateTime fechaHoraFinReal;

    // Costos
    @Column(name = "costo_aproximado")
    private Float costoAproximado;

    @Column(name = "costo_real")
    private Float costoReal;

    // Relación lógica con microservicio Flota
    @Column(name = "dominio_camion", length = 15)
    private String dominioCamion;
}
