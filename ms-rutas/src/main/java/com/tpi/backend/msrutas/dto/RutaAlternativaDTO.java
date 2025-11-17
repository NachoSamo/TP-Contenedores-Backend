package com.tpi.backend.msrutas.dto;

import lombok.Data;

@Data
public class RutaAlternativaDTO {

    // Ej: "Directa", "Vía depósito Córdoba"
    private String descripcion;

    private double kilometrosTotales;
    private long duracionTotalMinutos;

    // Para que el profe vea que contemplás el modelo de tramos
    private int cantidadTramos;
    private int cantidadDepositosIntermedios;

    // Si querés, después le podés agregar más cosas,
    // como ids de depósito, etc.
}
