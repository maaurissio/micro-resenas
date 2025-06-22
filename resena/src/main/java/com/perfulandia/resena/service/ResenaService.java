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

    public Resena modificarResena(Long idResena, Resena resenaActualizada) {
        Resena resenaExistente = resenaRepository.findById(idResena).orElse(null);
        if (resenaExistente == null) {
            throw new IllegalArgumentException("Reseña no encontrada con ID: " + idResena);
        }

        if (resenaActualizada.getComentario() != null) {
            resenaExistente.setComentario(resenaActualizada.getComentario());
        }
        if (resenaActualizada.getCalificacion() != 0) { // Usamos 0 como valor por defecto para no actualizar
            if (resenaActualizada.getCalificacion() < 1 || resenaActualizada.getCalificacion() > 5) {
                throw new IllegalArgumentException("La calificación debe estar entre 1 y 5");
            }
            resenaExistente.setCalificacion(resenaActualizada.getCalificacion());
        }

        return resenaRepository.save(resenaExistente);
    }

    public Object buscarResenasPorProducto(Resena filtro) {
        if (filtro == null || filtro.getIdProducto() <= 0) {
            return "ID de producto inválido";
        }
        List<Resena> reseñas = resenaRepository.findByIdProducto(filtro.getIdProducto());
        return reseñas.isEmpty() ? "No hay reseñas para el producto con ID: " + filtro.getIdProducto() : reseñas;
    }

    public void eliminarResena(Long idResena){
        if(!resenaRepository.existsById(idResena)){
            throw new IllegalArgumentException("Reseña no encontrada");
        }
        resenaRepository.deleteById(idResena);
    }

}
