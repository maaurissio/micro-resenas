package com.perfulandia.resena.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/{idResena}")
    public ResponseEntity<Object> modificarResena(@PathVariable Long idResena, @RequestBody Resena resena) {
        try {
            Resena resenaModificada = resenaService.modificarResena(idResena, resena);
            return new ResponseEntity<>(resenaModificada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/buscar")
    public ResponseEntity<Object> buscarResenasPorProducto(@RequestBody Resena filtro) {
        Object resultado = resenaService.buscarResenasPorProducto(filtro);
        if (resultado instanceof String) {
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @DeleteMapping("/{idResena}")
    public ResponseEntity<Object> eliminarResena(@PathVariable Long idResena){
        try {
            resenaService.eliminarResena(idResena);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
