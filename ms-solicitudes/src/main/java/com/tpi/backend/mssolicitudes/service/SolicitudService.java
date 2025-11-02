package com.tpi.backend.mssolicitudes.service;

import entities.Solicitud;
import entities.Cliente;
import entities.Contenedor;
import entities.Estado;
import com.tpi.backend.mssolicitudes.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final EstadoRepository estadoRepository;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            ClienteRepository clienteRepository,
                            ContenedorRepository contenedorRepository,
                            EstadoRepository estadoRepository) {
        this.solicitudRepository = solicitudRepository;
        this.clienteRepository = clienteRepository;
        this.contenedorRepository = contenedorRepository;
        this.estadoRepository = estadoRepository;
    }

    // -------- SOLICITUDES --------
    public List<Solicitud> listarSolicitudes() {
        return solicitudRepository.findAll();
    }

    public Solicitud crearSolicitud(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    // -------- CLIENTES --------
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // -------- CONTENEDORES --------
    public List<Contenedor> listarContenedores() {
        return contenedorRepository.findAll();
    }

    public Contenedor crearContenedor(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    // -------- ESTADOS --------
    public List<Estado> listarEstados() {
        return estadoRepository.findAll();
    }

    public Estado crearEstado(Estado estado) {
        return estadoRepository.save(estado);
    }
}
