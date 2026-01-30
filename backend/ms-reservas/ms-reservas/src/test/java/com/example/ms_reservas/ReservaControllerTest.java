package com.example.ms_reservas;

import com.example.ms_reservas.controller.ReservaController;
import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.model.Reserva;
import com.example.ms_reservas.service.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllReservas() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setMesa(mesa);
        List<Reserva> reservas = Arrays.asList(reserva);
        when(reservaService.getAllReservas()).thenReturn(reservas);

        // Act & Assert
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value(1));
        verify(reservaService, times(1)).getAllReservas();
    }

    @Test
    void testGetReservaById() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setMesa(mesa);
        when(reservaService.getReservaById(1L)).thenReturn(Optional.of(reserva));

        // Act & Assert
        mockMvc.perform(get("/api/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(1));
        verify(reservaService, times(1)).getReservaById(1L);
    }

    @Test
    void testCreateReserva() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        Reserva reserva = new Reserva();
        reserva.setIdUsuario(1L);
        reserva.setMesa(mesa);
        reserva.setFecha(LocalDate.now());
        reserva.setHora(LocalTime.now());
        when(reservaService.saveReserva(any(Reserva.class))).thenReturn(reserva);

        // Act & Assert
        mockMvc.perform(post("/api/reservas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1));
        verify(reservaService, times(1)).saveReserva(any(Reserva.class));
    }

    @Test
    void testUpdateReserva() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setMesa(mesa);
        when(reservaService.getReservaById(1L)).thenReturn(Optional.of(reserva));
        when(reservaService.saveReserva(any(Reserva.class))).thenReturn(reserva);

        // Act & Assert
        mockMvc.perform(put("/api/reservas/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(1));
        verify(reservaService, times(1)).saveReserva(any(Reserva.class));
    }

    @Test
    void testDeleteReserva() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/reservas/1"))
                .andExpect(status().isNoContent());
        verify(reservaService, times(1)).deleteReserva(1L);
    }

    @Test
    void testCancelarReserva() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setEstado("CANCELADA");
        reserva.setMesa(mesa);
        when(reservaService.cancelarReserva(1L)).thenReturn(reserva);

        // Act & Assert
        mockMvc.perform(put("/api/reservas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
        verify(reservaService, times(1)).cancelarReserva(1L);
    }
}