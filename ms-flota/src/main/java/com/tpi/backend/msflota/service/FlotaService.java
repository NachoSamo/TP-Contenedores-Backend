package com.tpi.backend.msflota.service;

import com.tpi.backend.msflota.dto.CamionDTO;
import entities.Camion;
import entities.Tarifa;
import entities.Transportista;
import com.tpi.backend.msflota.repository.CamionRepository;
import com.tpi.backend.msflota.repository.TarifaRepository;
import com.tpi.backend.msflota.repository.TransportistaRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlotaService {

    private final CamionRepository camionRepository;
    private final TarifaRepository tarifaRepository;
    private final TransportistaRepository transportistaRepository;

    public FlotaService(CamionRepository camionRepository,
                        TarifaRepository tarifaRepository,
                        TransportistaRepository transportistaRepository) {
        this.camionRepository = camionRepository;
        this.tarifaRepository = tarifaRepository;
        this.transportistaRepository = transportistaRepository;
    }

    // -------- CAMIONES --------
    public List<Camion> obtenerCamiones(String dominioCamion, Boolean disponibilidad) {

        boolean tieneDominio = dominioCamion != null && !dominioCamion.isBlank();
        boolean tieneDisponibilidad = disponibilidad != null;

        if (tieneDominio && tieneDisponibilidad) {
            return camionRepository
                    .findByDominioCamionContainingIgnoreCaseAndDisponibilidad(
                            dominioCamion, disponibilidad
                    );

        } else if (tieneDominio) {
            return camionRepository
                    .findByDominioCamionContainingIgnoreCase(dominioCamion);

        } else if (tieneDisponibilidad) {
            return camionRepository
                    .findByDisponibilidad(disponibilidad);

        } else {
            return camionRepository.findAll();
        }
    }

    public Camion registrarCamion(Camion camion) {
        // 1) Validar dominio_camion (PK obligatoria)
        if (camion.getDominioCamion() == null || camion.getDominioCamion().isBlank()) {
            throw new IllegalArgumentException("El dominio_camion es obligatorio.");
        }

        String dominioCamion = camion.getDominioCamion();

        // 2) El dominio_camion no debe existir previamente
        boolean existeCamion = camionRepository.existsById(dominioCamion);
        if (existeCamion) {
            throw new IllegalArgumentException(
                    "Ya existe un camión registrado con dominio " + dominioCamion
            );
        }

        // 3) Validar capacidades y datos numéricos mínimos
        if (camion.getCapacidadPesoMax() == null || camion.getCapacidadPesoMax() <= 0) {
            throw new IllegalArgumentException("La capacidadKg (capacidad_peso_max) debe ser mayor a 0.");
        }

        if (camion.getCapacidadVolumenMax() == null || camion.getCapacidadVolumenMax() <= 0) {
            throw new IllegalArgumentException("El volumenM3 (capacidad_volumen_max) debe ser mayor a 0.");
        }

        if (camion.getConsumoPromKm() == null || camion.getConsumoPromKm() <= 0) {
            throw new IllegalArgumentException("El consumoPromKm debe ser mayor a 0.");
        }

        if (camion.getCostoTraslado() == null || camion.getCostoTraslado() <= 0) {
            throw new IllegalArgumentException("El costoTraslado debe ser mayor a 0.");
        }

        // 4) Resolver transportista (si viene informado)
        if (camion.getTransportista() != null &&
                camion.getTransportista().getIdTransportista() != null) {

            Integer idTransportista = camion.getTransportista().getIdTransportista();

            var transportista = transportistaRepository.findById(idTransportista)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe un transportista registrado con id " + idTransportista
                    ));

            camion.setTransportista(transportista);
        } else {
            // si no viene transportista, se deja en null explícitamente
            camion.setTransportista(null);
        }

        // 5) Disponibilidad por defecto (si viene null)
        if (camion.getDisponibilidad() == null) {
            camion.setDisponibilidad(Boolean.TRUE); // por defecto disponible
        }

        // 6) Persistir camión
        return camionRepository.save(camion);
    }

    public List<Camion> obtenerCamionesDisponibles() {
        // Buscar camiones cuya disponibilidad sea true
        return camionRepository.findByDisponibilidad(Boolean.TRUE);
    }


    public Camion actualizarCamion(String dominio, CamionDTO dto) {
        // 1) Buscar camión existente por dominio (PK)
        Camion camion = camionRepository.findById(dominio)
                .orElseThrow(() -> new RuntimeException("Camión no encontrado"));

        // 2) Si el DTO trae dominioCamion no permitimos la modificación
        if (dto.getDominioCamion() != null &&
                !dto.getDominioCamion().isBlank() &&
                !dto.getDominioCamion().equals(dominio)) {

            throw new IllegalArgumentException("No se permite modificar el dominio del camión.");
        }

        if (dto.getCapacidadKg() != null) {
            camion.setCapacidadPesoMax(dto.getCapacidadKg());
        }

        if (dto.getVolumenM3() != null) {
            camion.setCapacidadVolumenMax(dto.getVolumenM3());
        }

        camion.setDisponibilidad(dto.isDisponibilidad());
        camion.setConsumoPromKm(dto.getConsumoPromKm());
        camion.setCostoTraslado(dto.getCostoTraslado());

        // Actualizar transportista si viene idTransportista
        if (dto.getIdTransportista() != null) {
            Transportista transportista = transportistaRepository.findById(dto.getIdTransportista())
                    .orElseThrow(() -> new RuntimeException("Transportista no encontrado"));
            camion.setTransportista(transportista);
        }


        return camionRepository.save(camion);
    }


    // -------- TRANSPORTISTAS --------
    public List<Transportista> listarTransportistas() {
        return transportistaRepository.findAll();
    }

    public Transportista registrarTransportista(Transportista transportista) {

        if (transportista.getNombre() == null || transportista.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del transportista es obligatorio.");
        }

        if (transportista.getApellido() == null || transportista.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido del transportista es obligatorio.");
        }

        if (transportista.getDni() == null || transportista.getDni().isBlank()) {
            throw new IllegalArgumentException("El DNI del transportista es obligatorio.");
        }

        // 2) El DNI no debe existir previamente
        boolean existeDni = transportistaRepository.existsByDni(transportista.getDni());
        if (existeDni) {
            throw new IllegalArgumentException(
                    "Ya existe un transportista registrado con DNI " + transportista.getDni()
            );
        }

        if (transportista.getActivo() == null) {
            transportista.setActivo(Boolean.TRUE);
        }

        // 6) Persistir transportista
        return transportistaRepository.save(transportista);
    }


    // -------- TARIFAS --------
    public List<Tarifa> listarTarifas(String dominioCamion) {
        boolean tieneDominio = dominioCamion != null && !dominioCamion.isBlank();

        if (tieneDominio) {
            return tarifaRepository
                    .findByCamion_DominioCamionContainingIgnoreCase(dominioCamion);
        } else {
            return tarifaRepository.findAll();
        }
    }

    public Tarifa registrarTarifa(Tarifa tarifa, String dominioCamion) {
        if (tarifa.getTipoTarifa() == null || tarifa.getTipoTarifa().isBlank()) {
            throw new IllegalArgumentException("El tipoTarifa es obligatorio.");
        }

        if (tarifa.getCostoLitroCombustible() == null || tarifa.getCostoLitroCombustible() <= 0) {
            throw new IllegalArgumentException("El costoLitroCombustible debe ser mayor a 0.");
        }

        if (tarifa.getCargoGestionTramo() == null || tarifa.getCargoGestionTramo() < 0) {
            throw new IllegalArgumentException("El cargoGestionTramo no puede ser negativo.");
        }

        if (dominioCamion == null || dominioCamion.isBlank()) {
            throw new IllegalArgumentException("El dominioCamion es obligatorio.");
        }

        Camion camion = camionRepository.findById(dominioCamion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un camión registrado con dominio " + dominioCamion
                ));

        tarifa.setCamion(camion);

        tarifa.setIdTarifa(null);
        return tarifaRepository.save(tarifa);
    }
}
