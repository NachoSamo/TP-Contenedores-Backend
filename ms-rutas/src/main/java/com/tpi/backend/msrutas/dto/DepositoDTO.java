package com.tpi.backend.msrutas.dto;

import lombok.Data;

/**
 * DTO que representa un depósito o punto de parada intermedio.
 */
@Data
public class DepositoDTO {
    private Integer idDeposito;
    private String nombre;
    private String direccion;
    private Float latitud;
    private Float longitud;
    private String tipo; // central, intermedio, destino
}
