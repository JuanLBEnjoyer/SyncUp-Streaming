package com.myapp.controller;

import com.myapp.dto.UsuarioDto;
import com.myapp.dto.UsuarioPerfilDto;
import com.myapp.dto.CancionDto;
import com.myapp.service.RecomendacionService;
import com.myapp.service.UsuarioService;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.model.Usuario;
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
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RecomendacionService recomendacionService;

    public UsuarioController(UsuarioService usuarioService, RecomendacionService recomendacionService) {
        this.usuarioService = usuarioService;
        this.recomendacionService = recomendacionService;
    }

    // ---------- OBTENER PERFIL ----------

    @GetMapping("/{user}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable String user) {
        Usuario u = usuarioService.obtenerUsuario(user);
        if (u == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Usuario no encontrado"));
        }
        UsuarioDto dto = usuarioService.toDto(u);
        return ResponseEntity.ok(dto);
    }

    // ---------- ACTUALIZAR PERFIL (nombre + password) ----------

    @PutMapping("/{user}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable String user,
                                              @Valid @RequestBody UsuarioPerfilDto dto) {
        try {
            Usuario u = usuarioService.actualizarPerfil(user, dto.getNombre(), dto.getPassword());
            UsuarioDto out = usuarioService.toDto(u);
            return ResponseEntity.ok(out);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ---------- OBTENER CANCIONES FAVORITAS ----------

    @GetMapping("/{user}/favoritos")
    public ResponseEntity<?> obtenerFavoritos(@PathVariable String user) {
        try {
            ListaEnlazada<Cancion> favoritos = usuarioService.obtenerFavoritos(user);
            List<CancionDto> respuesta = new ArrayList<>();

            for (int i = 0; i < favoritos.tamaño(); i++) {
                respuesta.add(new CancionDto(favoritos.obtener(i)));
            }

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ---------- AGREGAR FAVORITO ----------

    @PostMapping("/{user}/favoritos/{idCancion}")
    public ResponseEntity<?> agregarFavorito(@PathVariable String user,
                                             @PathVariable Long idCancion) {
        try {
            usuarioService.agregarFavorito(user, idCancion);
            return ResponseEntity.ok(Map.of("message", "Canción agregada a favoritos"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ---------- ELIMINAR FAVORITO ----------

    @DeleteMapping("/{user}/favoritos/{idCancion}")
    public ResponseEntity<?> eliminarFavorito(@PathVariable String user,
                                              @PathVariable Long idCancion) {
        try {
            usuarioService.eliminarFavorito(user, idCancion);
            return ResponseEntity.ok(Map.of("message", "Canción eliminada de favoritos"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{user}/descubrimiento-semanal")
public ResponseEntity<?> descubrimientoSemanal(
        @PathVariable String user,
        @RequestParam(name = "max", required = false, defaultValue = "20") int max) {

    try {
        var lista = recomendacionService.generarDescubrimientoSemanal(user, max);

        List<CancionDto> respuesta = new ArrayList<>();
        for (int i = 0; i < lista.tamaño(); i++) {
            respuesta.add(new CancionDto(lista.obtener(i)));
        }

        return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}


    // ---------- MANEJO DE ERRORES DE VALIDACIÓN ----------

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

