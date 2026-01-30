package com.example.ms_reservas.util;

import com.example.ms_reservas.dto.MesaDTO;
import com.example.ms_reservas.dto.ReservaDTO;
import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.model.Reserva;

public class MapperUtil {

    public static MesaDTO toMesaDTO(Mesa mesa) {
        MesaDTO dto = new MesaDTO();
        dto.setIdMesa(mesa.getIdMesa());
        dto.setNumero(mesa.getNumero());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setDisponible(mesa.getDisponible());
        return dto;
    }

    public static ReservaDTO toReservaDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setFecha(reserva.getFecha());
        dto.setHora(reserva.getHora());
        dto.setCantPersonas(reserva.getCantPersonas());
        dto.setEstado(reserva.getEstado());
        dto.setIdUsuario(reserva.getIdUsuario());
        dto.setMesa(toMesaDTO(reserva.getMesa()));
        return dto;
    }

    public static Mesa toMesaEntity(MesaDTO dto) {
        Mesa mesa = new Mesa();
        mesa.setIdMesa(dto.getIdMesa());
        mesa.setNumero(dto.getNumero());
        mesa.setCapacidad(dto.getCapacidad());
        mesa.setDisponible(dto.getDisponible());
        return mesa;
    }

    public static Reserva toReservaEntity(ReservaDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(dto.getIdReserva());
        reserva.setFecha(dto.getFecha());
        reserva.setHora(dto.getHora());
        reserva.setCantPersonas(dto.getCantPersonas());
        reserva.setEstado(dto.getEstado());
        reserva.setIdUsuario(dto.getIdUsuario());
        reserva.setMesa(toMesaEntity(dto.getMesa()));
        return reserva;
    }
}