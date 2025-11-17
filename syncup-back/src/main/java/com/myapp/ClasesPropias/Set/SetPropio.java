package com.myapp.ClasesPropias.Set;

import com.myapp.ClasesPropias.Iterador.IteradorPropio;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;


public class SetPropio<T> {

    private final ListaEnlazada<T> elementos;

    public SetPropio() {
        this.elementos = new ListaEnlazada<>();
    }

    public boolean agregar(T elemento) {
        if (contiene(elemento)) return false;
        elementos.agregar(elemento);
        return true;
    }

    public boolean eliminar(T elemento) {
        return elementos.eliminar(elemento);
    }

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

    public int tamaño() {
        return elementos.tamaño();
    }

    public boolean estaVacio() {
        return elementos.estaVacia();
    }

    public void limpiar() {
        elementos.limpiar();
    }

    public IteradorPropio<T> iterador() {
        return elementos.iterador();
    }

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

