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

    /**
     * Calcula un costo aproximado de traslado según una tarifa encontrada por tipo.
     * Implementación defensiva: busca en todas las tarifas el que coincida con el tipo
     * y aplica una fórmula simple:
     *   costo = cargoGestionTramo + (costoLitroCombustible * distancia * consumoFactor) + peso * factorPeso
     * Donde consumoFactor se toma del camión asociado si existe; en otro caso se usa 1.0.
     *
     * @param tipoContenedor nombre/tipo para buscar la tarifa
     * @param distancia km previstos
     * @param peso en kg
     * @return costo estimado (Double)
     */
    public Double calcularCosto(String tipoContenedor, Double distancia, Double peso) {
        if (tipoContenedor == null || distancia == null || peso == null) return 0.0;

        Tarifa tarifa = tarifaRepository.findAll()
                .stream()
                .filter(t -> t.getTipoTarifa() != null && t.getTipoTarifa().equalsIgnoreCase(tipoContenedor))
                .findFirst()
                .orElseGet(() -> tarifaRepository.findAll().stream().findFirst().orElse(null));

        if (tarifa == null) return 0.0;

        double cargo = tarifa.getCargoGestionTramo() != null ? tarifa.getCargoGestionTramo() : 0.0;
        double costoLitro = tarifa.getCostoLitroCombustible() != null ? tarifa.getCostoLitroCombustible() : 0.0;

        // intentar obtener consumo del camion asociado si está presente
        double consumoFactor = 1.0;
        try {
            if (tarifa.getCamion() != null && tarifa.getCamion().getConsumoPromKm() != null) {
                consumoFactor = tarifa.getCamion().getConsumoPromKm();
            }
        } catch (Exception ignored) {
        }

        // factor por kg (arbitrario, ajustable)
        double factorPeso = 0.02; // ejemplo: $0.02 por kg

        double costo = cargo + (costoLitro * distancia * consumoFactor) + (peso * factorPeso);
        return costo;
    }
}
