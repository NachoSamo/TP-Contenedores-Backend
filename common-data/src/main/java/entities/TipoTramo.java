package entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "tipo_tramo")
@Data
public class TipoTramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tramo")
    private Integer idTipoTramo; // PK (int), autogenerada

    @Column(name = "nombre_tipo")
    private String nombreTipo; // Columna "descripcion" del DER

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tramo")
    private Tramo tramo;


}
