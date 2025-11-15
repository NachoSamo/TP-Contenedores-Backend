package com.tpi.backend.mssolicitudes.service;

import com.tpi.backend.mssolicitudes.dto.SolicitudDTO;
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

    public Solicitud actualizarSolicitud(Integer nroSolicitud, SolicitudDTO dto) {
        Solicitud solicitud = solicitudRepository.findById(nroSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));


        if (dto.getCostoEstimado() != null) {
            solicitud.setCostoEstimado(dto.getCostoEstimado());
        }
        if (dto.getTiempoEstimado() != null) {
            solicitud.setTiempoEstimado(dto.getTiempoEstimado());
        }
        if (dto.getCostoReal() != null) {
            solicitud.setCostoReal(dto.getCostoReal());
        }
        if (dto.getTiempoReal() != null) {
            solicitud.setTiempoReal(dto.getTiempoReal());
        }
        if (dto.getDniCliente() != null) {
            Cliente cliente = clienteRepository.findById(dto.getDniCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            solicitud.setCliente(cliente);
        }
        if (dto.getIdContenedor() != null) {
            Contenedor contenedor = contenedorRepository.findById(dto.getIdContenedor())
                    .orElseThrow(() -> new RuntimeException("Contenedor no encontrado"));
            solicitud.setContenedor(contenedor);
        }
        if (dto.getIdEstado() != null) {
            Estado estado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            solicitud.setEstado(estado);
        }

        return solicitudRepository.save(solicitud);
    }


    // -------- CLIENTES --------
    public List<Cliente> listarClientes(Integer dni) {
        if (dni != null) {
            return clienteRepository.buscarPorDni(dni);
        }
        System.out.println(">>> [SERVICE] Ejecutando findAll");
        return clienteRepository.findAll();
    }

    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // -------- CONTENEDORES --------
    public List<Contenedor> listarContenedores() {
        return contenedorRepository.findAll();
    }

    public List<Contenedor> listarContenedoresPorEstadoNombre(String nombreEstado) {
        return contenedorRepository.findByEstado_NombreIgnoreCase(nombreEstado);
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
