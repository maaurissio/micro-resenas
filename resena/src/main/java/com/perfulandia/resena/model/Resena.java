package com.perfulandia.resena.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa una reseña de producto")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reseña", example = "1")
    private Long idResena;

    @Schema(description = "ID del cliente que realizó la reseña", example = "123", required = true)
    private int idCliente;

    @Schema(description = "ID del producto reseñado", example = "456", required = true)
    private int idProducto;

    @Schema(description = "Calificación del producto del 1 al 5", example = "5", minimum = "1", maximum = "5", required = true)
    private int calificacion;

    @Column(length = 250)
    @Schema(description = "Comentario sobre el producto", example = "Excelente producto, muy recomendado")
    private String comentario;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Santiago")
    @Schema(description = "Fecha y hora cuando se creó la reseña", example = "2025-06-30 14:30:00")
    private LocalDateTime fecha_resena;
}
