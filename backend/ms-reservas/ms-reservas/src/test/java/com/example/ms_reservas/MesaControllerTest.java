package com.example.ms_reservas;

import com.example.ms_reservas.controller.MesaController;
import com.example.ms_reservas.model.Mesa;
import com.example.ms_reservas.service.MesaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MesaController.class)
class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MesaService mesaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllMesas() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        mesa.setNumero(1);
        mesa.setCapacidad(4);
        mesa.setDisponible(true);
        List<Mesa> mesas = Arrays.asList(mesa);
        when(mesaService.getAllMesas()).thenReturn(mesas);

        // Act & Assert
        mockMvc.perform(get("/api/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMesa").value(1));
        verify(mesaService, times(1)).getAllMesas();
    }

    @Test
    void testGetMesaById() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        mesa.setNumero(1);
        when(mesaService.getMesaById(1L)).thenReturn(Optional.of(mesa));

        // Act & Assert
        mockMvc.perform(get("/api/mesas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMesa").value(1));
        verify(mesaService, times(1)).getMesaById(1L);
    }

    @Test
    void testCreateMesa() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setNumero(1);
        mesa.setCapacidad(4);
        mesa.setDisponible(true);
        when(mesaService.saveMesa(any(Mesa.class))).thenReturn(mesa);

        // Act & Assert
        mockMvc.perform(post("/api/mesas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mesa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(1));
        verify(mesaService, times(1)).saveMesa(any(Mesa.class));
    }

    @Test
    void testUpdateMesa() throws Exception {
        // Arrange
        Mesa mesa = new Mesa();
        mesa.setIdMesa(1L);
        mesa.setNumero(1);
        when(mesaService.getMesaById(1L)).thenReturn(Optional.of(mesa));
        when(mesaService.saveMesa(any(Mesa.class))).thenReturn(mesa);

        // Act & Assert
        mockMvc.perform(put("/api/mesas/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mesa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMesa").value(1));
        verify(mesaService, times(1)).saveMesa(any(Mesa.class));
    }

    @Test
    void testDeleteMesa() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/mesas/1"))
                .andExpect(status().isNoContent());
        verify(mesaService, times(1)).deleteMesa(1L);
    }
}