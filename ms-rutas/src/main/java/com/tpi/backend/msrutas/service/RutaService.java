package com.tpi.backend.msrutas.service;


import com.tpi.backend.msrutas.client.TarifaClient;
import com.tpi.backend.msrutas.dto.TarifaDTO;
import com.tpi.backend.msrutas.dto.geolocalizacion.DistanciaDTO;
import com.tpi.backend.msrutas.repository.*;
import entities.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.tpi.backend.msrutas.dto.RutaAlternativaDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final GeolocalizacionRepository geolocalizacionRepository;
    private final TipoTramoRepository tipoTramoRepository;
    private final EstadoRepository estadoRepository;
    private final CamionRepository camionRepository;
    private final TramoRepository tramoRepository;
    private final DepositoRepository depositoRepository;
    private final GeoService geoService;
    private final TarifaClient tarifaClient;
    // Helper para comparar geolocalizaciones de forma sencilla
    private boolean mismaGeo(Geolocalizacion g1, Geolocalizacion g2) {
        if (g1 == null || g2 == null) return false;
        return g1.getLatitud().equals(g2.getLatitud())
                && g1.getLongitud().equals(g2.getLongitud());
    }

    //private final GeoMapsClient geoMapsClient;


    public RutaService(RutaRepository rutaRepository, GeolocalizacionRepository geolocalizacionRepository, TipoTramoRepository tipoTramoRepository, EstadoRepository estadoRepository, CamionRepository camionRepository,
                       TramoRepository tramoRepository, DepositoRepository depositoRepository, GeoService geoService, TarifaClient tarifaClient) {

        this.rutaRepository = rutaRepository;
        this.geolocalizacionRepository = geolocalizacionRepository;
        this.tipoTramoRepository = tipoTramoRepository;
        this.estadoRepository = estadoRepository;
        this.camionRepository = camionRepository;
        this.tramoRepository = tramoRepository;
        this.depositoRepository = depositoRepository;
        //this.geoMapsClient = geoMapsClient;
        this.geoService = geoService;
        this.tarifaClient = tarifaClient;
    }

    // -------- RUTAS --------
    public List<Ruta> listarRutas(Integer idRuta) {
        if (idRuta != null) {
            return rutaRepository.findById(idRuta)
                    .map(List::of)
                    .orElseGet(List::of);
        } else {
            return rutaRepository.findAll();
        }
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

        // ---------- RUTA OBLIGATORIA ----------
        if (tramo.getRuta() == null || tramo.getRuta().getIdRuta() == null) {
            throw new IllegalArgumentException("idRuta es obligatorio.");
        }

        Ruta ruta = rutaRepository.findById(tramo.getRuta().getIdRuta())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe ruta con id " + tramo.getRuta().getIdRuta()
                ));
        tramo.setRuta(ruta);

