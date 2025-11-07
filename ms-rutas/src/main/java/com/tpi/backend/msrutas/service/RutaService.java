package com.tpi.backend.msrutas.service;

import entities.Ruta;
import entities.Tramo;
import entities.Deposito;
import com.tpi.backend.msrutas.repository.RutaRepository;
import com.tpi.backend.msrutas.repository.TramoRepository;
import com.tpi.backend.msrutas.repository.DepositoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final DepositoRepository depositoRepository;
    private final GeoMapsClient geoMapsClient;

    public RutaService(RutaRepository rutaRepository,
                       TramoRepository tramoRepository,
                       DepositoRepository depositoRepository,
                       GeoMapsClient geoMapsClient) {
        this.rutaRepository = rutaRepository;
        this.tramoRepository = tramoRepository;
        this.depositoRepository = depositoRepository;
        this.geoMapsClient = geoMapsClient;
    }

    // -------- RUTAS --------
    public List<Ruta> listarRutas() {
        return rutaRepository.findAll();
    }

    public Ruta crearRuta(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    // -------- TRAMOS --------
    public List<Tramo> listarTramosPorRuta(Integer idRuta) {
        return tramoRepository.findByRuta_IdRuta(idRuta);
    }

    public Tramo crearTramo(Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    // -------- DEPOSITOS --------
    public List<Deposito> listarDepositos() {
        return depositoRepository.findAll();
    }

    public Deposito crearDeposito(Deposito deposito) {
        return depositoRepository.save(deposito);
    }
}
