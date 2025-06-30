package com.perfulandia.resena.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfulandia.resena.model.Resena;
import com.perfulandia.resena.service.ResenaService;

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ResenaService resenaService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testCrearResena201Created() throws Exception {
                // Given
                Resena resena = new Resena(1L, 1, 1, 5, "Comentario", LocalDateTime.now());
                Mockito.when(resenaService.guardar(any())).thenReturn(resena);

                // When & Then
                mockMvc.perform(post("/api/resena")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(resena)))
                                .andExpect(status().isCreated()); // 201
        }

        @Test
        void testCrearResena400BadRequest() throws Exception {
                // Given
                Mockito.when(resenaService.guardar(any()))
                                .thenThrow(new IllegalArgumentException("La calificación debe estar entre 1 y 5"));
                Resena resenaInvalida = new Resena(null, 1, 1, 6, "Comentario", null);

                // When & Then
                mockMvc.perform(post("/api/resena")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(resenaInvalida)))
                                .andExpect(status().isBadRequest()); // 400
        }

        @Test
        void testObtenerTodasResenas200OK() throws Exception {
                // Given
                List<Resena> resenas = Arrays.asList(
                                new Resena(1L, 1, 1, 5, "Comentario 1", LocalDateTime.now()),
                                new Resena(2L, 2, 1, 4, "Comentario 2", LocalDateTime.now()));
                Mockito.when(resenaService.resenas()).thenReturn(resenas);

                // When & Then
                mockMvc.perform(get("/api/resena"))
                                .andExpect(status().isOk()); // 200
        }

        @Test
        void testObtenerTodas204NoContent() throws Exception {
                // Given
                Mockito.when(resenaService.resenas()).thenReturn(List.of());

                // When & Then
                mockMvc.perform(get("/api/resena"))
                                .andExpect(status().isNoContent()); // 204
        }

        @Test
        void testActualizarResena200OK() throws Exception {
                // Given
                Resena resenaModificada = new Resena(1L, 1, 1, 4, "Actualizado", LocalDateTime.now());
                Mockito.when(resenaService.modificarResena(eq(1L), any()))
                                .thenReturn(resenaModificada);
                Resena resenaRequest = new Resena();
                resenaRequest.setComentario("Actualizado");
                resenaRequest.setCalificacion(4);

                // When & Then
                mockMvc.perform(put("/api/resena/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(resenaRequest)))
                                .andExpect(status().isOk()); // 200
        }

        @Test
        void testActualizar404NotFound() throws Exception {
                // Given
                Mockito.when(resenaService.modificarResena(eq(1L), any()))
                                .thenThrow(new IllegalArgumentException("Reseña no encontrada con ID: 1"));
                Resena resenaRequest = new Resena();
                resenaRequest.setComentario("Actualizado");

                // When & Then
                mockMvc.perform(put("/api/resena/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(resenaRequest)))
                                .andExpect(status().isNotFound()); // 404
        }

        @Test
        void testActualizarResena400BadRequest() throws Exception {
                // Given
                Mockito.when(resenaService.modificarResena(eq(1L), any()))
                                .thenThrow(new IllegalArgumentException("La calificación debe estar entre 1 y 5"));
                Resena resenaRequest = new Resena();
                resenaRequest.setCalificacion(6); // Calificación inválida

                // When & Then
                mockMvc.perform(put("/api/resena/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(resenaRequest)))
                                .andExpect(status().isBadRequest()); // 400
        }

        @Test
        void testBuscarResenasPorIdProducto200OK() throws Exception {
                // Given
                List<Resena> resenas = Arrays.asList(
                                new Resena(1L, 1, 1, 5, "Comentario 1", LocalDateTime.now()),
                                new Resena(2L, 2, 1, 4, "Comentario 2", LocalDateTime.now()));
                Mockito.when(resenaService.buscarResenasPorProducto(any())).thenReturn(resenas);
                Resena filtro = new Resena();
                filtro.setIdProducto(1);

                // When & Then
                mockMvc.perform(post("/api/resena/buscar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filtro)))
                                .andExpect(status().isOk()); // 200
        }

        @Test
        void testBuscarResenasPorProducto404NotFound() throws Exception {
                // Given
                Mockito.when(resenaService.buscarResenasPorProducto(any()))
                                .thenReturn("No hay reseñas para el producto con ID: 999");
                Resena filtro = new Resena();
                filtro.setIdProducto(999);

                // When & Then
                mockMvc.perform(post("/api/resena/buscar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filtro)))
                                .andExpect(status().isNotFound()); // 404
        }

        @Test
        void testEliminar204NoContent() throws Exception {
                // Given
                doNothing().when(resenaService).eliminarResena(1L);

                // When & Then
                mockMvc.perform(delete("/api/resena/1"))
                                .andExpect(status().isNoContent()); // 204
        }

        @Test
        void testEliminar404NotFound() throws Exception {
                // Given
                doThrow(new IllegalArgumentException("Reseña no encontrada"))
                                .when(resenaService).eliminarResena(1L);

                // When & Then
                mockMvc.perform(delete("/api/resena/1"))
                                .andExpect(status().isNotFound()); // 404
        }

        @Test
        void testEliminar400BadRequest() throws Exception {
                // Given
                doThrow(new IllegalArgumentException("Error de validación"))
                                .when(resenaService).eliminarResena(1L);

                // When & Then
                mockMvc.perform(delete("/api/resena/1"))
                                .andExpect(status().isBadRequest()); // 400
        }
}