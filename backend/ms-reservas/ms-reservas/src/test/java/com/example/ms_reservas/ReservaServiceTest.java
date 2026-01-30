package com.example.ms_reservas;

import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.model.Reserva;
import com.example.ms_reservas.repository.MesaRepository;
import com.example.ms_reservas.repository.ReservaRepository;
import com.example.ms_reservas.service.ReservaService;
import com.example.ms_reservas.client.UsuarioClient;
import com.example.ms_reservas.dto.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReservas() {
        // Arrange
        Reserva reserva1 = new Reserva();
        reserva1.setIdReserva(1L);
        Reserva reserva2 = new Reserva();
        reserva2.setIdReserva(2L);
        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findAll()).thenReturn(reservas);

        // Act
        List<Reserva> result = reservaService.getAllReservas();

        // Assert
        assertEquals(2, result.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void testGetReservaById() {
        // Arrange
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // Act
        Optional<Reserva> result = reservaService.getReservaById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdReserva());
        verify(reservaRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveReserva() {
        // Arrange
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setIdUsuario(1L);
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        Reserva reserva = new Reserva();
        reserva.setIdUsuario(1L);
        reserva.setMesa(mesa);
        reserva.setFecha(LocalDate.now());
        reserva.setHora(LocalTime.now());

        when(usuarioClient.getUsuarioById(1L)).thenReturn(usuario);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva result = reservaService.saveReserva(reserva);

        // Assert
        assertNotNull(result);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void testDeleteReserva() {
        // Act
        reservaService.deleteReserva(1L);

        // Assert
        verify(reservaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCancelarReserva() {
        // Arrange
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setEstado("ACTIVA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // Act
        Reserva result = reservaService.cancelarReserva(1L);

        // Assert
        assertNotNull(result);
        assertEquals("CANCELADA", result.getEstado());
        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).save(reserva);
    }
}