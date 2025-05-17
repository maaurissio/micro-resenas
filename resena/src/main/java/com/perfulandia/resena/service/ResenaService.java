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
        return resenaRepository.save(resena);
    }
}
