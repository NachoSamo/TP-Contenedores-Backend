package com.tpi.backend.mssolicitudes.service;

import com.tpi.backend.mssolicitudes.dto.*;
import entities.*;
import com.tpi.backend.mssolicitudes.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


import com.tpi.backend.mssolicitudes.client.FlotaClient;
import com.tpi.backend.mssolicitudes.client.RutasClient;


@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final ContenedorRepository contenedorRepository;
    private final EstadoRepository estadoRepository;

    private final RutasClient rutasClient;
    private final FlotaClient flotaClient;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            ClienteRepository clienteRepository,
                            ContenedorRepository contenedorRepository,
                            EstadoRepository estadoRepository,
                            RutasClient rutasClient,
                            FlotaClient flotaClient) {
        this.solicitudRepository = solicitudRepository;
        this.clienteRepository = clienteRepository;
        this.contenedorRepository = contenedorRepository;
        this.estadoRepository = estadoRepository;
        this.rutasClient = rutasClient;
        this.flotaClient = flotaClient;
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

    public List<Contenedor> listarContenedoresPorDniCliente(Integer dniCliente) {
        return contenedorRepository.findByCliente_DniCliente(dniCliente);
    }

    public List<Contenedor> listarContenedoresPorDniYEstado(Integer dniCliente, String nombreEstado) {
        return contenedorRepository
                .findByCliente_DniClienteAndEstado_DescripcionIgnoreCase(dniCliente, nombreEstado);
    }


    public EstadoDTO obtenerEstadoActualDeContenedor(Integer idContenedor) {

        // 1) Buscar contenedor
        Contenedor contenedor = contenedorRepository.findById(idContenedor)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró el contenedor con id " + idContenedor));

        // 2) Obtener estado
        Estado estado = contenedor.getEstado();

        if (estado == null) {
            throw new IllegalStateException(
                    "El contenedor con id " + idContenedor + " no tiene un estado asignado");
        }

        // 3) Validar contexto = CONTENEDOR (usando el enum)
        Contexto contexto = estado.getContexto();

        if (contexto == null || contexto != Contexto.CONTENEDOR) {
            throw new IllegalStateException(
                    "El estado asociado al contenedor con id " + idContenedor +
                            " no corresponde al contexto CONTENEDOR (contexto actual: " + contexto + ")");
        }

        // 4) Mapear a EstadoDTO
        EstadoDTO dto = new EstadoDTO();
        dto.setIdEstado(estado.getIdEstado());
        dto.setContexto(contexto.name());
        dto.setDescripcion(estado.getDescripcion());

        return dto;
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

    // -------- TARIFAS --------
    /**
     * Calcula y persiste el costo_real de una solicitud.
     * Orquesta ms-rutas, ms-flota y los datos locales.
     */
    public TarifaSolicitudDTO calcularTarifaSolicitud(Integer nroSolicitud) {
        Solicitud solicitud = solicitudRepository.findById(nroSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada: " + nroSolicitud));

        Contenedor contenedor = solicitud.getContenedor();
        if (contenedor == null) {
            throw new IllegalStateException("La solicitud no tiene contenedor asociado");
        }

        Float pesoKg = contenedor.getPesoKg();      // campo en Contenedor (del common-data)
        Float volumenM3 = contenedor.getVolumenM3();

        // 1) Obtener distancia desde ms-rutas
        // 👉 acá depende de cómo definas el origen/destino (puede ser dirección de depósito, etc.)
        // Por ahora lo dejo genérico:
        String origen = "OrigenDummy";   // TODO: determinar desde la ruta / depósitos
        String destino = "DestinoDummy";

        DistanciaDTO distanciaDTO = rutasClient.obtenerDistancia(origen, destino);
        double distanciaKm = distanciaDTO.getKilometros();

        // 2) Obtener costo base desde ms-flota (seguimos usando su calculadora actual)
        String tipoContenedor = "BASE"; // o derivado del tipo real de contenedor
        double costoTraslado = flotaClient.calcularCosto(tipoContenedor, distanciaKm, pesoKg);

        // 3) Validar capacidad de camión (si ya tens un camión asignado)
        //    Podés guardar el dominio del camión en Tramo o en la propia Solicitud.
        String dominioCamion = "AA123BB"; // TODO: obtener del contexto real (Tramo.camion.dominio)

        CamionFlotaDTO camion = flotaClient.obtenerCamionPorDominio(dominioCamion);

        if (camion != null) {
            // VALIDACIÓN: peso
            if (pesoKg != null && camion.getCapacidadKg() != null &&
                    Float.compare(pesoKg, camion.getCapacidadKg()) > 0) {
                throw new IllegalArgumentException(
                        "El peso del contenedor (" + pesoKg + " kg) excede la capacidad del camión (" +
                                camion.getCapacidadKg() + " kg)");
            }

            // VALIDACIÓN: volumen
            if (volumenM3 != null && camion.getVolumenM3() != null &&
                    volumenM3 > camion.getVolumenM3()) {
                throw new IllegalArgumentException(
                        "El volumen del contenedor (" + volumenM3 + " m3) excede el volumen máximo del camión (" +
                                camion.getVolumenM3() + " m3)");
            }
        }

        // 4) Costo de estadía (si lo quers ya sumar acá)
        //    Podrías obtener el depósito desde ms-rutas (DepositoDTO con costoEstadiaDiaria)
        double costoEstadia = 0.0;   // placeholder hasta que definas los días de estadía

        // 5) Cargos de gestión (del campo cargo_gestion_tramo en Tarifa).
        //    Si  traerlo explícitamente, podríamos pedir también TarifaDTO desde ms-flota.
        double cargosGestion = 0.0;  // placeholder

        double costoReal = costoTraslado + costoEstadia + cargosGestion;

        // 6) Guardar en la Solicitud
        solicitud.setCostoReal((float) costoReal);
        solicitudRepository.save(solicitud);

        // 7) Armar DTO de respuesta
        TarifaSolicitudDTO dto = new TarifaSolicitudDTO();
        dto.setNroSolicitud(nroSolicitud);
        dto.setDistanciaKm(distanciaKm);
        dto.setCostoTraslado(costoTraslado);
        dto.setCostoEstadia(costoEstadia);
        dto.setCargosGestion(cargosGestion);
        dto.setCostoReal(costoReal);

        return dto;
    }
}


