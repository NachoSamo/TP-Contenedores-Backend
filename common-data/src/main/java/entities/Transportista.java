package entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * Representa al transportista o chofer responsable de uno o varios camiones.
 */
@Entity
@Table(name = "transportistas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transportista")
    private Integer idTransportista; // PK (int)

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

    @Column(nullable = false)
    private String estado; // Ej: "disponible", "en_ruta", "inactivo"

    // 🔹 Relación: un transportista puede tener varios camiones
    @OneToMany(mappedBy = "transportista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Camion> camiones;
}
