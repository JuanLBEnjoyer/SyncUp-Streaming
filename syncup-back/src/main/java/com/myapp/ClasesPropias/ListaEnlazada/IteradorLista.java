package com.myapp.ClasesPropias.ListaEnlazada;

import com.myapp.ClasesPropias.Iterador.IteradorPropio;

import java.util.NoSuchElementException;


public class IteradorLista<T> implements IteradorPropio<T> {

    private Nodo<T> actual;

    public IteradorLista(Nodo<T> inicio) {
        this.actual = inicio;
    }

    @Override
    public boolean tieneSiguiente() {
        return actual != null;
    }

    @Override
    public T siguiente() {
        if (!tieneSiguiente()) {
            throw new NoSuchElementException("No hay más elementos en la lista");
        }
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        return dato;
    }
}

