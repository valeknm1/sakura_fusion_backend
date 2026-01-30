package com.example.ms_reservas.repository;

import com.example.ms_reservas.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findByDisponible(Boolean disponible);

    List<Mesa> findByCapacidadGreaterThanEqual(Integer capacidad);
}