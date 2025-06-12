package com.perfulandia.resena.service;

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
        Resena resena = new Resena(1, 1, 1, 5, "muy bueno", null);
        //Resena r2 = new Resena(2, 2, 1, 1, "muy malo", null);
        Resena resenaGuardada = new Resena(1, 1, 5, 0, "muy bueno", null);
        when(resenaRepository.save(resena)).thenReturn(resenaGuardada);

        Resena resultado = resenaService.guardar(resena);
        assertThat(resultado.getIdResena()).isEqualTo(1);
        verify(resenaRepository).save(resena);
    }

    @Test
    void testListarResenas(){
        Resena r1 = new Resena(1, 1, 1, 5, "muy bueno", null);
        Resena r2 = new Resena(2, 2, 1, 0, "muy malo", null);
        when(resenaRepository.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<Resena> resultado = resenaService.resenas();
        assertThat(resultado).hasSize(2).contains(r1, r2);
        verify(resenaRepository).findAll();
    }

}
