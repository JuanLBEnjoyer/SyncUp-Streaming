package com.myapp.service;

import org.springframework.stereotype.Service;
import com.myapp.model.grafos.GrafoDeSimilitud;
import com.myapp.model.Cancion;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;


@Service
public class GrafoSimilitudService {

    private final GrafoDeSimilitud grafo = new GrafoDeSimilitud();


    public void registrarCancion(Cancion c) {
        if (c == null || c.getId() == null) {
            throw new IllegalArgumentException("Canción o ID inválidos");
        }
        grafo.agregarCancion(c);
    }

    public void eliminarCancion(Long id) {
        if (id == null || !grafo.contieneCancion(id)) {
            throw new IllegalArgumentException("La canción no existe en el grafo");
        }
        grafo.eliminarCancion(id);
    }

    public Cancion obtenerPorId(Long id) {
        return grafo.obtenerCancion(id);
    }

    public ListaEnlazada<Cancion> obtenerTodas() {
        return grafo.obtenerTodasCanciones();
    }

    public boolean existeCancion(Long id) {
        return grafo.contieneCancion(id);
    }

    public int numeroDeNodos() {
        return grafo.getNumeroDeNodos();
    }

    public void establecerSimilitud(Long id1, Long id2, double similitud) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("IDs requeridos");
        }
        if (id1.equals(id2)) {
            throw new IllegalArgumentException("No se puede relacionar una canción consigo misma");
        }
        if (!grafo.contieneCancion(id1) || !grafo.contieneCancion(id2)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }
        if (similitud < 0.0 || similitud > 1.0) {
            throw new IllegalArgumentException("La similitud debe estar entre 0 y 1");
        }
        grafo.actualizarSimilitud(id1, id2, similitud);
    }

    public double obtenerSimilitud(Long id1, Long id2) {
        return grafo.getSimilitud(id1, id2);
    }

    public SetPropio<Cancion> vecinos(Long id) {
        if (!grafo.contieneCancion(id)) {
            throw new IllegalArgumentException("La canción no existe en el grafo");
        }
        return grafo.obtenerVecinos(id);
    }

    public ListaEnlazada<Cancion> caminoMasSimilar(Long origen, Long destino) {
        return grafo.encontrarCaminoMasCorto(origen, destino);
    }
}


