package com.tpi.backend.mssolicitudes.service;

import com.tpi.backend.mssolicitudes.dto.SolicitudDTO;
import entities.Solicitud;
import entities.Cliente;
import entities.Contenedor;
import entities.Estado;
import com.tpi.backend.mssolicitudes.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;


import com.tpi.backend.mssolicitudes.client.FlotaClient;
import com.tpi.backend.mssolicitudes.client.RutasClient;
import com.tpi.backend.mssolicitudes.dto.TarifaSolicitudDTO;
import com.tpi.backend.mssolicitudes.dto.CamionFlotaDTO;
import com.tpi.backend.mssolicitudes.dto.DistanciaDTO;


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


