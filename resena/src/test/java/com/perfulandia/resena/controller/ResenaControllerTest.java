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
    void testCrearResena_Created() throws Exception {
        Resena resena = new Resena(1L, 1, 1, 5, "Comentario", LocalDateTime.now());
        Mockito.when(resenaService.guardar(any())).thenReturn(resena);

        mockMvc.perform(post("/api/resena")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resena)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCrearResena_BadRequest() throws Exception {
        Mockito.when(resenaService.guardar(any()))
                .thenThrow(new IllegalArgumentException("La calificación debe estar entre 1 y 5"));

        Resena resenaInvalida = new Resena(null, 1, 1, 6, "Comentario", null);
        mockMvc.perform(post("/api/resena")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resenaInvalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testObtenerTodas_OK() throws Exception {
        List<Resena> resenas = Arrays.asList(
            new Resena(1L, 1, 1, 5, "Comentario 1", LocalDateTime.now()),
            new Resena(2L, 2, 1, 4, "Comentario 2", LocalDateTime.now())
        );
        Mockito.when(resenaService.resenas()).thenReturn(resenas);

        mockMvc.perform(get("/api/resena"))
                .andExpect(status().isOk());
    }

    @Test
    void testObtenerTodas_NoContent() throws Exception {
        Mockito.when(resenaService.resenas()).thenReturn(List.of());

        mockMvc.perform(get("/api/resena"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testActualizar_OK() throws Exception {
        Resena resenaModificada = new Resena(1L, 1, 1, 4, "Actualizado", LocalDateTime.now());
        Mockito.when(resenaService.modificarResena(eq(1L), any()))
                .thenReturn(resenaModificada);

        Resena resenaRequest = new Resena();
        resenaRequest.setComentario("Actualizado");
        resenaRequest.setCalificacion(4);

        mockMvc.perform(put("/api/resena/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizar_NotFound() throws Exception {
        Mockito.when(resenaService.modificarResena(eq(1L), any()))
                .thenThrow(new IllegalArgumentException("Reseña no encontrada con ID: 1"));

        Resena resenaRequest = new Resena();
        resenaRequest.setComentario("Actualizado");
        resenaRequest.setCalificacion(4);

        mockMvc.perform(put("/api/resena/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizar_BadRequest() throws Exception {
        Mockito.when(resenaService.modificarResena(eq(1L), any()))
                .thenThrow(new IllegalArgumentException("La calificación debe estar entre 1 y 5"));

        Resena resenaRequest = new Resena();
        resenaRequest.setCalificacion(6); // Calificación inválida

        mockMvc.perform(put("/api/resena/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resenaRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBuscarResenasPorProducto_OK() throws Exception {
        List<Resena> resenas = Arrays.asList(
            new Resena(1L, 1, 1, 5, "Comentario 1", LocalDateTime.now()),
            new Resena(2L, 2, 1, 4, "Comentario 2", LocalDateTime.now())
        );
        Mockito.when(resenaService.buscarResenasPorProducto(any())).thenReturn(resenas);

        Resena filtro = new Resena();
        filtro.setIdProducto(1);

        mockMvc.perform(post("/api/resena/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarResenasPorProducto_NotFound() throws Exception {
        Mockito.when(resenaService.buscarResenasPorProducto(any()))
                .thenReturn("No hay reseñas para el producto con ID: 999");

        Resena filtro = new Resena();
        filtro.setIdProducto(999);

        mockMvc.perform(post("/api/resena/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminar_NoContent() throws Exception {
        // El servicio no lanza excepción cuando la eliminación es exitosa
        doNothing().when(resenaService).eliminarResena(1L);

        mockMvc.perform(delete("/api/resena/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminar_BadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Reseña no encontrada"))
                .when(resenaService).eliminarResena(1L);

        mockMvc.perform(delete("/api/resena/1"))
                .andExpect(status().isBadRequest());
    }
}