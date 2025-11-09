package com.myapp.model.grafos;


import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.model.Cancion;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;

/**
 * Grafo de similitud entre canciones.
 * Cada nodo es una canción y las aristas representan la similitud entre ellas.
 * Utiliza estructuras de datos propias: MapSimple, ListaEnlazada, SetPropio.
 */

public class GrafoDeSimilitud {

    private final MapSimple<Long, Cancion> canciones;
    private final MapSimple<Long, MapSimple<Long, Double>> similitudes;

    // Umbral mínimo para considerar una relación significativa
    private static final double UMBRAL_SIMILITUD = 0.5;

    public GrafoDeSimilitud() {
        this.canciones = new HashMapSimple<>();
        this.similitudes = new HashMapSimple<>();
    }

    /** Agrega una canción como nodo en el grafo. */
    public void agregarCancion(Cancion cancion) {
        if (cancion == null) throw new IllegalArgumentException("La canción no puede ser null");
        if (!canciones.containsKey(cancion.getId())) {
            canciones.put(cancion.getId(), cancion);
            similitudes.put(cancion.getId(), new HashMapSimple<>());
        }
    }

    /** Elimina una canción y sus relaciones de similitud. */
    public void eliminarCancion(Long id) {
        if (id == null) return;

        // Eliminar referencias a esta canción en otros nodos
        for (Long key : similitudes.keys()) {
            MapSimple<Long, Double> inner = similitudes.get(key);
            if (inner != null) inner.remove(id);
        }

        similitudes.remove(id);
        canciones.remove(id);
    }

    /** Normaliza el valor de similitud entre [0,1]. */
    private double normalizarSimilitud(double s) {
        if (Double.isNaN(s) || Double.isInfinite(s)) {
            throw new IllegalArgumentException("La similitud no puede ser NaN ni infinita");
        }
        return Math.min(Math.max(s, 0.0), 1.0);
    }

    /** Establece o actualiza la similitud entre dos canciones (grafo no dirigido). */
    public void actualizarSimilitud(Long id1, Long id2, double similitud) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("Los IDs no pueden ser null");
        }
        if (!canciones.containsKey(id1) || !canciones.containsKey(id2)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }

        double s = normalizarSimilitud(similitud);

        MapSimple<Long, Double> m1 = similitudes.get(id1);
        MapSimple<Long, Double> m2 = similitudes.get(id2);

        m1.put(id2, s);
        m2.put(id1, s);
    }

    /**
     * Retorna las canciones vecinas de un nodo cuya similitud supera el umbral.
     * Devuelve un SetPropio<Cancion>, no un HashSet.
     */
    public SetPropio<Cancion> obtenerVecinos(Long id) {
        if (!canciones.containsKey(id)) {
            throw new IllegalArgumentException("La canción no existe en el grafo");
        }

        SetPropio<Cancion> vecinos = new SetPropio<>();
        MapSimple<Long, Double> similitudesCancion = similitudes.get(id);
        if (similitudesCancion == null) return vecinos;

        for (Long key : similitudesCancion.keys()) {
            Double val = similitudesCancion.get(key);
            if (val != null && val >= UMBRAL_SIMILITUD) {
                vecinos.agregar(canciones.get(key));
            }
        }
        return vecinos;
    }

    /**
     * Algoritmo de Dijkstra adaptado: encuentra el camino de mayor similitud (menor costo -log(similitud)).
     * Usa estructuras propias (MapSimple, ListaEnlazada, SetPropio).
     */
    public ListaEnlazada<Cancion> encontrarCaminoMasCorto(Long idOrigen, Long idDestino) {
        if (!canciones.containsKey(idOrigen) || !canciones.containsKey(idDestino)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }

        MapSimple<Long, Double> distancias = new HashMapSimple<>();
        MapSimple<Long, Long> previos = new HashMapSimple<>();
        SetPropio<Long> visitados = new SetPropio<>();

        // Inicializar todas las distancias en infinito
        for (Long id : canciones.keys()) {
            distancias.put(id, Double.POSITIVE_INFINITY);
            previos.put(id, null);
        }
        distancias.put(idOrigen, 0.0);

        // Lista auxiliar para simular una cola de prioridad (simple O(n²))
        ListaEnlazada<Long> pendientes = new ListaEnlazada<>();
        pendientes.agregar(idOrigen);

        while (!pendientes.estaVacia()) {
            // Seleccionar el nodo con menor distancia actual
            Long actual = obtenerMinimoNoVisitado(distancias, visitados);
            if (actual == null) break;
            visitados.agregar(actual);

            if (actual.equals(idDestino)) break;

            MapSimple<Long, Double> vecinos = similitudes.get(actual);
            if (vecinos == null) continue;

            for (Long idVecino : vecinos.keys()) {
                if (visitados.contiene(idVecino)) continue;

                double similitud = vecinos.get(idVecino);
                if (similitud > 0.0) { // tolerancia mínima
                    double costo = -Math.log(Math.max(similitud, 1e-10));
                    double nuevaDistancia = distancias.get(actual) + costo;

                    if (nuevaDistancia < distancias.get(idVecino)) {
                        distancias.put(idVecino, nuevaDistancia);
                        previos.put(idVecino, actual);
                    }
                    pendientes.agregar(idVecino);
                }
            }
        }

        // Reconstruir el camino encontrado
        ListaEnlazada<Cancion> camino = new ListaEnlazada<>();
        Long actual = idDestino;
        while (actual != null) {
            camino.agregar(canciones.get(actual));
            actual = previos.get(actual);
        }

        // Invertir lista para mostrar desde origen a destino
        return invertir(camino);
    }

    /** Encuentra el nodo no visitado con menor distancia. */
    private Long obtenerMinimoNoVisitado(MapSimple<Long, Double> distancias, SetPropio<Long> visitados) {
        Double minDist = Double.POSITIVE_INFINITY;
        Long minNodo = null;
        for (Long id : distancias.keys()) {
            if (!visitados.contiene(id)) {
                Double dist = distancias.get(id);
                if (dist < minDist) {
                    minDist = dist;
                    minNodo = id;
                }
            }
        }
        return minNodo;
    }

    /** Invierte una ListaEnlazada (útil para devolver el camino en orden correcto). */
    private ListaEnlazada<Cancion> invertir(ListaEnlazada<Cancion> original) {
        ListaEnlazada<Cancion> invertida = new ListaEnlazada<>();
        for (int i = original.tamaño() - 1; i >= 0; i--) {
            invertida.agregar(original.obtener(i));
        }
        return invertida;
    }

    /** Devuelve la similitud entre dos canciones (0 si no hay relación). */
    public double getSimilitud(Long id1, Long id2) {
        if (!canciones.containsKey(id1) || !canciones.containsKey(id2)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }
        MapSimple<Long, Double> inner = similitudes.get(id1);
        if (inner == null) return 0.0;
        Double v = inner.get(id2);
        return v == null ? 0.0 : v;
    }

    /** Retorna la cantidad de nodos (canciones) en el grafo. */
    public int getNumeroDeNodos() {
        return canciones.size();
    }
}
