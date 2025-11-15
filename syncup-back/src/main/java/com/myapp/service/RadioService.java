package com.myapp.service;
import org.springframework.stereotype.Service;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.ClasesPropias.Iterador.IteradorPropio;
import com.myapp.model.Cancion;

/**
 * RF-006: Generar una "Radio" a partir de una canción.
 * 
 * Usa el GrafoDeSimilitud para recorrer canciones similares (BFS),
 * construyendo una cola de reproducción.
 */
@Service
public class RadioService {

    private final GrafoSimilitudService grafoService;

    public RadioService(GrafoSimilitudService grafoService) {
        this.grafoService = grafoService;
    }

    /**
     * Genera una cola de reproducción tipo "Radio" a partir de una canción inicial.
     *
     * @param idInicial   ID de la canción desde donde comienza la radio.
     * @param maxResultados Cantidad máxima de canciones en la cola.
     * @return ListaEnlazada<Cancion> con la secuencia de reproducción.
     */
    public ListaEnlazada<Cancion> generarRadio(Long idInicial, int maxResultados) {
        ListaEnlazada<Cancion> cola = new ListaEnlazada<>();

        if (idInicial == null || maxResultados <= 0) {
            return cola;
        }

        if (!grafoService.existeCancion(idInicial)) {
            throw new IllegalArgumentException("La canción inicial no existe en el grafo");
        }

        // Conjunto de IDs ya visitados (para no repetir canciones)
        SetPropio<Long> visitados = new SetPropio<>();
        // Cola de IDs por procesar (BFS)
        ListaEnlazada<Long> pendientes = new ListaEnlazada<>();

        visitados.agregar(idInicial);
        pendientes.agregar(idInicial);

        while (!pendientes.estaVacia() && cola.tamaño() < maxResultados) {
            Long actualId = pendientes.primero();
            pendientes.eliminar(actualId);

            Cancion actual = grafoService.obtenerPorId(actualId);
            if (actual != null) {
                cola.agregar(actual);
            }

            // Obtener vecinos (canciones similares) usando el grafo de similitud
            SetPropio<Cancion> vecinos = grafoService.vecinos(actualId);
            if (vecinos == null) continue;

            IteradorPropio<Cancion> it = vecinos.iterador();
            while (it.tieneSiguiente() && cola.tamaño() < maxResultados) {
                Cancion vecino = it.siguiente();
                if (vecino == null || vecino.getId() == null) continue;

                Long vecinoId = vecino.getId();
                if (!visitados.contiene(vecinoId)) {
                    visitados.agregar(vecinoId);
                    pendientes.agregar(vecinoId);
                }
            }
        }

        return cola;
    }
}
