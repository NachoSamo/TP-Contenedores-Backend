package com.tpi.backend.msflota.repository;

import com.tpi.backend.msflota.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para las tarifas.
 * Permite buscar o crear tarifas según el tipo de contenedor.
 */
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    // Buscar una tarifa específica por tipo de contenedor
    Tarifa findByTipoContenedor(String tipoContenedor);
}
