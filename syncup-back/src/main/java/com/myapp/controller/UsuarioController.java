package com.myapp.controller;

import com.myapp.dto.UsuarioDto;
import com.myapp.dto.UsuarioPerfilDto;
import com.myapp.dto.CancionDto;
import com.myapp.service.CsvService;
import com.myapp.service.GrafoSocialService;
import com.myapp.service.RecomendacionService;
import com.myapp.service.UsuarioService;
import com.myapp.util.ConversorDto;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.model.Usuario;
import com.myapp.model.Cancion;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.util.Map;


@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RecomendacionService recomendacionService;
    private final GrafoSocialService grafoSocialService;
    private final CsvService csvService;

    public UsuarioController(UsuarioService usuarioService, RecomendacionService recomendacionService, GrafoSocialService grafoSocialService, CsvService csvService) {
        this.usuarioService = usuarioService;
        this.recomendacionService = recomendacionService;
        this.grafoSocialService = grafoSocialService;
        this.csvService = csvService;
    }

    @GetMapping("/{user}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable String user) {
        Usuario u = usuarioService.obtenerUsuario(user);
        if (u == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Usuario no encontrado"));
        }
        UsuarioDto dto = usuarioService.toDto(u);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{user}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable String user,
                                            @Valid @RequestBody UsuarioPerfilDto dto) {
        try {
            Usuario u = usuarioService.actualizarPerfil(user, dto.getNombre(), dto.getPassword());
            UsuarioDto out = usuarioService.toDto(u);
            return ResponseEntity.ok(Map.of(
                "message", "Perfil actualizado correctamente",
                "usuario", out
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{user}/favoritos")
    public ResponseEntity<?> obtenerFavoritos(@PathVariable String user) {
        try {
            ListaEnlazada<Cancion> favoritos = usuarioService.obtenerFavoritos(user);
            CancionDto[] respuesta = ConversorDto.listaCancionesAArray(favoritos);

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

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
            CancionDto[] respuesta = ConversorDto.listaCancionesAArray(lista);

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{userA}/seguir/{userB}")
    public ResponseEntity<?> seguir(@PathVariable String userA,
                                    @PathVariable String userB) {
        try {
            Usuario usuarioA = usuarioService.obtenerUsuario(userA);
            Usuario usuarioB = usuarioService.obtenerUsuario(userB);

            if (usuarioA == null) {
                return ResponseEntity.status(404)
                    .body(Map.of("message", "Usuario " + userA + " no encontrado"));
            }

            if (usuarioB == null) {
                return ResponseEntity.status(404)
                    .body(Map.of("message", "Usuario " + userB + " no encontrado"));
            }

            grafoSocialService.seguir(userA, userB);

            return ResponseEntity.ok(Map.of(
                "message", userA + " ahora sigue a " + userB,
                "siguiendo", userB
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{userA}/seguir/{userB}")
    public ResponseEntity<?> dejarDeSeguir(@PathVariable String userA,
                                        @PathVariable String userB) {
        try {
            grafoSocialService.dejarDeSeguir(userA, userB);

            return ResponseEntity.ok(Map.of(
                "message", userA + " dejó de seguir a " + userB
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{user}/seguidos")
    public ResponseEntity<?> obtenerSeguidos(@PathVariable String user) {
        try {
            SetPropio<String> seguidosIds = grafoSocialService.seguidos(user);
            UsuarioDto[] seguidos = ConversorDto.setUsuariosAArray(
                seguidosIds, 
                usuarioService::obtenerUsuario
            );

            return ResponseEntity.ok(Map.of(
                "user", user,
                "seguidos", seguidos,
                "total", seguidos.length
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{user}/seguidores")
    public ResponseEntity<?> obtenerSeguidores(@PathVariable String user) {
        try {
             SetPropio<String> seguidoresIds = grafoSocialService.seguidores(user);
            UsuarioDto[] seguidores = ConversorDto.setUsuariosAArray(
                seguidoresIds, 
                usuarioService::obtenerUsuario
            );

            return ResponseEntity.ok(Map.of(
                "user", user,
                "seguidores", seguidores,
                "total", seguidores.length
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{userA}/sigue/{userB}")
    public ResponseEntity<?> verificarSiSigue(@PathVariable String userA,
                                            @PathVariable String userB) {
        try {
            SetPropio<String> seguidos = grafoSocialService.seguidos(userA);
            boolean sigue = seguidos.contiene(userB);

            return ResponseEntity.ok(Map.of(
                "userA", userA,
                "userB", userB,
                "sigue", sigue
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{user}/sugerencias")
    public ResponseEntity<?> obtenerSugerencias(
            @PathVariable String user,
            @RequestParam(name = "limite", required = false, defaultValue = "10") int limite) {
        try {
            ListaEnlazada<String> sugerenciasIds = 
                grafoSocialService.sugerenciasIds(user, limite);
            
            UsuarioDto[] sugerencias = ConversorDto.listaUsernamesAArray(
                sugerenciasIds, 
                usuarioService::obtenerUsuario
            );

            return ResponseEntity.ok(Map.of(
                "user", user,
                "sugerencias", sugerencias,
                "total", sugerencias.length
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{user}/favoritos/csv")
    public ResponseEntity<?> descargarFavoritosCSV(@PathVariable String user) {
    try {
        ListaEnlazada<Cancion> favoritos = usuarioService.obtenerFavoritos(user);

        if (favoritos.estaVacia()) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "El usuario no tiene canciones favoritas"
            ));
        }

     
            byte[] csvBytes = csvService.generarCsvFavoritos(favoritos, user);
            String nombreArchivo = csvService.generarNombreArchivo(user);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                   "attachment; filename=\"" + nombreArchivo + "\"");

            return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);

        } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
        return ResponseEntity.status(500)
                .body(Map.of("message", "Error al generar el archivo CSV"));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        MapSimple<String, String> errors = new HashMapSimple<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(Map.of(
            "message", "Error de validación",
            "errors", errors
        ));
    }
}

