package com.tpi.backend.msflota.controller;

import com.tpi.backend.msflota.dto.*;
import com.tpi.backend.msflota.service.FlotaService;
import com.tpi.backend.msflota.util.FlotaMapper;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;


/**
 * Controlador REST del microservicio de flota.
 * Expone endpoints para gestionar camiones, transportistas y tarifas.
 */
@RestController
@RequestMapping("/")
public class FlotaController {

    private final FlotaService flotaService;
    private final FlotaMapper flotaMapper;

    public FlotaController(FlotaService flotaService, FlotaMapper flotaMapper) {
        this.flotaService = flotaService;
        this.flotaMapper = flotaMapper;
    }

    // -------------------- CAMIONES --------------------
    @GetMapping("/camiones")
    public List<CamionDTO> listarCamiones() {
        return flotaService.obtenerCamiones()
                .stream()
                .map(flotaMapper::toCamionDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/camiones")
    public CamionDTO registrarCamion(@RequestBody CamionDTO dto) {
        var camion = flotaMapper.toCamionEntity(dto);
        var nuevo = flotaService.registrarCamion(camion);
        return flotaMapper.toCamionDTO(nuevo);
    }

    @GetMapping("/camiones/disponibles")
    public List<CamionDTO> obtenerCamionesDisponibles() {
        return flotaService.obtenerCamionesDisponibles()
                .stream()
                .map(flotaMapper::toCamionDTO)
                .collect(Collectors.toList());
    }

    // -------------------- TRANSPORTISTAS --------------------
    @GetMapping("/transportistas")
    public List<TransportistaDTO> listarTransportistas() {
        return flotaService.listarTransportistas()
                .stream()
                .map(flotaMapper::toTransportistaDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/transportistas")
    public TransportistaDTO crearTransportista(@RequestBody TransportistaDTO dto) {
        var entidad = flotaMapper.toTransportistaEntity(dto);
        var nuevo = flotaService.crearTransportista(entidad);
        return flotaMapper.toTransportistaDTO(nuevo);
    }

    // -------------------- TARIFAS --------------------
    @GetMapping("/tarifas")
    public List<TarifaDTO> listarTarifas() {
        return flotaService.listarTarifas()
                .stream()
                .map(flotaMapper::toTarifaDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/tarifas")
    public TarifaDTO crearTarifa(@RequestBody TarifaDTO dto) {
        var entidad = flotaMapper.toTarifaEntity(dto);
        var nueva = flotaService.crearTarifa(entidad);
        return flotaMapper.toTarifaDTO(nueva);
    }

    // -------------------- CÁLCULO DE COSTO --------------------
    @GetMapping("/tarifas/calcular")
    public Double calcularCosto(@RequestParam String tipoContenedor,
                                @RequestParam Double distancia,
                                @RequestParam Double peso) {
        return flotaService.calcularCosto(tipoContenedor, distancia, peso);
    }
}
