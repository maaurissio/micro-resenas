package com.perfulandia.resena.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.perfulandia.resena.model.Resena;
import com.perfulandia.resena.repository.ResenaRepository;
public class ResenaServiceTest {
    
    @Mock
    private ResenaRepository resenaRepository;

    @InjectMocks
    private ResenaService resenaService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarResena(){
        Resena resena = new Resena(null, 1, 1, 5, "muy bueno", LocalDateTime.now()); // Añadimos fecha
        Resena resenaGuardada = new Resena(1L, 1, 1, 5, "muy bueno", LocalDateTime.now());
        when(resenaRepository.save(resena)).thenReturn(resenaGuardada);

        Resena resultado = resenaService.guardar(resena);
        assertThat(resultado).isNotNull(); // Verificamos que no sea null
        // assertThat(resultado.getCalificacion()).isEqualTo(5); // Verificamos calificación
        // assertThat(resultado.getComentario()).isEqualTo("muy bueno"); // Verificamos comentario]
        verify(resenaRepository).save(resena);
    }

    @Test
    void testListarResenas(){
        Resena r1 = new Resena(null, 1, 1, 5, "muy bueno", null);
        Resena r2 = new Resena(null, 2, 1, 0, "muy malo", null);
        when(resenaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Resena> resultado = resenaService.resenas();
        assertThat(resultado).hasSize(2).contains(r1, r2);
        verify(resenaRepository).findAll();
    }

    @Test
    void testModificarResena() {
        Resena resenaExistente = new Resena(1L, 1, 1, 4, "original", LocalDateTime.now());
        Resena resenaActualizada = new Resena(null, 0, 0, 5, "modificado", null);
        Resena resenaModificada = new Resena(1L, 1, 1, 5, "modificado", LocalDateTime.now());

        when(resenaRepository.findById(1L)).thenReturn(java.util.Optional.of(resenaExistente));
        when(resenaRepository.save(resenaExistente)).thenReturn(resenaModificada);

        Resena resultado = resenaService.modificarResena(1L, resenaActualizada);

        assertThat(resultado.getCalificacion()).isEqualTo(5);
        assertThat(resultado.getComentario()).isEqualTo("modificado");
        verify(resenaRepository).findById(1L);
        verify(resenaRepository).save(resenaExistente);
    }

    @Test
    void testBuscarResenasPorProducto() {
        Resena filtro = new Resena(null, 0, 1, 0, null, null);
        Resena r1 = new Resena(1L, 1, 1, 5, "muy bueno", LocalDateTime.now());
        List<Resena> reseñas = Arrays.asList(r1);

        when(resenaRepository.findByIdProducto(1)).thenReturn(reseñas);

        Object resultado = resenaService.buscarResenasPorProducto(filtro);

        assertThat(resultado).isInstanceOf(List.class);
        List<Resena> resultadoList = (List<Resena>) resultado;
        assertThat(resultadoList).hasSize(1).contains(r1);
        verify(resenaRepository).findByIdProducto(1);
    }

    @Test
    void testEliminarResena() {
        Long idResena = 1L;
        when(resenaRepository.existsById(idResena)).thenReturn(true);

        resenaService.eliminarResena(idResena);

        verify(resenaRepository).existsById(idResena);
        verify(resenaRepository).deleteById(idResena);
    }

}
