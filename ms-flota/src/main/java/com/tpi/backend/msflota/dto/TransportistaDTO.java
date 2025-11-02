package com.tpi.backend.msflota.dto;

import lombok.Data;

@Data
public class TransportistaDTO {
    private Integer idTransportista;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String licencia;
    private String estado;
}
