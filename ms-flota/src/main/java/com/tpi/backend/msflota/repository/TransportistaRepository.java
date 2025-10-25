package com.tpi.backend.msflota.repository;

import entities.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad Transportista.
 */
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {

    // Buscar transportistas por estado (ej: “disponible”)
    List<Transportista> findByEstado(String estado);

    // Buscar transportista por DNI
    Transportista findByDni(String dni);
}
