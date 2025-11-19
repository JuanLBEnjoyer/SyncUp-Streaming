package com.myapp.model.grafos;


import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.model.Cancion;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;

public class GrafoDeSimilitud {

    private final MapSimple<Long, Cancion> canciones;
    private final MapSimple<Long, MapSimple<Long, Double>> similitudes;
    private static final double UMBRAL_SIMILITUD = 0.5;

    public GrafoDeSimilitud() {
        this.canciones = new HashMapSimple<>();
        this.similitudes = new HashMapSimple<>();
    }

    public void agregarCancion(Cancion cancion) {
        if (cancion == null || cancion.getId() == null) return;

        Long id = cancion.getId();
        if (!canciones.containsKey(id)) {
            canciones.put(id, cancion);
            similitudes.put(id, new HashMapSimple<>());
        } else {
            canciones.put(id, cancion);
        }
    }

    public void eliminarCancion(Long id) {
        if (id == null) return;

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

    public void actualizarSimilitud(Long id1, Long id2, double similitud) {
        if (id1 == null || id2 == null) return;
        if (!canciones.containsKey(id1) || !canciones.containsKey(id2)) return;

        MapSimple<Long, Double> m1 = similitudes.get(id1);
        MapSimple<Long, Double> m2 = similitudes.get(id2);
        if (m1 == null || m2 == null) return;

        m1.put(id2, similitud);
        m2.put(id1, similitud); 
    }

    public double getSimilitud(Long id1, Long id2) {
        if (id1 == null || id2 == null) return 0.0;
        MapSimple<Long, Double> inner = similitudes.get(id1);
        if (inner == null) return 0.0;
        Double v = inner.get(id2);
        return (v == null) ? 0.0 : v;
    }

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

    public ListaEnlazada<Cancion> encontrarCaminoMasCorto(Long idOrigen, Long idDestino) {
        ListaEnlazada<Cancion> rutaVacia = new ListaEnlazada<>();

        if (idOrigen == null || idDestino == null) return rutaVacia;
        if (!canciones.containsKey(idOrigen) || !canciones.containsKey(idDestino)) {
            return rutaVacia;
        }

        MapSimple<Long, Double> distancias = new HashMapSimple<>();
        MapSimple<Long, Long> previos = new HashMapSimple<>();
        SetPropio<Long> visitados = new SetPropio<>();

        for (Long id : canciones.keys()) {
            distancias.put(id, Double.POSITIVE_INFINITY);
            previos.put(id, null);
        }
        distancias.put(idOrigen, 0.0);

        while (true) {
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

            if (actual == null) break;              
            if (actual.equals(idDestino)) break;      

            visitados.agregar(actual);

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
            return rutaVacia; 
        }

        ListaEnlazada<Cancion> rutaInvertida = new ListaEnlazada<>();
        Long actual = idDestino;
        while (actual != null) {
            rutaInvertida.agregar(canciones.get(actual));
            actual = previos.get(actual);
        }

        ListaEnlazada<Cancion> ruta = new ListaEnlazada<>();
        for (int i = rutaInvertida.tamaño() - 1; i >= 0; i--) {
            ruta.agregar(rutaInvertida.obtener(i));
        }
        return ruta;
    }
}

