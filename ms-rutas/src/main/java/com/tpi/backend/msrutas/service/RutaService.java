package com.tpi.backend.msrutas.service;

import entities.Ruta;
import entities.Tramo;
import entities.Deposito;
import com.tpi.backend.msrutas.repository.RutaRepository;
import com.tpi.backend.msrutas.repository.TramoRepository;
import com.tpi.backend.msrutas.repository.DepositoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final TramoRepository tramoRepository;
    private final DepositoRepository depositoRepository;

    public RutaService(RutaRepository rutaRepository,
                       TramoRepository tramoRepository,
                       DepositoRepository depositoRepository) {
        this.rutaRepository = rutaRepository;
        this.tramoRepository = tramoRepository;
        this.depositoRepository = depositoRepository;
    }

    public List<Ruta> listarRutas() {
        return rutaRepository.findAll();
    }

    public Ruta crearRuta(Ruta ruta) {
        if (ruta.getSolicitud().getNroSolicitud() == null) {
            throw new IllegalArgumentException("El número de solicitud (nroSolicitud) es obligatorio para crear una ruta.");
        }
        return rutaRepository.save(ruta);
    }

    public List<Tramo> listarTramosPorRuta(Integer idRuta) {
        if (!rutaRepository.existsById(idRuta)) {
            throw new EntityNotFoundException("No se encontró la ruta con ID: " + idRuta);
        }
        return tramoRepository.findByRuta_IdRuta(idRuta);
    }

    public Tramo crearTramo(Tramo tramo) {
        if (tramo.getRuta() == null || tramo.getRuta().getIdRuta() == null) {
            throw new IllegalArgumentException("El ID de la ruta es obligatorio para crear un tramo.");
        }
        if (tramo.getOrigenDeposito() == null || tramo.getOrigenDeposito().getIdDeposito() == null) {
            throw new IllegalArgumentException("El depósito de origen es obligatorio para crear un tramo.");
        }
        if (tramo.getDestinoDeposito() == null || tramo.getDestinoDeposito().getIdDeposito() == null) {
            throw new IllegalArgumentException("El depósito de destino es obligatorio para crear un tramo.");
        }

        Ruta rutaExistente = rutaRepository.findById(tramo.getRuta().getIdRuta())
                .orElseThrow(() -> new EntityNotFoundException("La ruta con ID " + tramo.getRuta().getIdRuta() + " no existe."));

        Deposito origenExistente = depositoRepository.findById(tramo.getOrigenDeposito().getIdDeposito())
                .orElseThrow(() -> new EntityNotFoundException("El depósito de origen con ID " + tramo.getOrigenDeposito().getIdDeposito() + " no existe."));

        Deposito destinoExistente = depositoRepository.findById(tramo.getDestinoDeposito().getIdDeposito())
                .orElseThrow(() -> new EntityNotFoundException("El depósito de destino con ID " + tramo.getDestinoDeposito().getIdDeposito() + " no existe."));

        tramo.setRuta(rutaExistente);
        tramo.setOrigenDeposito(origenExistente);
        tramo.setDestinoDeposito(destinoExistente);

        return tramoRepository.save(tramo);
    }

    public List<Deposito> listarDepositos() {
        return depositoRepository.findAll();
    }

    public Deposito crearDeposito(Deposito deposito) {
        if (deposito.getNombre() == null || deposito.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del depósito es obligatorio.");
        }

        return depositoRepository.save(deposito);
    }
}