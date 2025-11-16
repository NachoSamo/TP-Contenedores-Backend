package com.tpi.backend.msflota.service;

import com.tpi.backend.msflota.dto.CamionDTO;
import entities.Camion;
import entities.Tarifa;
import entities.Transportista;
import com.tpi.backend.msflota.repository.CamionRepository;
import com.tpi.backend.msflota.repository.TarifaRepository;
import com.tpi.backend.msflota.repository.TransportistaRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public List<Camion> obtenerCamiones(String dominioCamion, Boolean disponibilidad) {
        boolean tieneDominio = dominioCamion != null && !dominioCamion.isBlank();
        boolean tieneDisponibilidad = disponibilidad != null;

        if (tieneDominio && tieneDisponibilidad) {
            return camionRepository
                    .findByDominioCamionContainingIgnoreCaseAndDisponibilidad(dominioCamion, disponibilidad);
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
        if (camion.getDominioCamion() == null || camion.getDominioCamion().isBlank()) {
            throw new IllegalArgumentException("El dominio del camión es obligatorio.");
        }
        if (camionRepository.existsById(camion.getDominioCamion())) {
            throw new IllegalArgumentException("Ya existe un camión registrado con el dominio " + camion.getDominioCamion());
        }
        if (camion.getTransportista() != null && camion.getTransportista().getIdTransportista() != null) {
            Integer idTransportista = camion.getTransportista().getIdTransportista();
            Transportista transportista = transportistaRepository.findById(idTransportista)
                    .orElseThrow(() -> new EntityNotFoundException("No existe un transportista registrado con id " + idTransportista));
            camion.setTransportista(transportista);
        }
        return camionRepository.save(camion);
    }

    public List<Camion> obtenerCamionesDisponibles() {
        return camionRepository.findByDisponibilidad(Boolean.TRUE);
    }

    public Camion actualizarCamion(String dominio, CamionDTO dto) {
        Camion camion = camionRepository.findById(dominio)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el camión con dominio: " + dominio));

        if (dto.getDominioCamion() != null && !dto.getDominioCamion().isBlank() && !dto.getDominioCamion().equals(dominio)) {
            throw new IllegalArgumentException("No se permite modificar el dominio del camión.");
        }
        if (dto.getCapacidadKg() != null) {
            camion.setCapacidadPesoMax(dto.getCapacidadKg());
        }
        if (dto.getVolumenM3() != null) {
            camion.setCapacidadVolumenMax(dto.getVolumenM3());
        }
        if (dto.getDisponibilidad()!= null) {
            camion.setDisponibilidad(dto.getDisponibilidad());
        }
        if (dto.getConsumoPromKm() != null) {
            camion.setConsumoPromKm(dto.getConsumoPromKm());
        }
        if (dto.getCostoTraslado() != null) {
            camion.setCostoTraslado(dto.getCostoTraslado());
        }
        if (dto.getIdTransportista() != null) {
            Transportista transportista = transportistaRepository.findById(dto.getIdTransportista())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró el transportista con ID: " + dto.getIdTransportista()));
            camion.setTransportista(transportista);
        }
        return camionRepository.save(camion);
    }

    public List<Transportista> listarTransportistas() {
        return transportistaRepository.findAll();
    }

    public Transportista crearTransportista(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    public List<Tarifa> listarTarifas() {
        return tarifaRepository.findAll();
    }

    public Tarifa crearTarifa(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }
}