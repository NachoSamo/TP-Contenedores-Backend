package com.tpi.backend.msflota.repository;

import entities.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad Camion.
 * Permite realizar consultas CRUD sobre la tabla de camiones.
 */
public interface CamionRepository extends JpaRepository<Camion, Long> {

    // Buscar camiones por estado del transportista
    List<Camion> findByTransportista_Estado(String estado);

    // Buscar por patente exacta
    Camion findByPatente(String patente);
}
