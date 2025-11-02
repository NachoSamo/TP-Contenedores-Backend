package com.tpi.backend.msflota.repository;

import entities.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
    // Buscar tarifa por su tipo tal como está en la entidad (tipoTarifa)
    Tarifa findByTipoTarifa(String tipoTarifa);
}
