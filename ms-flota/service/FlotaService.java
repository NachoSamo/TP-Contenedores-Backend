package com.tpi.backend.msflota.service;

import com.tpi.backend.msflota.model.Camion;
import com.tpi.backend.msflota.model.Tarifa;
import com.tpi.backend.msflota.model.Transportista;
import com.tpi.backend.msflota.repository.CamionRepository;
import com.tpi.backend.msflota.repository.TarifaRepository;
import com.tpi.backend.msflota.repository.TransportistaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de la flota.
 * Coordina las operaciones entre los repositorios y aplica lógica de negocio.
 */
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

    // ============================
    // 🔹 Gestión de Camiones
    // ============================
    public List<Camion> obtenerCamiones() {
        return camionRepository.findAll();
    }

    public Camion registrarCamion(Camion camion) {
        return camionRepository.save(camion);
    }

    public List<Camion> obtenerCamionesDisponibles() {
        return camionRepository.findByTransportista_Estado("disponible");
    }

    // ============================
    // 🔹 Gestión de Transportistas
    // ============================
    public List<Transportista> listarTransportistas() {
        return transportistaRepository.findAll();
    }

    public Transportista crearTransportista(Transportista t) {
        return transportistaRepository.save(t);
    }

    // ============================
    // 🔹 Gestión de Tarifas
    // ============================
    public List<Tarifa> listarTarifas() {
        return tarifaRepository.findAll();
    }

    public Tarifa crearTarifa(Tarifa t) {
        return tarifaRepository.save(t);
    }

    /**
     * Calcula el costo estimado de un viaje.
     * @param tipoContenedor tipo de contenedor (ej: 40 pies)
     * @param distancia distancia en km
     * @param peso peso en toneladas
     * @return costo total estimado
     */
    public Double calcularCosto(String tipoContenedor, Double distancia, Double peso) {
        Tarifa tarifa = tarifaRepository.findByTipoContenedor(tipoContenedor);
        if (tarifa == null) throw new RuntimeException("No existe tarifa para el tipo: " + tipoContenedor);
        return tarifa.getPrecioBase() + (distancia * tarifa.getPrecioPorKm()) + (peso * tarifa.getPrecioPorTonelada());
    }
}
