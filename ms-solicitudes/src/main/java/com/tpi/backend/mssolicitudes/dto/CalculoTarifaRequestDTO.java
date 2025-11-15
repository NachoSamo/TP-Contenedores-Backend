package com.tpi.backend.mssolicitudes.dto;

import lombok.Data;

@Data
public class CalculoTarifaRequestDTO {
    private Long idContenedor;
    private Long idCamion;
    // Puedes agregar más campos según necesidad, como idRuta, volumen, peso, etc.
}

