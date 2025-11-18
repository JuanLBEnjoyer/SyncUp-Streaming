package com.myapp.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myapp.service.UsuarioService;
import com.myapp.util.ConversorDto;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.dto.MetricasDto;
import com.myapp.dto.UsuarioDto;
import com.myapp.model.Usuario;
import com.myapp.service.CancionService;
import com.myapp.service.CargaMasivaService;
import com.myapp.service.MetricasService;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CancionService cancionService;
    private final CargaMasivaService cargaMasivaService;
    private final MetricasService metricasService;

    public AdminController(UsuarioService usuarioService, CancionService cancionService, CargaMasivaService cargaMasivaService, MetricasService metricasService) {
        this.usuarioService = usuarioService;
        this.cancionService = cancionService;
        this.cargaMasivaService = cargaMasivaService;
        this.metricasService = metricasService;
    }  
    
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        try {
            ListaEnlazada<Usuario> usuarios = usuarioService.listarTodos();
            UsuarioDto[] respuesta = ConversorDto.listaUsuariosAArray(usuarios);

            return ResponseEntity.ok(Map.of(
                "usuarios", respuesta,
                "total", respuesta.length
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Error al listar usuarios: " + e.getMessage()));
        }
    }

    @DeleteMapping("/usuarios/{user}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String user) {
        try {
            usuarioService.eliminarUsuario(user);

            return ResponseEntity.ok(Map.of(
                "message", "Usuario eliminado correctamente",
                "usuario", user
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/canciones/cargar")
    public ResponseEntity<?> cargarCancionesMasivamente(
            @RequestParam("file") MultipartFile file) {
        
        try {
            CargaMasivaService.ResultadoCarga resultado = 
                cargaMasivaService.procesarArchivo(file);

            if (!resultado.tieneErrores()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Proceso de carga completado exitosamente",
                    "totalProcesadas", resultado.getTotalExitosas(),
                    "exitosas", resultado.getTotalExitosas(),
                    "errores", 0
                ));
            }

            var errores = resultado.getErrores();
            Map<String, Object>[] arrayErrores = new Map[errores.tamaño()];
            
            for (int i = 0; i < errores.tamaño(); i++) {
                var error = errores.obtener(i);
                
                arrayErrores[i] = Map.of(
                    "linea", error.getNumeroLinea(),
                    "contenido", error.getContenido(),
                    "error", error.getMensaje()
                );
            }

            if (resultado.getTotalExitosas() > 0) {
                return ResponseEntity.ok(Map.of(
                    "message", "Proceso completado con algunos errores",
                    "totalProcesadas", resultado.getTotalExitosas() + resultado.getTotalErrores(),
                    "exitosas", resultado.getTotalExitosas(),
                    "errores", resultado.getTotalErrores(),
                    "detalleErrores", arrayErrores
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Todas las líneas fallaron",
                    "totalProcesadas", resultado.getTotalErrores(),
                    "exitosas", 0,
                    "errores", resultado.getTotalErrores(),
                    "detalleErrores", arrayErrores
                ));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "message", "Error al procesar el archivo",
                    "detalle", e.getMessage()
                ));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> obtenerInfo() {
        try {
            int totalUsuarios = usuarioService.contarUsuarios();
            int totalCanciones = cancionService.listarTodas().tamaño();

            return ResponseEntity.ok(Map.of(
                "totalUsuarios", totalUsuarios,
                "totalCanciones", totalCanciones,
                "version", "1.0.0"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Error al obtener información: " + e.getMessage()));
        }
    }

    @GetMapping("/metricas")
public ResponseEntity<?> obtenerMetricas() {
    try {
        MetricasDto metricas = metricasService.calcularMetricas();
        
        // Convertir estructuras propias a formato JSON
        return ResponseEntity.ok(Map.of(
            "totalUsuarios", metricas.getTotalUsuarios(),
            "totalCanciones", metricas.getTotalCanciones(),
            "cancionesPorGenero", convertirMapAJson(metricas.getCancionesPorGenero()),
            "cancionesPorDecada", convertirMapAJson(metricas.getCancionesPorDecada()),
            "topArtistas", convertirTopArtistasAJson(metricas.getTopArtistas()),
            "generosMasFavoritos", convertirMapAJson(metricas.getGenerosMasFavoritos()),
            "duracionPromedio", Math.round(metricas.getDuracionPromedio() * 100.0) / 100.0,
            "añoMasAntiguo", metricas.getAñoMasAntiguo(),
            "añoMasReciente", metricas.getAñoMasReciente()
        ));

    } catch (Exception e) {
        return ResponseEntity.status(500)
            .body(Map.of("message", "Error al calcular métricas: " + e.getMessage()));
    }
}

    private Map<String, Integer> convertirMapAJson(MapSimple<String, Integer> mapa) {
        if (mapa == null) {
            return Map.of();
        }


        int count = 0;
        for (String key : mapa.keys()) {
            if (mapa.get(key) != null) {
            count++;
            }
        }

        String[] keys = new String[count];
        Integer[] values = new Integer[count];
    
        int i = 0;
        for (String key : mapa.keys()) {
            Integer value = mapa.get(key);
            if (value != null) {
                keys[i] = key;
                values[i] = value;
                i++;
        }
    }

        java.util.Map<String, Integer> resultado = new java.util.HashMap<>();
        for (int j = 0; j < count; j++) {
            resultado.put(keys[j], values[j]);
        }

        return resultado;
    }

    private Map<String, Object>[] convertirTopArtistasAJson(ListaEnlazada<MetricasService.TopItem> lista) {
        if (lista == null || lista.estaVacia()) {
            return new Map[0];
        }

        Map<String, Object>[] array = new Map[lista.tamaño()];
    
        for (int i = 0; i < lista.tamaño(); i++) {
            var item = lista.obtener(i);
            array[i] = Map.of(
                "nombre", item.getNombre(),
                "cantidad", item.getValor()
            );
        }

        return array;
    }

    
}