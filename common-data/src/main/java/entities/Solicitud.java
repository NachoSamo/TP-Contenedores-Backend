package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nro_solicitud")
    private Integer nroSolicitud; // PK (int), autogenerada

    @Column(name = "costo_estimado")
    private Float costoEstimado; // Tipo "float" en el DER

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado; // Tipo "int" en el DER

    @Column(name = "costo_real")
    private Float costoReal; // Tipo "float" en el DER

    @Column(name = "tiempo_real")
    private Integer tiempoReal; // Tipo "int" en el DER

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; // Tipo "DateTime" en el DER

    // Relación "realiza" (inversa): Muchas Solicitudes son de un Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false) // FK (int)
    private Cliente cliente;

    // Relación "asociado_a" (inversa): Muchas Solicitudes para un Contenedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contenedor", nullable = false) // FK (int)
    private Contenedor contenedor;

    // Relación "estado" (inversa): Muchas Solicitudes tienen un Estado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false) // FK (int)
    private Estado estado;
}