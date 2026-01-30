package com.example.ms_reservas.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mesa")
@Data
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private Long idMesa;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "disponible", nullable = false)
    private Boolean disponible;
}