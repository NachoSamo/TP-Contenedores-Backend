package com.tpi.backend.msflota.repository;

import entities.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CamionRepository extends JpaRepository<Camion, String> {
    // Buscar por disponibilidad (boolean) en la entidad common-data
    List<Camion> findByDisponibilidad(Boolean disponibilidad);
}

