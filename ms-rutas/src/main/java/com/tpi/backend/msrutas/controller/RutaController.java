package com.tpi.backend.msrutas.controller;

import com.tpi.backend.msrutas.dto.*;
import com.tpi.backend.msrutas.service.RutaService;
import com.tpi.backend.msrutas.util.RutaMapper;
import entities.Ruta;
import entities.Tramo;
import entities.Deposito;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class RutaController {

    private final RutaService rutaService;
    private final RutaMapper mapper;

    public RutaController(RutaService rutaService, RutaMapper mapper) {
        this.rutaService = rutaService;
        this.mapper = mapper;
    }

    // -------- RUTAS --------
    @GetMapping
    public List<RutaDTO> listarRutas() {
        return rutaService.listarRutas()
                .stream()
                .map(mapper::toRutaDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public RutaDTO crearRuta(@RequestBody RutaDTO dto) {
        Ruta ruta = rutaService.crearRuta(mapper.toRutaEntity(dto));
        return mapper.toRutaDTO(ruta);
    }

    // -------- TRAMOS --------
    @GetMapping("/{idRuta}/tramos")
    public List<TramoDTO> listarTramosPorRuta(@PathVariable Integer idRuta) {
        return rutaService.listarTramosPorRuta(idRuta)
                .stream()
                .map(mapper::toTramoDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/tramos")
    public TramoDTO crearTramo(@RequestBody TramoDTO dto) {
        Tramo tramo = rutaService.crearTramo(mapper.toTramoEntity(dto));
        return mapper.toTramoDTO(tramo);
    }

    // -------- DEPOSITOS --------
    @GetMapping("/depositos")
    public List<DepositoDTO> listarDepositos() {
        return rutaService.listarDepositos()
                .stream()
                .map(mapper::toDepositoDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/depositos")
    public DepositoDTO crearDeposito(@RequestBody DepositoDTO dto) {
        Deposito deposito = rutaService.crearDeposito(mapper.toDepositoEntity(dto));
        return mapper.toDepositoDTO(deposito);
    }
}
