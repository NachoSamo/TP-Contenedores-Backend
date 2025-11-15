package com.tpi.backend.msflota.service;

import entities.Camion;
import entities.Tarifa;
import entities.Transportista;
import com.tpi.backend.msflota.repository.CamionRepository;
import com.tpi.backend.msflota.repository.TarifaRepository;
import com.tpi.backend.msflota.repository.TransportistaRepository;
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
    public List<Camion> obtenerCamiones() {
        return camionRepository.findAll();
    }

    public Camion registrarCamion(Camion camion) {
        return camionRepository.save(camion);
    }

    public List<Camion> obtenerCamionesDisponibles() {
        // Buscar camiones cuya disponibilidad sea true
        return camionRepository.findByDisponibilidad(Boolean.TRUE);
    }

    // -------- TRANSPORTISTAS --------
    public List<Transportista> listarTransportistas() {
        return transportistaRepository.findAll();
    }

    public Transportista crearTransportista(Transportista transportista) {
        return transportistaRepository.save(transportista);
    }

    // -------- TARIFAS --------
    public List<Tarifa> listarTarifas() {
        return tarifaRepository.findAll();
    }

    public Tarifa crearTarifa(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }
}
