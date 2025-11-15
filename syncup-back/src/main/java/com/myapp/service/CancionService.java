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

    public CancionService(GrafoSimilitudService grafoSimilitud, TrieAutocompletadoService trieAutocompletado, BusquedaConcurrenteService busquedaConcurrente) {
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
        return c;
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
        grafoSimilitud.registrarCancion(c); // actúa como reemplazo de metadata
        trieAutocompletado.registrarTitulo(c.getTitulo());
        return c;
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

