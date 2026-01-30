package com.example.ms_reservas.repository;

import com.example.ms_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByFecha(LocalDate fecha);

    List<Reserva> findByIdUsuario(Long idUsuario);

    List<Reserva> findByEstado(String estado);

    List<Reserva> findByFechaAndEstado(LocalDate fecha, String estado);
}