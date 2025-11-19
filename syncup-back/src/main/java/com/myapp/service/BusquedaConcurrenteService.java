package com.myapp.service;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.model.Cancion;
import com.myapp.model.enums.Genero;
import org.springframework.stereotype.Service;

@Service
public class BusquedaConcurrenteService {

    public ListaEnlazada<Cancion> buscarConHilos(
            ListaEnlazada<Cancion> todas,
            String artista,
            Genero genero,
            Integer anio,
            boolean usarAnd,
            int numHilos) {

        if (todas.tamaño() == 0) {
            return new ListaEnlazada<>();
        }

        int tamañoTotal = todas.tamaño();
        int tamañoPorHilo = (int) Math.ceil((double) tamañoTotal / numHilos);

        BuscadorThread[] threads = new BuscadorThread[numHilos];

        for (int i = 0; i < numHilos; i++) {
            int inicio = i * tamañoPorHilo;
            int fin = Math.min(inicio + tamañoPorHilo, tamañoTotal);

            threads[i] = new BuscadorThread(
                todas, inicio, fin, artista, genero, anio, usarAnd
            );
            threads[i].start();
        }

        for (BuscadorThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Búsqueda interrumpida", e);
            }
        }

        ListaEnlazada<Cancion> resultadoFinal = new ListaEnlazada<>();
        for (BuscadorThread thread : threads) {
            ListaEnlazada<Cancion> parcial = thread.getResultado();
            for (int i = 0; i < parcial.tamaño(); i++) {
                resultadoFinal.agregar(parcial.obtener(i));
            }
        }

        return resultadoFinal;
    }

    private static class BuscadorThread extends Thread {
        private final ListaEnlazada<Cancion> catalogo;
        private final int inicio;
        private final int fin;
        private final String artista;
        private final Genero genero;
        private final Integer anio;
        private final boolean usarAnd;
        private final ListaEnlazada<Cancion> resultado;

        public BuscadorThread(ListaEnlazada<Cancion> catalogo,
                              int inicio, int fin,
                              String artista, Genero genero, Integer anio,
                              boolean usarAnd) {
            this.catalogo = catalogo;
            this.inicio = inicio;
            this.fin = fin;
            this.artista = artista;
            this.genero = genero;
            this.anio = anio;
            this.usarAnd = usarAnd;
            this.resultado = new ListaEnlazada<>();
        }

        @Override
        public void run() {
            String filtroArtista = (artista == null) ? null 
                : artista.trim().toLowerCase();
            boolean hayFiltroArtista = (filtroArtista != null 
                && !filtroArtista.isBlank());
            boolean hayFiltroGenero = (genero != null);
            boolean hayFiltroAnio = (anio != null);

            for (int i = inicio; i < fin; i++) {
                Cancion c = catalogo.obtener(i);

                boolean coincideArtista = true;
                boolean coincideGenero = true;
                boolean coincideAnio = true;

                if (hayFiltroArtista) {
                    String artistaCancion = (c.getArtista() == null) 
                        ? "" : c.getArtista().toLowerCase();
                    coincideArtista = artistaCancion.contains(filtroArtista);
                }

                if (hayFiltroGenero) {
                    coincideGenero = (c.getGenero() == genero);
                }

                if (hayFiltroAnio) {
                    coincideAnio = (c.getAño() == anio.intValue());
                }

                boolean coincide;
                if (usarAnd) {
                    coincide = true;
                    if (hayFiltroArtista) coincide &= coincideArtista;
                    if (hayFiltroGenero) coincide &= coincideGenero;
                    if (hayFiltroAnio) coincide &= coincideAnio;
                } else {
                    coincide = false;
                    if (hayFiltroArtista) coincide |= coincideArtista;
                    if (hayFiltroGenero) coincide |= coincideGenero;
                    if (hayFiltroAnio) coincide |= coincideAnio;
                }

                if (coincide) {
                    resultado.agregar(c);
                }
            }
        }

        public ListaEnlazada<Cancion> getResultado() {
            return resultado;
        }
    }
}