package com.tpi.backend.msflota.controller;

import entities.Camion;
import entities.Tarifa;
import entities.Transportista;
import com.tpi.backend.msflota.service.FlotaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST del microservicio de flota.
 * Expone endpoints para gestionar camiones, transportistas y tarifas.
 */
@RestController
@RequestMapping("/api/flota")
public class FlotaController {

    private final FlotaService flotaService;

    public FlotaController(FlotaService flotaService) {
        this.flotaService = flotaService;
    }

    // -------------------- CAMIONES --------------------
    @GetMapping("/camiones")
    public List<Camion> listarCamiones() {
        return flotaService.obtenerCamiones();
    }

    @PostMapping("/camiones")
    public Camion registrarCamion(@RequestBody Camion camion) {
        return flotaService.registrarCamion(camion);
    }

    @GetMapping("/camiones/disponibles")
    public List<Camion> obtenerCamionesDisponibles() {
        return flotaService.obtenerCamionesDisponibles();
    }

    // -------------------- TRANSPORTISTAS --------------------
    @GetMapping("/transportistas")
    public List<Transportista> listarTransportistas() {
        return flotaService.listarTransportistas();
    }

    @PostMapping("/transportistas")
    public Transportista crearTransportista(@RequestBody Transportista t) {
        return flotaService.crearTransportista(t);
    }

    // -------------------- TARIFAS --------------------
    @GetMapping("/tarifas")
    public List<Tarifa> listarTarifas() {
        return flotaService.listarTarifas();
    }

    @PostMapping("/tarifas")
    public Tarifa crearTarifa(@RequestBody Tarifa tarifa) {
        return flotaService.crearTarifa(tarifa);
    }

    // -------------------- CALCULO DE COSTO --------------------
    @GetMapping("/tarifas/calcular")
    public Double calcularCosto(@RequestParam String tipoContenedor,
                                @RequestParam Double distancia,
                                @RequestParam Double peso) {
        return flotaService.calcularCosto(tipoContenedor, distancia, peso);
    }
}
