package com.example.ms_reservas.service;

import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> getAllMesas() {
        return mesaRepository.findAll();
    }

    public Optional<Mesa> getMesaById(Long id) {
        return mesaRepository.findById(id);
    }

    public Mesa saveMesa(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public void deleteMesa(Long id) {
        mesaRepository.deleteById(id);
    }

    public List<Mesa> getMesasDisponibles() {
        return mesaRepository.findByDisponible(true);
    }

    public List<Mesa> getMesasByCapacidad(Integer capacidad) {
        return mesaRepository.findByCapacidadGreaterThanEqual(capacidad);
    }

    public boolean isMesaDisponible(Long idMesa, java.time.LocalDate fecha, java.time.LocalTime hora) {
        // Lógica para verificar disponibilidad: no hay reserva activa en esa fecha y hora para la mesa
        // Esto requiere consultar reservas, pero por simplicidad, asumir que si la mesa está disponible, lo está.
        // En un caso real, implementar lógica más compleja.
        Optional<Mesa> mesa = mesaRepository.findById(idMesa);
        return mesa.isPresent() && mesa.get().getDisponible();
    }
}