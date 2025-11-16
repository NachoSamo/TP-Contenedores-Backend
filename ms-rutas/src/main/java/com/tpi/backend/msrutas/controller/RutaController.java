package com.tpi.backend.msrutas.controller;

import com.tpi.backend.msrutas.dto.ErrorResponseDTO;
import com.tpi.backend.msrutas.dto.RutaDTO;
import com.tpi.backend.msrutas.dto.TramoDTO;
import com.tpi.backend.msrutas.dto.DepositoDTO;
import com.tpi.backend.msrutas.service.RutaService;
import com.tpi.backend.msrutas.util.RutaMapper;
import entities.Ruta;
import entities.Tramo;
import entities.Deposito;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping
    public List<RutaDTO> listarRutas() {
        return rutaService.listarRutas()
                .stream()
                .map(mapper::toRutaDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> crearRuta(@RequestBody RutaDTO dto, HttpServletRequest request) {
        try {
            Ruta ruta = rutaService.crearRuta(mapper.toRutaEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRutaDTO(ruta));
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{idRuta}/tramos")
    public ResponseEntity<?> listarTramosPorRuta(@PathVariable Integer idRuta, HttpServletRequest request) {
        try {
            List<TramoDTO> tramos = rutaService.listarTramosPorRuta(idRuta)
                    .stream()
                    .map(mapper::toTramoDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(tramos);
        } catch (EntityNotFoundException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found", e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/tramos")
    public ResponseEntity<?> crearTramo(@RequestBody TramoDTO dto, HttpServletRequest request) {
        try {
            Tramo tramo = rutaService.crearTramo(mapper.toTramoEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toTramoDTO(tramo));
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (EntityNotFoundException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found", e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/depositos")
    public List<DepositoDTO> listarDepositos() {
        return rutaService.listarDepositos()
                .stream()
                .map(mapper::toDepositoDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/depositos")
    public ResponseEntity<?> crearDeposito(@RequestBody DepositoDTO dto, HttpServletRequest request) {
        try {
            Deposito deposito = rutaService.crearDeposito(mapper.toDepositoEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDepositoDTO(deposito));
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage(), request.getRequestURI());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}