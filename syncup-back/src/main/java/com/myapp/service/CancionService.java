package com.myapp.service;

import org.springframework.stereotype.Service;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.model.Cancion;
import com.myapp.model.enums.Genero;


@Service
public class CancionService {

    private final GrafoSimilitudService grafoSimilitud;
    private final TrieAutocompletadoService trieAutocompletado;
    private final BusquedaConcurrenteService busquedaConcurrente;
    private long secuencia = 1L;

    public CancionService(GrafoSimilitudService grafoSimilitud, 
                        TrieAutocompletadoService trieAutocompletado, 
                        BusquedaConcurrenteService busquedaConcurrente) {
        this.grafoSimilitud = grafoSimilitud;
        this.trieAutocompletado = trieAutocompletado;
        this.busquedaConcurrente = busquedaConcurrente;
    }

    public Cancion crear(String titulo, String artista, Genero genero, int anio, double duracion) {
        Cancion c = new Cancion();
        c.setId(secuencia++);
        c.setTitulo(titulo);
        c.setArtista(artista);
        c.setGenero(genero);
        c.setAño(anio);
        c.setDuracion(duracion);

        grafoSimilitud.registrarCancion(c);
        
        trieAutocompletado.registrarTitulo(titulo);
        
        calcularSimilitudesAutomaticas(c);
        
        return c;
    }

    private void calcularSimilitudesAutomaticas(Cancion nueva) {
        ListaEnlazada<Cancion> todas = grafoSimilitud.obtenerTodas();
        
        for (int i = 0; i < todas.tamaño(); i++) {
            Cancion existente = todas.obtener(i);
            
            if (existente.getId().equals(nueva.getId())) {
                continue;
            }
            
            double similitud = calcularSimilitud(nueva, existente);
            
            if (similitud >= 0.5) {
                grafoSimilitud.establecerSimilitud(nueva.getId(), existente.getId(), similitud);
            }
        }
    }

    private double calcularSimilitud(Cancion c1, Cancion c2) {
        double similitud = 0.0;
        
        if (c1.getGenero() == c2.getGenero()) {
            similitud += 0.4;
        }

        if (c1.getArtista() != null && c2.getArtista() != null) {
            if (c1.getArtista().equalsIgnoreCase(c2.getArtista())) {
                similitud += 0.3;
            }
        }
        
        int diferenciaAños = Math.abs(c1.getAño() - c2.getAño());
        if (diferenciaAños <= 5) {
            similitud += 0.2;
        }
        
        double diferenciaDuracion = Math.abs(c1.getDuracion() - c2.getDuracion());
        if (diferenciaDuracion <= 30) {
            similitud += 0.1;
        }
        return Math.min(similitud, 1.0);
    }

    public Cancion obtener(Long id) {
        return grafoSimilitud.obtenerPorId(id);
    }

    public ListaEnlazada<Cancion> listarTodas() {
        return grafoSimilitud.obtenerTodas();
    }

    public Cancion actualizar(Cancion c) {
        if (c == null || c.getId() == null) {
            throw new IllegalArgumentException("La canción debe tener ID para actualizar");
        }
        if (!grafoSimilitud.existeCancion(c.getId())) {
            throw new IllegalArgumentException("La canción no existe");
        }
        
        grafoSimilitud.registrarCancion(c);

        trieAutocompletado.registrarTitulo(c.getTitulo());
        
        recalcularSimilitudes(c);
        
        return c;
    }

    private void recalcularSimilitudes(Cancion actualizada) {
        ListaEnlazada<Cancion> todas = grafoSimilitud.obtenerTodas();
        
        for (int i = 0; i < todas.tamaño(); i++) {
            Cancion otra = todas.obtener(i);
            
            if (otra.getId().equals(actualizada.getId())) {
                continue;
            }
            
            double similitud = calcularSimilitud(actualizada, otra);
            
            if (similitud >= 0.5) {
                grafoSimilitud.establecerSimilitud(actualizada.getId(), otra.getId(), similitud);
            }
        }
    }

    public void eliminar(Long id) {
        grafoSimilitud.eliminarCancion(id);
    }

    public boolean existe(Long id) {
        return grafoSimilitud.existeCancion(id);
    }

    public void relacionar(Long id1, Long id2, double similitud) {
        grafoSimilitud.establecerSimilitud(id1, id2, similitud);
    }

    public double similitud(Long id1, Long id2) {
        return grafoSimilitud.obtenerSimilitud(id1, id2);
    }

    public SetPropio<Cancion> similares(Long id) {
        return grafoSimilitud.vecinos(id);
    }

    public ListaEnlazada<Cancion> caminoMasSimilar(Long origen, Long destino) {
        return grafoSimilitud.caminoMasSimilar(origen, destino);
    }

    public ListaEnlazada<String> sugerirTitulos(String prefijo, int limite) {
        return trieAutocompletado.sugerir(prefijo, limite);
    }

    public ListaEnlazada<String> sugerirTitulos(String prefijo) {
        return trieAutocompletado.sugerir(prefijo);
    }

    public ListaEnlazada<Cancion> buscar(String artista,
                                        Genero genero,
                                        Integer anio,
                                        boolean usarAnd) {
        
        ListaEnlazada<Cancion> todas = listarTodas();

        String filtroArtista = (artista == null) ? null : artista.trim().toLowerCase();
        boolean hayFiltroArtista = (filtroArtista != null && !filtroArtista.isBlank());
        boolean hayFiltroGenero = (genero != null);
        boolean hayFiltroAnio = (anio != null);

        if (!hayFiltroArtista && !hayFiltroGenero && !hayFiltroAnio) {
            return todas;
        }

        return busquedaConcurrente.buscarConHilos(
            todas,      
            artista,   
            genero,     
            anio,       
            usarAnd,    
            4           
        );
    }
}

