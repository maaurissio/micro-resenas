package com.perfulandia.resena.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/resena")
@Tag(name = "Reseñas", description = "API para gestión de reseñas de productos")
public class ResenaController {
    
    @Autowired
    private ResenaService resenaService;

    @GetMapping
    @Operation(summary = "Obtener todas las reseñas", description = "Retorna una lista de todas las reseñas disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente",
                content = @Content(schema = @Schema(implementation = Resena.class))),
        @ApiResponse(responseCode = "204", description = "No hay reseñas disponibles")
    })
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> getResenas(){
        List<Resena> resenas = resenaService.resenas();
        if (resenas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        // Agregar enlaces HATEOAS a cada reseña
        List<EntityModel<Resena>> resenasConEnlaces = resenas.stream()
            .map(resena -> EntityModel.of(resena)
                .add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(resena.getIdResena())).withSelfRel())
                .add(linkTo(methodOn(ResenaController.class).modificarResena(resena.getIdResena(), null)).withRel("actualizar"))
                .add(linkTo(methodOn(ResenaController.class).eliminarResena(resena.getIdResena())).withRel("eliminar")))
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Resena>> collectionModel = CollectionModel.of(resenasConEnlaces)
            .add(linkTo(methodOn(ResenaController.class).getResenas()).withSelfRel())
            .add(linkTo(methodOn(ResenaController.class).guardar(null)).withRel("crear"));
        
        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reseña por ID", description = "Retorna una reseña específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña encontrada",
                content = @Content(schema = @Schema(implementation = Resena.class))),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public ResponseEntity<EntityModel<Resena>> obtenerResenaPorId(
            @Parameter(description = "ID de la reseña", required = true) @PathVariable Long id) {
        try {
            Resena resena = resenaService.buscarPorId(id);
            EntityModel<Resena> resenaModel = EntityModel.of(resena)
                .add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(id)).withSelfRel())
                .add(linkTo(methodOn(ResenaController.class).getResenas()).withRel("todas"))
                .add(linkTo(methodOn(ResenaController.class).modificarResena(id, null)).withRel("actualizar"))
                .add(linkTo(methodOn(ResenaController.class).eliminarResena(id)).withRel("eliminar"));
            
            return new ResponseEntity<>(resenaModel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Crear nueva reseña", description = "Crea una nueva reseña para un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
                content = @Content(schema = @Schema(implementation = Resena.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<EntityModel<Resena>> guardar(
            @Parameter(description = "Datos de la reseña a crear", required = true) @RequestBody Resena resena){
        try {
            resena.setFecha_resena(LocalDateTime.now());
            Resena resenaGuardada = resenaService.guardar(resena);
            
            EntityModel<Resena> resenaModel = EntityModel.of(resenaGuardada)
                .add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(resenaGuardada.getIdResena())).withSelfRel())
                .add(linkTo(methodOn(ResenaController.class).getResenas()).withRel("todas"))
                .add(linkTo(methodOn(ResenaController.class).modificarResena(resenaGuardada.getIdResena(), null)).withRel("actualizar"))
                .add(linkTo(methodOn(ResenaController.class).eliminarResena(resenaGuardada.getIdResena())).withRel("eliminar"));
            
            return new ResponseEntity<>(resenaModel, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idResena}")
    @Operation(summary = "Actualizar reseña", description = "Actualiza una reseña existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente",
                content = @Content(schema = @Schema(implementation = Resena.class))),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<EntityModel<Resena>> modificarResena(
            @Parameter(description = "ID de la reseña a actualizar", required = true) @PathVariable Long idResena, 
            @Parameter(description = "Nuevos datos de la reseña", required = true) @RequestBody Resena resena) {
        try {
            Resena resenaModificada = resenaService.modificarResena(idResena, resena);
            
            EntityModel<Resena> resenaModel = EntityModel.of(resenaModificada)
                .add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(idResena)).withSelfRel())
                .add(linkTo(methodOn(ResenaController.class).getResenas()).withRel("todas"))
                .add(linkTo(methodOn(ResenaController.class).eliminarResena(idResena)).withRel("eliminar"));
            
            return new ResponseEntity<>(resenaModel, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/buscar")
    @Operation(summary = "Buscar reseñas por producto", description = "Busca reseñas filtradas por producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reseñas encontradas",
                content = @Content(schema = @Schema(implementation = Resena.class))),
        @ApiResponse(responseCode = "404", description = "No se encontraron reseñas para el producto")
    })
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> buscarResenasPorProducto(
            @Parameter(description = "Filtro de búsqueda", required = true) @RequestBody Resena filtro) {
        Object resultado = resenaService.buscarResenasPorProducto(filtro);
        if (resultado instanceof String) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        @SuppressWarnings("unchecked")
        List<Resena> resenas = (List<Resena>) resultado;
        
        List<EntityModel<Resena>> resenasConEnlaces = resenas.stream()
            .map(resena -> EntityModel.of(resena)
                .add(linkTo(methodOn(ResenaController.class).obtenerResenaPorId(resena.getIdResena())).withSelfRel())
                .add(linkTo(methodOn(ResenaController.class).modificarResena(resena.getIdResena(), null)).withRel("actualizar"))
                .add(linkTo(methodOn(ResenaController.class).eliminarResena(resena.getIdResena())).withRel("eliminar")))
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Resena>> collectionModel = CollectionModel.of(resenasConEnlaces)
            .add(linkTo(methodOn(ResenaController.class).buscarResenasPorProducto(filtro)).withSelfRel())
            .add(linkTo(methodOn(ResenaController.class).getResenas()).withRel("todas"));
        
        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @DeleteMapping("/{idResena}")
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reseña eliminada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al eliminar la reseña")
    })
    public ResponseEntity<Void> eliminarResena(
            @Parameter(description = "ID de la reseña a eliminar", required = true) @PathVariable Long idResena){
        try {
            resenaService.eliminarResena(idResena);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
