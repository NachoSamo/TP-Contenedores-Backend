package com.tpi.backend.msflota.dto;

import lombok.Data;

/**
 * DTO para transferir datos del Camion sin exponer entidades JPA completas.
 */
@Data
public class CamionDTO {
    private Integer idCamion;
    private String dominio;
    private String modelo;
    private Float capacidadKg;
    private Float volumenM3;
    private String estado; // disponible, en ruta, mantenimiento
    private Integer idTransportista; // referencia, no objeto completo
}