// ---------- ORIGEN / DESTINO GEO  ----------
        Geolocalizacion origenGeo = resolverOrigen(tramo);
        Geolocalizacion destinoGeo = resolverDestino(tramo);

        tramo.setOrigenGeo(origenGeo);
        tramo.setDestinoGeo(destinoGeo);

        // ---------- TIPO DE TRAMO ----------
        if (tramo.getTipoTramo() == null ||
                tramo.getTipoTramo().getIdTipoTramo() == null) {
            throw new IllegalArgumentException("tipoTramo es obligatorio.");
        }

        TipoTramo tipoTramo = tipoTramoRepository.findById(
                        tramo.getTipoTramo().getIdTipoTramo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe tipo de tramo con id " +
                                tramo.getTipoTramo().getIdTipoTramo()
                ));
        tramo.setTipoTramo(tipoTramo);

        // ---------- ESTADO ----------
        Estado estado;

        if (tramo.getEstado() != null && tramo.getEstado().getIdEstado() != null) {
            estado = estadoRepository.findById(tramo.getEstado().getIdEstado())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe estado con id " + tramo.getEstado().getIdEstado()
                    ));
        } else {
            estado = estadoRepository
                    .findByDescripcion("PENDIENTE")
                    .orElseThrow(() -> new IllegalStateException(
                            "No se encontró estado con descripcion 'PENDIENTE'"
                    ));
        }

        tramo.setEstado(estado);

        // ---------- CAMIÓN ----------
        if (tramo.getCamion() != null &&
                tramo.getCamion().getDominioCamion() != null) {

            Camion camion = camionRepository.findById(
                            tramo.getCamion().getDominioCamion())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe camión con dominio " +
                                    tramo.getCamion().getDominioCamion()
                    ));
            tramo.setCamion(camion);
        }

        // ---------- GEO + COSTO + TIEMPOS----------
        DistanciaDTO dist = calcularDistanciaEntre(origenGeo, destinoGeo);

        // 1) fechaHoraInicioEstimada
        LocalDateTime inicioEstimada = tramo.getFechaHoraInicioEstimada();
        if (inicioEstimada == null) {
            inicioEstimada = LocalDateTime.now();
        }
        tramo.setFechaHoraInicioEstimada(inicioEstimada);

        // 2) fechaHoraFinEstimada = inicioEstimada + duración de Google Maps
        if (dist.getDuracionMinutos() > 0) {
            tramo.setFechaHoraFinEstimada(
                    inicioEstimada.plusMinutes(dist.getDuracionMinutos())
            );
        }


        // 3) costo aproximado = km * consumoPromKm * costoLitroCombustible
        Float costoAproximado = calcularCostoAproximado(tramo.getCamion(), dist);
        tramo.setCostoAproximado(costoAproximado);


        tramo.setIdTramo(null); // aseguramos alta
        return tramoRepository.save(tramo);
    }

    // ================== HELPERS PRIVADOS ==================

    private Float calcularCostoAproximado(Camion camion, DistanciaDTO dist) {
        if (camion == null || dist == null) {
            return null;
        }

        double kms = dist.getKilometros();
        Float consumoPromKm = camion.getConsumoPromKm();          // litros por km
        Float costoLitroCombustible = obtenerCostoLitroCombustible(camion);

        if (kms <= 0 || consumoPromKm == null || consumoPromKm <= 0
                || costoLitroCombustible == null || costoLitroCombustible <= 0) {
            return null;
        }

        // litros totales = km * (litros/km)
        double litrosTotales = kms * consumoPromKm;

        // costo = litros * $/litro
        double costo = litrosTotales * costoLitroCombustible;

        return (float) costo;
    }

    private Float obtenerCostoLitroCombustible(Camion camion) {
        if (camion == null || camion.getDominioCamion() == null) {
            return null;
        }

        TarifaDTO tarifa = tarifaClient.obtenerTarifaPorCamion(camion.getDominioCamion());
        if (tarifa == null || tarifa.getCostoLitroCombustible() == null) {
            throw new IllegalStateException(
                    "No se encontró tarifa con costoLitroCombustible para el camión " +
                            camion.getDominioCamion()
            );
        }

        return tarifa.getCostoLitroCombustible();
    }



    private Geolocalizacion resolverOrigen(Tramo tramo) {
        // 1) Si vino origenDepositoId en el DTO:
        if (tramo.getOrigenDeposito() != null &&
                tramo.getOrigenDeposito().getIdDeposito() != null) {

            Long idDep = tramo.getOrigenDeposito().getIdDeposito();

            Deposito deposito = depositoRepository.findById(idDep)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe depósito origen con id " + idDep
                    ));

            tramo.setOrigenDeposito(deposito);

            Geolocalizacion geo = deposito.getGeolocalizacion();
            if (geo == null) {
                throw new IllegalStateException(
                        "El depósito origen " + idDep + " no tiene geolocalización asociada"
                );
            }

            return geo;
        }

        // 2) Si NO vino depósito, pero sí un idGeo directo:
        if (tramo.getOrigenGeo() != null &&
                tramo.getOrigenGeo().getIdGeo() != null) {

            Integer idGeo = tramo.getOrigenGeo().getIdGeo();

            Geolocalizacion geo = geolocalizacionRepository.findById(idGeo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe geolocalización origen con id " + idGeo
                    ));

            tramo.setOrigenGeo(geo);

            return geo;
        }

        throw new IllegalArgumentException(
                "Debe indicar origenGeo o origenDepositoId para el tramo."
        );
    }


    private Geolocalizacion resolverDestino(Tramo tramo) {
        // 1) Si vino destinoDepositoId en el DTO:
        if (tramo.getDestinoDeposito() != null &&
                tramo.getDestinoDeposito().getIdDeposito() != null) {

            Long idDep = tramo.getDestinoDeposito().getIdDeposito();

            Deposito deposito = depositoRepository.findById(idDep)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe depósito destino con id " + idDep
                    ));

            tramo.setDestinoDeposito(deposito);

            Geolocalizacion geo = deposito.getGeolocalizacion();
            if (geo == null) {
                throw new IllegalStateException(
                        "El depósito destino " + idDep + " no tiene geolocalización asociada"
                );
            }

            return geo;
        }

        // 2) Si NO vino depósito destino, pero sí un idGeo directo:
        if (tramo.getDestinoGeo() != null &&
                tramo.getDestinoGeo().getIdGeo() != null) {

            Integer idGeo = tramo.getDestinoGeo().getIdGeo();

            Geolocalizacion geo = geolocalizacionRepository.findById(idGeo)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe geolocalización destino con id " + idGeo
                    ));

            tramo.setDestinoGeo(geo);

            return geo;
        }

        throw new IllegalArgumentException(
                "Debe indicar destinoGeo o destinoDepositoId para el tramo."
        );
    }


    private DistanciaDTO calcularDistanciaEntre(Geolocalizacion origen, Geolocalizacion destino) {
        String origenStr = origen.getLatitud() + "," + origen.getLongitud();
        String destinoStr = destino.getLatitud() + "," + destino.getLongitud();

        try {
            return geoService.calcularDistancia(origenStr, destinoStr);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular distancia entre origen y destino", e);
        }
    }


    // -------- DEPOSITOS --------

    public List<Deposito> listarDepositos() {
        return depositoRepository.findAll();
    }

    public Deposito crearDeposito(Deposito deposito) {
        if (deposito.getNombre() == null || deposito.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del depósito es obligatorio.");
        }

        return depositoRepository.save(deposito);
    }

    // -------- RUTAS ALTERNATIVAS (ORIGEN-DESTINO + VÍA DEPÓSITOS) --------
    public List<RutaAlternativaDTO> calcularRutasAlternativas(
            Integer origenGeoId,
            Integer destinoGeoId
    ) {
        Geolocalizacion origen = geolocalizacionRepository.findById(origenGeoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe geolocalización origen con id " + origenGeoId
                ));

        Geolocalizacion destino = geolocalizacionRepository.findById(destinoGeoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe geolocalización destino con id " + destinoGeoId
                ));

        List<RutaAlternativaDTO> alternativas = new java.util.ArrayList<>();

        // 1) Ruta directa Origen -> Destino
        DistanciaDTO distDirecta = calcularDistanciaEntre(origen, destino);

        RutaAlternativaDTO rutaDirecta = new RutaAlternativaDTO();
        rutaDirecta.setDescripcion("Directa origen-destino");
        rutaDirecta.setKilometrosTotales(distDirecta.getKilometros());
        rutaDirecta.setDuracionTotalMinutos(distDirecta.getDuracionMinutos());
        rutaDirecta.setCantidadTramos(1);
        rutaDirecta.setCantidadDepositosIntermedios(0);

        alternativas.add(rutaDirecta);

        // 2) Rutas vía cada depósito: Origen -> Depósito -> Destino
        List<Deposito> depositos = depositoRepository.findAll();

        for (Deposito deposito : depositos) {
            Geolocalizacion geoDep = deposito.getGeolocalizacion();
            if (geoDep == null) {
                // Si el depósito no tiene geo cargada, lo ignoramos
                continue;
            }

            // Evitar rutas absurdas (ej: depósito con misma geo que origen o destino)
            if (mismaGeo(origen, geoDep) || mismaGeo(destino, geoDep)) {
                continue;
            }

            DistanciaDTO distOrigenDep = calcularDistanciaEntre(origen, geoDep);
            DistanciaDTO distDepDestino = calcularDistanciaEntre(geoDep, destino);

            RutaAlternativaDTO rutaViaDeposito = new RutaAlternativaDTO();
            rutaViaDeposito.setDescripcion("Vía depósito " + deposito.getNombre());
            rutaViaDeposito.setKilometrosTotales(
                    distOrigenDep.getKilometros() + distDepDestino.getKilometros()
            );
            rutaViaDeposito.setDuracionTotalMinutos(
                    distOrigenDep.getDuracionMinutos() + distDepDestino.getDuracionMinutos()
            );
            rutaViaDeposito.setCantidadTramos(2);
            rutaViaDeposito.setCantidadDepositosIntermedios(1);

            alternativas.add(rutaViaDeposito);
        }

        return alternativas;
    }

}