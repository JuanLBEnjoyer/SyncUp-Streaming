package com.myapp.ClasesPropias.Iterador;

/**
 * Interfaz genérica para iteradores propios (sin depender de java.util.Iterator).
 */
public interface IteradorPropio<T> {
    boolean tieneSiguiente();
    T siguiente();
}

