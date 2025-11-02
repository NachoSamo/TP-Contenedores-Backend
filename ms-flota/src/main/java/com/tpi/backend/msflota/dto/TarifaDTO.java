package com.tpi.backend.msflota.dto;

import lombok.Data;

/**
 * DTO para transferir datos de Tarifa entre la API y la capa de servicio.
 */
@Data
public class TarifaDTO {
    private Integer idTarifa;
    private String tipoTarifa;
    private Float costoLitroCombustible;
    private Float cargoGestionTramo;
    private String dominioCamion;
}
