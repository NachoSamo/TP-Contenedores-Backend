package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "estados")
@Data
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado; // PK (int), autogenerada

    @Column(nullable = false)
    private String contexto; // Columna "contexto" del DER

    @Column
    private String descripcion; // Columna "descripcion" del DER

    // Relación: Un Estado puede estar en muchos ContenedTenedores
    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    private List<Contenedor> contenedores;

    // Relación: Un Estado puede estar en muchas Solicitudes
    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    private List<Solicitud> solicitudes;
}