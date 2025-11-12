package com.tpi.backend.mssolicitudes.controller;

import com.tpi.backend.mssolicitudes.dto.*;
import com.tpi.backend.mssolicitudes.service.SolicitudService;
import com.tpi.backend.mssolicitudes.util.SolicitudMapper;
import entities.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final SolicitudMapper mapper;

    public SolicitudController(SolicitudService solicitudService, SolicitudMapper mapper) {
        this.solicitudService = solicitudService;
        this.mapper = mapper;
    }

    // -------- SOLICITUDES --------
    @GetMapping
    public List<SolicitudDTO> listarSolicitudes() {
        return solicitudService.listarSolicitudes()
                .stream()
                .map(mapper::toSolicitudDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public SolicitudDTO crearSolicitud(@RequestBody SolicitudDTO dto) {
        Solicitud solicitud = solicitudService.crearSolicitud(mapper.toSolicitudEntity(dto));
        return mapper.toSolicitudDTO(solicitud);
    }

    // -------- CLIENTES --------
    @GetMapping("/clientes")
    public List<ClienteDTO> listarClientes() {
        return solicitudService.listarClientes()
                .stream()
                .map(mapper::toClienteDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/clientes")
    public ClienteDTO crearCliente(@RequestBody ClienteDTO dto) {
        Cliente cliente = solicitudService.crearCliente(mapper.toClienteEntity(dto));
        return mapper.toClienteDTO(cliente);
    }

    // -------- CONTENEDORES --------
    @GetMapping("/contenedores")
    public List<ContenedorDTO> listarContenedores() {
        return solicitudService.listarContenedores()
                .stream()
                .map(mapper::toContenedorDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/contenedores")
    public ContenedorDTO crearContenedor(@RequestBody ContenedorDTO dto) {
        Contenedor contenedor = solicitudService.crearContenedor(mapper.toContenedorEntity(dto));
        return mapper.toContenedorDTO(contenedor);
    }

    // -------- ESTADOS --------
    @GetMapping("/estados")
    public List<EstadoDTO> listarEstados() {
        return solicitudService.listarEstados()
                .stream()
                .map(mapper::toEstadoDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/estados")
    public EstadoDTO crearEstado(@RequestBody EstadoDTO dto) {
        Estado estado = solicitudService.crearEstado(mapper.toEstadoEntity(dto));
        return mapper.toEstadoDTO(estado);
    }
}
