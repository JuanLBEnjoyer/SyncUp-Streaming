package com.myapp.model;

import java.util.*;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.Map.HashMapSimple;

public class GrafoDeSimilitud {
    private final MapSimple<Long, Cancion> canciones;
    private final MapSimple<Long, MapSimple<Long, Double>> similitudes;
    private static final double UMBRAL_SIMILITUD = 0.5;

    public GrafoDeSimilitud() {
        this.canciones = new HashMapSimple<>();
        this.similitudes = new HashMapSimple<>();
    }

    public void agregarCancion(Cancion cancion) {
        if (!canciones.containsKey(cancion.getId())) {
            canciones.put(cancion.getId(), cancion);
            similitudes.put(cancion.getId(), new HashMapSimple<>());
        }
    }

    public void eliminarCancion(Long id) {
        // Eliminar referencias a esta canción en otras canciones
        for (Long key : similitudes.keys()) {
            MapSimple<Long, Double> inner = similitudes.get(key);
            if (inner != null) inner.remove(id);
        }
        // Eliminar la canción y su entrada en la tabla de similitudes
        similitudes.remove(id);
        canciones.remove(id);
    }

    public void actualizarSimilitud(Long id1, Long id2, double similitud) {
        if (!canciones.containsKey(id1) || !canciones.containsKey(id2)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }
        if (similitud < 0 || similitud > 1) {
            throw new IllegalArgumentException("La similitud debe estar entre 0 y 1");
        }
        MapSimple<Long, Double> m1 = similitudes.get(id1);
        MapSimple<Long, Double> m2 = similitudes.get(id2);
        m1.put(id2, similitud);
        m2.put(id1, similitud); // El grafo es no dirigido
    }

    public Set<Cancion> obtenerVecinos(Long id) {
        if (!canciones.containsKey(id)) {
            throw new IllegalArgumentException("La canción no existe en el grafo");
        }
        Set<Cancion> vecinos = new HashSet<>();
        MapSimple<Long, Double> similitudesCancion = similitudes.get(id);
        for (Long key : similitudesCancion.keys()) {
            Double val = similitudesCancion.get(key);
            if (val != null && val >= UMBRAL_SIMILITUD) {
                vecinos.add(canciones.get(key));
            }
        }
        return vecinos;
    }

    public List<Set<Cancion>> obtenerComponentesConexos() {
        List<Set<Cancion>> componentes = new ArrayList<>();
        Set<Long> visitados = new HashSet<>();

        for (Long id : canciones.keys()) {
            if (!visitados.contains(id)) {
                Set<Cancion> componente = new HashSet<>();
                dfs(id, visitados, componente);
                if (!componente.isEmpty()) {
                    componentes.add(componente);
                }
            }
        }
        return componentes;
    }

    private void dfs(Long idActual, Set<Long> visitados, Set<Cancion> componente) {
        visitados.add(idActual);
        componente.add(canciones.get(idActual));

        MapSimple<Long, Double> inner = similitudes.get(idActual);
        for (Long neighbor : inner.keys()) {
            Double val = inner.get(neighbor);
            if (val != null && val >= UMBRAL_SIMILITUD && !visitados.contains(neighbor)) {
                dfs(neighbor, visitados, componente);
            }
        }
    }

    /**
     * Implementación de Dijkstra para encontrar el camino de mayor similitud entre dos canciones.
     * La similitud total de una ruta es el producto de las similitudes individuales.
     * Usamos -log(similitud) como costo para convertir el producto de similitudes en suma de costos:
     * - Similitud 1.0 → Costo 0.0 (camino óptimo)
     * - Similitud 0.5 → Costo 0.69 (-log(0.5))
     * - Similitud 0.1 → Costo 2.30 (-log(0.1))
     * Al minimizar la suma de -log(similitud), maximizamos el producto de similitudes.
     */
    public List<Cancion> encontrarCaminoMasCorto(Long idOrigen, Long idDestino) {
        if (!canciones.containsKey(idOrigen) || !canciones.containsKey(idDestino)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }

    MapSimple<Long, Double> distancias = new HashMapSimple<>();
    MapSimple<Long, Long> previos = new HashMapSimple<>();
        Set<Long> visitados = new HashSet<>();
        
        // Cola de prioridad con los nodos por visitar, ordenada por distancia
        PriorityQueue<Long> cola = new PriorityQueue<>(
            (a, b) -> Double.compare(distancias.get(a), distancias.get(b))
        );

        // Inicialización: distancia infinita a todos los nodos excepto origen
        for (Long id : canciones.keys()) {
            distancias.put(id, Double.POSITIVE_INFINITY);
            previos.put(id, null);
        }
        distancias.put(idOrigen, 0.0);
        cola.offer(idOrigen);

        // Algoritmo de Dijkstra
        while (!cola.isEmpty()) {
            Long actual = cola.poll();
            
            // Si llegamos al destino, terminamos
            if (actual.equals(idDestino)) break;
            
            // Si ya procesamos este nodo, continuamos
            if (visitados.contains(actual)) continue;
            visitados.add(actual);

            // Procesar cada vecino
            MapSimple<Long, Double> vecinos = similitudes.get(actual);
            for (Long idVecino : vecinos.keys()) {
                if (visitados.contains(idVecino)) continue;
                double similitud = vecinos.get(idVecino);
                if (similitud >= UMBRAL_SIMILITUD) {
                    double costoArista = -Math.log(Math.max(similitud, 1e-10));
                    double nuevaDistancia = distancias.get(actual) + costoArista;

                    // Si encontramos un camino mejor, actualizamos
                    if (nuevaDistancia < distancias.get(idVecino)) {
                        distancias.put(idVecino, nuevaDistancia);
                        previos.put(idVecino, actual);
                        cola.offer(idVecino);
                    }
                }
            }
        }

        // Si no hay camino al destino devolvemos lista vacía
        Double distDestino = distancias.get(idDestino);
        if (distDestino == null || distDestino.equals(Double.POSITIVE_INFINITY)) {
            return Collections.emptyList();
        }

        // Reconstruir el camino y retornarlo
        List<Cancion> camino = new ArrayList<>();
        for (Long actual = idDestino; actual != null; actual = previos.get(actual)) {
            camino.add(0, canciones.get(actual));
        }
        return camino;
    }

