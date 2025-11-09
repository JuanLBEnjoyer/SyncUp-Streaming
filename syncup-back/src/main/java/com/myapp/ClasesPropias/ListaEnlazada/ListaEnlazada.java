package com.myapp.ClasesPropias.ListaEnlazada;
import java.util.NoSuchElementException;

import com.myapp.ClasesPropias.Iterador.IteradorPropio;

public class ListaEnlazada<T> {

    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int tamaño;

    public ListaEnlazada() {
        this.inicio = null;
        this.fin = null;
        this.tamaño = 0;
    }

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            inicio = fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        tamaño++;
    }

    public boolean eliminar(T dato) {
        if (estaVacia()) return false;

        if ((inicio.getDato() == null && dato == null) ||
            (inicio.getDato() != null && inicio.getDato().equals(dato))) {
            inicio = inicio.getSiguiente();
            if (inicio == null) fin = null; 
            tamaño--;
            return true;
        }

        Nodo<T> actual = inicio;
        Nodo<T> anterior = null;

        while (actual != null) {
            if ((actual.getDato() == null && dato == null) ||
                (actual.getDato() != null && actual.getDato().equals(dato))) {
                anterior.setSiguiente(actual.getSiguiente());
                if (actual == fin) fin = anterior;
                tamaño--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean contiene(T dato) {
        Nodo<T> actual = inicio;
        while (actual != null) {
            if ((actual.getDato() == null && dato == null) ||
                (actual.getDato() != null && actual.getDato().equals(dato))) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) throw new IndexOutOfBoundsException();

        Nodo<T> actual = inicio;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    public int tamaño() {
        return tamaño;
    }

    public void limpiar() {
        inicio = fin = null;
        tamaño = 0;
    }

    public boolean estaVacia() {
        return inicio == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Nodo<T> actual = inicio;
        while (actual != null) {
            sb.append(actual.getDato());
            if (actual.getSiguiente() != null) sb.append(", ");
            actual = actual.getSiguiente();
        }
        sb.append("]");
        return sb.toString();
    }

    public T primero() {
        if (estaVacia()) throw new NoSuchElementException("La lista está vacía");
        return inicio.getDato();
    }

    public T ultimo() {
        if (estaVacia()) throw new NoSuchElementException("La lista está vacía");
        return fin.getDato();
    }

    public IteradorPropio<T> iterador() {
        return new IteradorLista<T>(inicio);
    }
}
