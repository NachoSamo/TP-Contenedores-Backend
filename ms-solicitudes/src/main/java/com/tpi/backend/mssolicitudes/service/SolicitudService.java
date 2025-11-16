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
    public List<Solicitud> listarSolicitudes(Integer nroSolicitud) {
        if (nroSolicitud != null) {
            return solicitudRepository.findByNroSolicitud(nroSolicitud);
        }
        return solicitudRepository.findAll();
    }

    public Solicitud crearSolicitud(Solicitud solicitud) {
        // 1) Validar el cliente registrado
        if (solicitud.getCliente() == null || solicitud.getCliente().getDniCliente() == null) {
            throw new IllegalArgumentException("El dni_cliente es obligatorio.");
        }

        Integer dniCliente = solicitud.getCliente().getDniCliente();

        // 1) El dni_cliente debe existir como cliente registrado
        var cliente = clienteRepository.findById(dniCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe un cliente registrado con DNI " + dniCliente));

        // 2) Validar el contenedor y su id
        if (solicitud.getContenedor() == null || solicitud.getContenedor().getIdContenedor() == null) {
            throw new IllegalArgumentException("El id_contenedor es obligatorio.");
        }

        Integer idContenedor = solicitud.getContenedor().getIdContenedor();

        // 2) El id_contenedor debe existir
        var contenedor = contenedorRepository.findById(idContenedor)
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe un contenedor con id " + idContenedor));

        // 3) El contenedor debe pertenecer al cliente indicado
        if (contenedor.getCliente() == null ||
                contenedor.getCliente().getDniCliente() == null ||
                !contenedor.getCliente().getDniCliente().equals(dniCliente)) {

            throw new IllegalArgumentException(
                    "El contenedor " + idContenedor + " no pertenece al cliente con DNI " + dniCliente
            );
        }

        // 4) El contenedor no debe estar actualmente asociado a otra solicitud activa
        List<String> estadosBloqueantes = List.of("ACTIVA", "ACT");

        boolean existeSolicitudActiva = solicitudRepository
                .existsByContenedor_IdContenedorAndEstado_DescripcionIn(idContenedor, estadosBloqueantes);

        if (existeSolicitudActiva) {
            throw new IllegalArgumentException(
                    "El contenedor " + idContenedor + " ya está asociado a una solicitud activa."
            );
        }


        solicitud.setCliente(cliente);
        solicitud.setContenedor(contenedor);


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
            return clienteRepository.findByDniCliente(dni);
        }
        return clienteRepository.findAll();
    }

    public Cliente crearCliente(Cliente cliente) {

        if (cliente.getDniCliente() == null) {
            throw new IllegalArgumentException("El DNI del cliente es obligatorio");
        }

        if (clienteRepository.existsByDniCliente(cliente.getDniCliente())) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con el DNI " + cliente.getDniCliente()
            );
        }
        return clienteRepository.save(cliente);
    }

    // -------- CONTENEDORES --------
    public List<Contenedor> listarContenedores() {
        return contenedorRepository.findAll();
    }

    public List<Contenedor> listarContenedoresPorEstadoNombre(String nombreEstado) {
        return contenedorRepository.findByEstado_DescripcionIgnoreCase(nombreEstado);
    }

    public Contenedor crearContenedor(Contenedor contenedor) {
        try {
            if (contenedor.getPesoKg() == null || contenedor.getPesoKg() <= 0) {
                throw new IllegalArgumentException("El peso (kg) debe ser mayor que 0");
            }

            if (contenedor.getVolumenM3() == null || contenedor.getVolumenM3() <= 0) {
                throw new IllegalArgumentException("El volumen (m3) debe ser mayor que 0");
            }

            return contenedorRepository.save(contenedor);

        } catch (IllegalArgumentException ex) {
            // Error de validación conocido
            System.out.println(">>> ERROR VALIDANDO CONTENEDOR: " + ex.getMessage());
            // Lo relanzás tal cual para que el controller (o un @ControllerAdvice) lo maneje
            throw ex;

        } catch (Exception ex) {
            // Cualquier otro error inesperado
            System.out.println(">>> ERROR INESPERADO CREANDO CONTENEDOR: " + ex.getMessage());
            throw new RuntimeException("Error al crear el contenedor", ex);
        }
    }




    // -------- ESTADOS --------
    public List<Estado> listarEstados() {
        return estadoRepository.findAll();
    }

    public Estado crearEstado(Estado estado) {
        return estadoRepository.save(estado);
    }
}
