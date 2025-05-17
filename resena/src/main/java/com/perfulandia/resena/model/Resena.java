package com.perfulandia.resena.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "resena")
@NoArgsConstructor
@AllArgsConstructor

public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_resena;
    private int clienteId;
    private int id_producto;
    private int calificacion;

    @Column(length = 250, nullable = true)
    private String comentario;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Santiago")
    private LocalDateTime fecha_resena;
}
