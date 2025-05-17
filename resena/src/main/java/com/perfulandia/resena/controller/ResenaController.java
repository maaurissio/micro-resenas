package com.perfulandia.resena.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfulandia.resena.model.Resena;
import com.perfulandia.resena.service.ResenaService;

@RestController
@RequestMapping("api/resena")
public class ResenaController {
    @Autowired
    private ResenaService resenaService;

    @GetMapping
    public ResponseEntity<List<Resena>> getResenas(){
        List<Resena> resenas = resenaService.resenas();
        if (resenas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(resenas, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Resena> guardar(@RequestBody Resena resena){
        resena.setFecha_resena(LocalDateTime.now());
        Resena resenaGuardada = resenaService.guardar(resena);
        return new ResponseEntity<>(resenaGuardada, HttpStatus.CREATED);
    }
}
