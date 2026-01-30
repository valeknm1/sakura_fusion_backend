package com.example.ms_reservas;

import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.repository.MesaRepository;
import com.example.ms_reservas.service.MesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @InjectMocks
    private MesaService mesaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMesas() {
        // Arrange
        Mesa mesa1 = new Mesa();
        mesa1.setIdMesa(1L);
        mesa1.setNumero(1);
        Mesa mesa2 = new Mesa();
        mesa2.setIdMesa(2L);
        mesa2.setNumero(2);
        List<Mesa> mesas = Arrays.asList(mesa1, mesa2);
        when(mesaRepository.findAll()).thenReturn(mesas);

        // Act
        List<Mesa> result = mesaService.getAllMesas();

        // Assert
        assertEquals(2, result.size());
        verify(mesaRepository, times(1)).findAll();
    }

    @Test
    void testGetMesaById() {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        mesa.setNumero(1);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        // Act
        Optional<Mesa> result = mesaService.getMesaById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdMesa());
        verify(mesaRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveMesa() {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setNumero(1);
        mesa.setCapacidad(4);
        mesa.setDisponible(true);
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesa);

        // Act
        Mesa result = mesaService.saveMesa(mesa);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getNumero());
        verify(mesaRepository, times(1)).save(mesa);
    }

    @Test
    void testDeleteMesa() {
        // Act
        mesaService.deleteMesa(1L);

        // Assert
        verify(mesaRepository, times(1)).deleteById(1L);
    }
}