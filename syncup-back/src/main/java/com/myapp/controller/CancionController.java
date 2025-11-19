package com.myapp.controller;

import com.myapp.dto.CancionCreateDto;
import com.myapp.dto.CancionDto;
import com.myapp.dto.SimilitudDto;
import com.myapp.service.CancionService;
import com.myapp.service.RadioService;
import com.myapp.util.ConversorDto;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.model.Cancion;
import com.myapp.model.enums.Genero;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/canciones")
@CrossOrigin(origins = "http://localhost:3000")
public class CancionController {

    private final CancionService cancionService;
    private final RadioService radioService;

    public CancionController(CancionService cancionService, RadioService radioService) {
        this.cancionService = cancionService;
        this.radioService = radioService;
    }

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
            return ResponseEntity.ok(Map.of(
                "message", "Canción creada correctamente",
                "cancion", new CancionDto(c)
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Cancion c = cancionService.obtener(id);
        if (c == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Canción no encontrada"));
        }
        return ResponseEntity.ok(new CancionDto(c));
    }


    @GetMapping
    public ResponseEntity<?> listarTodas() {
        ListaEnlazada<Cancion> lista = cancionService.listarTodas();
        CancionDto[] respuesta = ConversorDto.listaCancionesAArray(lista);
        
        return ResponseEntity.ok(respuesta);
    }


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
            CancionDto respuesta = new CancionDto(actualizada);
            return ResponseEntity.ok(Map.of(
                "message", "Canción actualizada correctamente",
                "cancion", respuesta
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            cancionService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Canción eliminada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

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

    @GetMapping("/autocompletar")
    public ResponseEntity<?> autocompletar(
        @RequestParam String prefijo,
        @RequestParam(name = "limite", required = false, defaultValue = "10") int limite) {

    ListaEnlazada<String> sugerencias = cancionService.sugerirTitulos(prefijo, limite);
    String[] respuesta = ConversorDto.listaStringsAArray(sugerencias);

    return ResponseEntity.ok(respuesta);
}

@GetMapping("/buscar")
public ResponseEntity<?> buscar(
    @RequestParam(name = "artista", required = false) String artista,
    @RequestParam(name = "genero", required = false) String generoStr,
    @RequestParam(name = "año", required = false) Integer año,
    @RequestParam(name = "modo", required = false, defaultValue = "AND") String modo) {

    Genero genero = null;
    if (generoStr != null && !generoStr.isBlank()) {
        try {
            genero = Genero.valueOf(generoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Género inválido: " + generoStr)
            );
        }
    }

    boolean usarAnd = !"OR".equalsIgnoreCase(modo);

    var lista = cancionService.buscar(artista, genero, año, usarAnd);

    CancionDto[] respuesta = ConversorDto.listaCancionesAArray(lista);

    return ResponseEntity.ok(respuesta);
}


@GetMapping("/{id}/radio")
public ResponseEntity<?> generarRadio(
        @PathVariable Long id,
        @RequestParam(name = "max", required = false, defaultValue = "20") int max) {
    try {
        ListaEnlazada<Cancion> lista = radioService.generarRadio(id, max);
        CancionDto[] respuesta = ConversorDto.listaCancionesAArray(lista);

        return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}

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

    

}

