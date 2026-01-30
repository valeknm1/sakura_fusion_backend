package com.example.ms_reservas.service;

import com.example.ms_reservas.client.UsuarioClient;
import com.example.ms_reservas.dto.UsuarioDTO;
import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.model.Reserva;
import com.example.ms_reservas.repository.MesaRepository;
import com.example.ms_reservas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> getReservaById(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva saveReserva(Reserva reserva) {
        // Verificar que el usuario existe
        try {
            UsuarioDTO usuario = usuarioClient.getUsuarioById(reserva.getIdUsuario());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al validar usuario: " + e.getMessage());
        }

        // Verificar disponibilidad antes de guardar
        if (!isMesaDisponible(reserva.getMesa().getIdMesa(), reserva.getFecha(), reserva.getHora())) {
            throw new RuntimeException("Mesa no disponible en esa fecha y hora");
        }
        return reservaRepository.save(reserva);
    }

    public void deleteReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> getReservasByFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    public List<Reserva> getReservasByUsuario(Long idUsuario) {
        return reservaRepository.findByIdUsuario(idUsuario);
    }

    public List<Reserva> getReservasByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    public Reserva cancelarReserva(Long id) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setEstado("CANCELADA");
            return reservaRepository.save(reserva);
        }
        throw new RuntimeException("Reserva no encontrada");
    }

    private boolean isMesaDisponible(Long idMesa, LocalDate fecha, LocalTime hora) {
        // Verificar si hay reservas activas en esa fecha y hora para la mesa
        List<Reserva> reservas = reservaRepository.findByFechaAndEstado(fecha, "ACTIVA");
        for (Reserva r : reservas) {
            if (r.getMesa().getIdMesa().equals(idMesa) && r.getHora().equals(hora)) {
                return false;
            }
        }
        return true;
    }
}