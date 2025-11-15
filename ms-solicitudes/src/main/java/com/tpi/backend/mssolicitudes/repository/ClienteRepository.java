package com.tpi.backend.mssolicitudes.repository;

import entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    //Optional<Cliente> findByDniCliente(Integer dniCliente);
    @Query(
            value = "SELECT * FROM clientes c WHERE c.dni_cliente = :dni",
            nativeQuery = true
    )
    List<Cliente> buscarPorDni(@Param("dni") Integer dni);
}
