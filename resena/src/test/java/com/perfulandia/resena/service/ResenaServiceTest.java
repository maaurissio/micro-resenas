package com.perfulandia.resena.service;

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


// @Test
//     void testGuardarMascota() {
//         Mascota mascota = new Mascota(null, "Rex", "Perro", 5);
//         Mascota mascotaGuardada = new Mascota(1L, "Rex", "Perro", 5);
//         when(mascotaRepository.save(mascota)).thenReturn(mascotaGuardada);

//         Mascota resultado = mascotaService.guardarMascota(mascota);
//         assertThat(resultado.getId()).isEqualTo(1L);
//         verify(mascotaRepository).save(mascota);
//     }












}
