package com.myapp.model.grafos;


import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.model.Cancion;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;


/**
 * Grafo de similitud:
 * - Ponderado
 * - No dirigido
 * - Usa solo estructuras propias (MapSimple, ListaEnlazada, SetPropio)
 * 
 * Requisitos que cumple:
 *  RF-019: Cancion funciona como nodo del grafo.
 *  RF-021: Grafo Ponderado No Dirigido.
 *  RF-022: Soporta Dijkstra para rutas de menor costo (mayor similitud).
 */
public class GrafoDeSimilitud {

    private final MapSimple<Long, Cancion> canciones;
    private final MapSimple<Long, MapSimple<Long, Double>> similitudes;
    private static final double UMBRAL_SIMILITUD = 0.5;

    public GrafoDeSimilitud() {
        this.canciones = new HashMapSimple<>();
        this.similitudes = new HashMapSimple<>();
    }

    // ---------- NODOS (CANCIONES) ----------

    /** Agrega una canción como nodo del grafo (si no existe) o actualiza su metadata. */
    public void agregarCancion(Cancion cancion) {
        if (cancion == null || cancion.getId() == null) return;

        Long id = cancion.getId();
        if (!canciones.containsKey(id)) {
            canciones.put(id, cancion);
            similitudes.put(id, new HashMapSimple<>());
        } else {
            // Actualiza datos (titulo, artista, etc.) sin tocar las aristas
            canciones.put(id, cancion);
        }
    }

    /** Elimina la canción y todas sus relaciones de similitud. */
    public void eliminarCancion(Long id) {
        if (id == null) return;

        // Eliminar referencias en otros nodos
        for (Long key : similitudes.keys()) {
            MapSimple<Long, Double> inner = similitudes.get(key);
            if (inner != null) {
                inner.remove(id);
            }
        }
        similitudes.remove(id);
        canciones.remove(id);
    }

    public boolean contieneCancion(Long id) {
        return id != null && canciones.containsKey(id);
    }

    public Cancion obtenerCancion(Long id) {
        if (id == null) return null;
        return canciones.get(id);
    }

    /** Lista todas las canciones del grafo en una ListaEnlazada propia. */
    public ListaEnlazada<Cancion> obtenerTodasCanciones() {
        ListaEnlazada<Cancion> lista = new ListaEnlazada<>();
        for (Long id : canciones.keys()) {
            lista.agregar(canciones.get(id));
        }
        return lista;
    }

    public int getNumeroDeNodos() {
        return canciones.size();
    }

    // ---------- ARISTAS (SIMILITUD) ----------

    /**
     * Crea/actualiza la arista no dirigida entre dos canciones con el peso "similitud".
     * Se asume validación de rango [0,1] y existencia de nodos desde el service.
     */
    public void actualizarSimilitud(Long id1, Long id2, double similitud) {
        if (id1 == null || id2 == null) return;
        if (!canciones.containsKey(id1) || !canciones.containsKey(id2)) return;

        MapSimple<Long, Double> m1 = similitudes.get(id1);
        MapSimple<Long, Double> m2 = similitudes.get(id2);
        if (m1 == null || m2 == null) return;

        m1.put(id2, similitud);
        m2.put(id1, similitud); // no dirigido
    }

    /** Retorna la similitud directa entre dos canciones, 0.0 si no hay arista. */
    public double getSimilitud(Long id1, Long id2) {
        if (id1 == null || id2 == null) return 0.0;
        MapSimple<Long, Double> inner = similitudes.get(id1);
        if (inner == null) return 0.0;
        Double v = inner.get(id2);
        return (v == null) ? 0.0 : v;
    }

    /** Vecinos con similitud >= UMBRAL_SIMILITUD. */
    public SetPropio<Cancion> obtenerVecinos(Long id) {
        SetPropio<Cancion> vecinos = new SetPropio<>();
        if (id == null || !canciones.containsKey(id)) return vecinos;

        MapSimple<Long, Double> sim = similitudes.get(id);
        if (sim == null) return vecinos;

        for (Long key : sim.keys()) {
            Double val = sim.get(key);
            if (val != null && val >= UMBRAL_SIMILITUD) {
                vecinos.agregar(canciones.get(key));
            }
        }
        return vecinos;
    }

    // ---------- DIJKSTRA (RF-022) SIN PriorityQueue NI java.util LIST ----------

    /**
     * Implementación de Dijkstra SIN PriorityQueue (selección lineal).
     * Minimiza la suma de -log(similitud) para maximizar el producto de similitudes.
     *
     * Retorna la ruta desde origen hasta destino como ListaEnlazada<Cancion>.
     * Si no hay camino, retorna una lista vacía.
     */
    public ListaEnlazada<Cancion> encontrarCaminoMasCorto(Long idOrigen, Long idDestino) {
        ListaEnlazada<Cancion> rutaVacia = new ListaEnlazada<>();

        if (idOrigen == null || idDestino == null) return rutaVacia;
        if (!canciones.containsKey(idOrigen) || !canciones.containsKey(idDestino)) {
            return rutaVacia;
        }

        MapSimple<Long, Double> distancias = new HashMapSimple<>();
        MapSimple<Long, Long> previos = new HashMapSimple<>();
        SetPropio<Long> visitados = new SetPropio<>();

        // Inicializar distancias
        for (Long id : canciones.keys()) {
            distancias.put(id, Double.POSITIVE_INFINITY);
            previos.put(id, null);
        }
        distancias.put(idOrigen, 0.0);

        while (true) {
            // 1. Tomar el no visitado con menor distancia
            Long actual = null;
            double mejor = Double.POSITIVE_INFINITY;

            for (Long id : canciones.keys()) {
                if (!visitados.contiene(id)) {
                    Double d = distancias.get(id);
                    if (d != null && d < mejor) {
                        mejor = d;
                        actual = id;
                    }
                }
            }

            if (actual == null) break;                // no quedan alcanzables
            if (actual.equals(idDestino)) break;      // llegamos al destino

            visitados.agregar(actual);

            // 2. Relajar aristas de "actual"
            MapSimple<Long, Double> vecinos = similitudes.get(actual);
            if (vecinos == null) continue;

            for (Long idVecino : vecinos.keys()) {
                if (visitados.contiene(idVecino)) continue;

                double similitud = vecinos.get(idVecino);
                if (similitud < UMBRAL_SIMILITUD || similitud <= 0.0) continue;

                double costoArista = -Math.log(Math.max(similitud, 1e-10));
                Double distActual = distancias.get(actual);
                if (distActual == null) continue;

                double nuevaDistancia = distActual + costoArista;
                Double distVecino = distancias.get(idVecino);

                if (distVecino == null || nuevaDistancia < distVecino) {
                    distancias.put(idVecino, nuevaDistancia);
                    previos.put(idVecino, actual);
                }
            }
        }

        Double distDestino = distancias.get(idDestino);
        if (distDestino == null || distDestino.equals(Double.POSITIVE_INFINITY)) {
            return rutaVacia; // sin camino
        }

        // 3. Reconstruir ruta destino -> origen
        ListaEnlazada<Cancion> rutaInvertida = new ListaEnlazada<>();
        Long actual = idDestino;
        while (actual != null) {
            rutaInvertida.agregar(canciones.get(actual));
            actual = previos.get(actual);
        }

        // 4. Invertir para dejarla origen -> destino
        ListaEnlazada<Cancion> ruta = new ListaEnlazada<>();
        for (int i = rutaInvertida.tamaño() - 1; i >= 0; i--) {
            ruta.agregar(rutaInvertida.obtener(i));
        }
        return ruta;
    }
}

