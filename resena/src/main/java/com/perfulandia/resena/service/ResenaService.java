package com.perfulandia.resena.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfulandia.resena.model.Resena;
import com.perfulandia.resena.repository.ResenaRepository;

@Service
public class ResenaService {
    @Autowired
    private ResenaRepository resenaRepository;

    public List<Resena> resenas(){
        return resenaRepository.findAll();
    }

    public Resena guardar(Resena resena){
        if(resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
        }
        return resenaRepository.save(resena);
    }
}