    /**
     * Calcula la similitud total de una ruta (producto de similitudes entre aristas
     * consecutivas). Retorna 0.0 si la ruta está vacía. Si la ruta tiene un solo nodo
     * devuelve 1.0.
     */
    public double calcularSimilitudRuta(List<Cancion> ruta) {
        if (ruta == null || ruta.isEmpty()) return 0.0;
        if (ruta.size() == 1) return 1.0;

        double producto = 1.0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            Long id1 = ruta.get(i).getId();
            Long id2 = ruta.get(i + 1).getId();
            double s = getSimilitud(id1, id2);
            producto *= s;
            if (producto == 0.0) return 0.0; // corta si llega a cero
        }
        return producto;
    }

    public Set<Map.Entry<Long, Long>> obtenerArbolRecubrimientoMinimo() {
    Set<Map.Entry<Long, Long>> arbol = new HashSet<>();
        if (canciones.isEmpty()) return arbol;

        Set<Long> visitados = new HashSet<>();
        PriorityQueue<Edge> cola = new PriorityQueue<>();
    Iterator<Long> it = canciones.keys().iterator();
    if (!it.hasNext()) return arbol;
    Long inicio = it.next();
    visitados.add(inicio);

        // Agregar todas las aristas del vértice inicial
        MapSimple<Long, Double> iniciales = similitudes.get(inicio);
        for (Long vecinoKey : iniciales.keys()) {
            double val = iniciales.get(vecinoKey);
            if (val >= UMBRAL_SIMILITUD) {
                cola.offer(new Edge(inicio, vecinoKey, 1 - val));
            }
        }

        while (!cola.isEmpty() && visitados.size() < canciones.size()) {
            Edge edge = cola.poll();
            if (visitados.contains(edge.destino)) continue;

            visitados.add(edge.destino);
            arbol.add(new AbstractMap.SimpleEntry<>(edge.origen, edge.destino));

            // Agregar aristas del nuevo vértice
            MapSimple<Long, Double> vecs = similitudes.get(edge.destino);
            for (Long vecinoKey : vecs.keys()) {
                double val = vecs.get(vecinoKey);
                if (!visitados.contains(vecinoKey) && val >= UMBRAL_SIMILITUD) {
                    cola.offer(new Edge(edge.destino, vecinoKey, 1 - val));
                }
            }
        }

        return arbol;
    }

    private static class Edge implements Comparable<Edge> {
        final Long origen;
        final Long destino;
        final double peso;

        Edge(Long origen, Long destino, double peso) {
            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
        }

        @Override
        public int compareTo(Edge otro) {
            return Double.compare(this.peso, otro.peso);
        }
    }

    // Métodos auxiliares para tests y visualización
    public int getNumeroDeNodos() {
        return canciones.size();
    }

    public Map<Long, Cancion> getCanciones() {
        // Construir un Map normal para compatibilidad con el resto del código
        Map<Long, Cancion> mapa = new HashMap<>();
        for (Long id : canciones.keys()) mapa.put(id, canciones.get(id));
        return Collections.unmodifiableMap(mapa);
    }

    public double getSimilitud(Long id1, Long id2) {
        if (!canciones.containsKey(id1) || !canciones.containsKey(id2)) {
            throw new IllegalArgumentException("Una o ambas canciones no existen en el grafo");
        }
        MapSimple<Long, Double> inner = similitudes.get(id1);
        if (inner == null) return 0.0;
        Double v = inner.get(id2);
        return v == null ? 0.0 : v;
    }
}