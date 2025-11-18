package com.myapp.service;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.model.Cancion;
import com.myapp.model.enums.Genero;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Service
public class CargaMasivaService {

    private final CancionService cancionService;

    public CargaMasivaService(CancionService cancionService) {
        this.cancionService = cancionService;
    }

    public ResultadoCarga procesarArchivo(MultipartFile file) throws IOException {
        
        ResultadoCarga resultado = new ResultadoCarga();
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String nombreArchivo = file.getOriginalFilename();
        if (nombreArchivo == null || 
            (!nombreArchivo.endsWith(".csv") && !nombreArchivo.endsWith(".txt"))) {
            throw new IllegalArgumentException("El archivo debe ser .csv o .txt");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String linea;
            int numeroLinea = 0;
            boolean esPrimeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                
                if (linea.trim().isEmpty()) {
                    continue;
                }

                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    if (linea.toLowerCase().contains("titulo") || 
                        linea.toLowerCase().contains("artista")) {
                        continue;
                    }
                }

                try {
                    Cancion cancion = parsearLinea(linea, numeroLinea);
                    Cancion creada = cancionService.crear(
                        cancion.getTitulo(),
                        cancion.getArtista(),
                        cancion.getGenero(),
                        cancion.getAño(),
                        cancion.getDuracion()
                    );
                    
                    resultado.agregarExito(creada);
                    
                } catch (Exception e) {
                    resultado.agregarError(numeroLinea, linea, e.getMessage());
                }
            }
        }

        return resultado;
    }

    private Cancion parsearLinea(String linea, int numeroLinea) {
        
        String[] partes = linea.split(",");
        if (partes.length != 5) {
            throw new IllegalArgumentException(
                String.format("Formato inválido. Se esperan 5 campos, se encontraron %d", 
                    partes.length)
            );
        }

        try {
            String titulo = partes[0].trim();
            String artista = partes[1].trim();
            String generoStr = partes[2].trim().toUpperCase();
            String añoStr = partes[3].trim();
            String duracionStr = partes[4].trim();
            if (titulo.isEmpty()) {
                throw new IllegalArgumentException("El título no puede estar vacío");
            }
            if (artista.isEmpty()) {
                throw new IllegalArgumentException("El artista no puede estar vacío");
            }
            Genero genero;
            try {
                genero = Genero.valueOf(generoStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    String.format("Género inválido: '%s'. Géneros válidos: ROCK, POP, ELECTRONICA, HIPHOP, SALSA, REGGAETON", 
                        generoStr)
                );
            }
            int año;
            try {
                año = Integer.parseInt(añoStr);
                if (año < 1000 || año > 2100) {
                    throw new IllegalArgumentException(
                        String.format("Año inválido: %d. Debe estar entre 1000 y 2100", año)
                    );
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    String.format("Año inválido: '%s'. Debe ser un número entero", añoStr)
                );
            }

            double duracion;
            try {
                duracion = Double.parseDouble(duracionStr);
                if (duracion <= 0) {
                    throw new IllegalArgumentException("La duración debe ser mayor a cero");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    String.format("Duración inválida: '%s'. Debe ser un número decimal", duracionStr)
                );
            }
            Cancion cancion = new Cancion();
            cancion.setTitulo(titulo);
            cancion.setArtista(artista);
            cancion.setGenero(genero);
            cancion.setAño(año);
            cancion.setDuracion(duracion);

            return cancion;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear la línea: " + e.getMessage());
        }
    }

    public static class ResultadoCarga {
        private final ListaEnlazada<Cancion> cancionesExitosas;
        private final ListaEnlazada<ErrorLinea> errores;

        public ResultadoCarga() {
            this.cancionesExitosas = new ListaEnlazada<>();
            this.errores = new ListaEnlazada<>();
        }

        public void agregarExito(Cancion cancion) {
            cancionesExitosas.agregar(cancion);
        }

        public void agregarError(int numeroLinea, String linea, String mensaje) {
            errores.agregar(new ErrorLinea(numeroLinea, linea, mensaje));
        }

        public ListaEnlazada<Cancion> getCancionesExitosas() {
            return cancionesExitosas;
        }

        public ListaEnlazada<ErrorLinea> getErrores() {
            return errores;
        }

        public int getTotalExitosas() {
            return cancionesExitosas.tamaño();
        }

        public int getTotalErrores() {
            return errores.tamaño();
        }

        public boolean tieneErrores() {
            return !errores.estaVacia();
        }
    }

    public static class ErrorLinea {
        private final int numeroLinea;
        private final String contenido;
        private final String mensaje;

        public ErrorLinea(int numeroLinea, String contenido, String mensaje) {
            this.numeroLinea = numeroLinea;
            this.contenido = contenido;
            this.mensaje = mensaje;
        }

        public int getNumeroLinea() {
            return numeroLinea;
        }

        public String getContenido() {
            return contenido;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}