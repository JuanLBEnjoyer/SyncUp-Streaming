package com.myapp.ClasesPropias.Set;

import com.myapp.ClasesPropias.Iterador.IteradorPropio;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;

/**
 * Implementación de un conjunto (Set) propio.
 * Basado en la ListaEnlazada e IteradorPropio del estudiante.
 * No permite elementos duplicados según equals().
 */
public class SetPropio<T> {

    private final ListaEnlazada<T> elementos;

    public SetPropio() {
        this.elementos = new ListaEnlazada<>();
    }

    /** 
     * Agrega un elemento al conjunto si no existe.
     * Retorna true si fue agregado, false si ya existía.
     */
    public boolean agregar(T elemento) {
        if (contiene(elemento)) return false;
        elementos.agregar(elemento);
        return true;
    }

    /**
     * Elimina un elemento del conjunto.
     * Retorna true si se eliminó correctamente.
     */
    public boolean eliminar(T elemento) {
        return elementos.eliminar(elemento);
    }

    /**
     * Verifica si el conjunto contiene el elemento dado.
     */
    public boolean contiene(T elemento) {
        IteradorPropio<T> it = elementos.iterador();
        while (it.tieneSiguiente()) {
            T actual = it.siguiente();
            if ((actual == null && elemento == null) ||
                (actual != null && actual.equals(elemento))) {
                return true;
            }
        }
        return false;
    }

    /** Retorna el número de elementos del conjunto. */
    public int tamaño() {
        return elementos.tamaño();
    }

    /** Indica si el conjunto está vacío. */
    public boolean estaVacio() {
        return elementos.estaVacia();
    }

    /** Elimina todos los elementos del conjunto. */
    public void limpiar() {
        elementos.limpiar();
    }

    /** Devuelve un iterador propio para recorrer el conjunto. */
    public IteradorPropio<T> iterador() {
        return elementos.iterador();
    }

    /**
     * Devuelve un nuevo SetPropio que representa la unión de este conjunto con otro.
     */
    public SetPropio<T> union(SetPropio<T> otro) {
        SetPropio<T> resultado = new SetPropio<>();
        IteradorPropio<T> it = this.iterador();
        while (it.tieneSiguiente()) {
            resultado.agregar(it.siguiente());
        }
        IteradorPropio<T> it2 = otro.iterador();
        while (it2.tieneSiguiente()) {
            resultado.agregar(it2.siguiente());
        }
        return resultado;
    }

    /**
     * Devuelve un nuevo SetPropio que representa la intersección con otro conjunto.
     */
    public SetPropio<T> interseccion(SetPropio<T> otro) {
        SetPropio<T> resultado = new SetPropio<>();
        IteradorPropio<T> it = this.iterador();
        while (it.tieneSiguiente()) {
            T valor = it.siguiente();
            if (otro.contiene(valor)) {
                resultado.agregar(valor);
            }
        }
        return resultado;
    }

    /**
     * Devuelve un nuevo SetPropio con los elementos de este conjunto que no están en otro.
     */
    public SetPropio<T> diferencia(SetPropio<T> otro) {
        SetPropio<T> resultado = new SetPropio<>();
        IteradorPropio<T> it = this.iterador();
        while (it.tieneSiguiente()) {
            T valor = it.siguiente();
            if (!otro.contiene(valor)) {
                resultado.agregar(valor);
            }
        }
        return resultado;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        IteradorPropio<T> it = iterador();
        boolean primero = true;
        while (it.tieneSiguiente()) {
            if (!primero) sb.append(", ");
            sb.append(it.siguiente());
            primero = false;
        }
        sb.append("}");
        return sb.toString();
    }
}

