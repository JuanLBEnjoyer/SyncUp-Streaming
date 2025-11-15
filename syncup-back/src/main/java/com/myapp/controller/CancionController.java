package com.myapp.controller;

import com.myapp.dto.CancionCreateDto;
import com.myapp.dto.CancionDto;
import com.myapp.dto.SimilitudDto;
import com.myapp.service.CancionService;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.ClasesPropias.Iterador.IteradorPropio;
import com.myapp.model.Cancion;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/canciones")
@CrossOrigin(origins = "http://localhost:3000")
public class CancionController {

    private final CancionService cancionService;

    public CancionController(CancionService cancionService) {
        this.cancionService = cancionService;
    }

    // --------- CREAR CANCIÓN ---------

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CancionCreateDto dto) {
        try {
            Cancion c = cancionService.crear(
                dto.getTitulo(),
                dto.getArtista(),
                dto.getGenero(),
                dto.getAnio(),
                dto.getDuracion()
            );
            return ResponseEntity.ok(new CancionDto(c));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --------- OBTENER UNA CANCIÓN ---------

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Cancion c = cancionService.obtener(id);
        if (c == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Canción no encontrada"));
        }
        return ResponseEntity.ok(new CancionDto(c));
    }

    // --------- LISTAR TODAS ---------

    @GetMapping
    public ResponseEntity<List<CancionDto>> listarTodas() {
        ListaEnlazada<Cancion> lista = cancionService.listarTodas();
        List<CancionDto> respuesta = new ArrayList<>();

        for (int i = 0; i < lista.tamaño(); i++) {
            respuesta.add(new CancionDto(lista.obtener(i)));
        }

        return ResponseEntity.ok(respuesta);
    }

    // --------- ACTUALIZAR ---------

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody CancionCreateDto dto) {
        try {
            Cancion existente = cancionService.obtener(id);
            if (existente == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Canción no encontrada"));
            }

            existente.setTitulo(dto.getTitulo());
            existente.setArtista(dto.getArtista());
            existente.setGenero(dto.getGenero());
            existente.setAño(dto.getAnio());
            existente.setDuracion(dto.getDuracion());

            Cancion actualizada = cancionService.actualizar(existente);
            return ResponseEntity.ok(new CancionDto(actualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --------- ELIMINAR ---------

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            cancionService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Canción eliminada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --------- ESTABLECER SIMILITUD ENTRE DOS CANCIONES ---------

    @PostMapping("/{id1}/similitud/{id2}")
    public ResponseEntity<?> establecerSimilitud(@PathVariable Long id1,
                                                 @PathVariable Long id2,
                                                 @Valid @RequestBody SimilitudDto dto) {
        try {
            cancionService.relacionar(id1, id2, dto.getSimilitud());
            return ResponseEntity.ok(Map.of("message", "Similitud establecida correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --------- OBTENER CANCIONES SIMILARES A UNA ---------

    @GetMapping("/{id}/similares")
    public ResponseEntity<?> obtenerSimilares(@PathVariable Long id) {
        try {
            SetPropio<Cancion> similares = cancionService.similares(id);
            List<CancionDto> respuesta = new ArrayList<>();

            IteradorPropio<Cancion> it = similares.iterador();
            while (it.tieneSiguiente()) {
                respuesta.add(new CancionDto(it.siguiente()));
            }

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --------- OBTENER RUTA MÁS SIMILAR ENTRE DOS CANCIONES ---------

    @GetMapping("/ruta/{origen}/{destino}")
    public ResponseEntity<?> rutaMasSimilar(@PathVariable Long origen,
                                            @PathVariable Long destino) {
        try {
            ListaEnlazada<Cancion> ruta = cancionService.caminoMasSimilar(origen, destino);
            List<CancionDto> respuesta = new ArrayList<>();

            for (int i = 0; i < ruta.tamaño(); i++) {
                respuesta.add(new CancionDto(ruta.obtener(i)));
            }

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --------- MANEJO DE VALIDACIONES (@Valid) ---------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(Map.of(
            "message", "Error de validación",
            "errors", errors
        ));
    }

    @GetMapping("/autocompletar")
    public ResponseEntity<?> autocompletar(
        @RequestParam String prefijo,
        @RequestParam(name = "limite", required = false, defaultValue = "10") int limite) {

    ListaEnlazada<String> sugerencias = cancionService.sugerirTitulos(prefijo, limite);
    List<String> respuesta = new ArrayList<>();

    for (int i = 0; i < sugerencias.tamaño(); i++) {
        respuesta.add(sugerencias.obtener(i));
    }

    return ResponseEntity.ok(respuesta);
}

}

