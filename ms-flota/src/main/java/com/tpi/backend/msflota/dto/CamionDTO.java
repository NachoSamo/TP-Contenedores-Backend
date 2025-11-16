package com.tpi.backend.msflota.dto;

import lombok.Data;

@Data
public class CamionDTO {
    private String dominioCamion;
    private String modelo;
    private Float capacidadKg;
    private Float volumenM3;
    private boolean disponibilidad;
    private float consumoPromKm;
    private float costoTraslado;
    private Integer idTransportista; // referencia, no objeto completo
}

