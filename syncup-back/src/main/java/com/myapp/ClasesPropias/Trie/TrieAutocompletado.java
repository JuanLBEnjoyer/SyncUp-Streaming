package com.myapp.ClasesPropias.Trie;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;

public class TrieAutocompletado {

    private final NodoTrie raiz;

    public TrieAutocompletado() {
        this.raiz = new NodoTrie();
    }

    public void insertar(String palabra) {
        if (palabra == null) return;

        NodoTrie actual = raiz;
        for (int i = 0; i < palabra.length(); i++) {
            char c = palabra.charAt(i);
            MapSimple<Character, NodoTrie> hijos = actual.hijos;
            NodoTrie hijo = hijos.get(c);
            if (hijo == null) {
                hijo = new NodoTrie();
                hijos.put(c, hijo);
            }
            actual = hijo;
        }
        actual.esFinDePalabra = true;
    }

    public boolean contienePalabra(String palabra) {
        if (palabra == null) return false;

        NodoTrie actual = raiz;
        for (int i = 0; i < palabra.length(); i++) {
            char c = palabra.charAt(i);
            NodoTrie hijo = actual.hijos.get(c);
            if (hijo == null) return false;
            actual = hijo;
        }
        return actual.esFinDePalabra;
    }

    public ListaEnlazada<String> autocompletar(String prefijo) {
        return autocompletar(prefijo, 10);
    }

    public ListaEnlazada<String> autocompletar(String prefijo, int limite) {
        ListaEnlazada<String> resultados = new ListaEnlazada<>();
        if (prefijo == null || limite <= 0) return resultados;

        NodoTrie nodoPrefijo = raiz;

        for (int i = 0; i < prefijo.length(); i++) {
            char c = prefijo.charAt(i);
            NodoTrie hijo = nodoPrefijo.hijos.get(c);
            if (hijo == null) {
                return resultados;
            }
            nodoPrefijo = hijo;
        }

        recolectar(nodoPrefijo, prefijo, resultados, limite);
        return resultados;
    }

    private void recolectar(NodoTrie nodo, String prefijoActual,
                            ListaEnlazada<String> resultados, int limite) {

        if (resultados.tamaño() >= limite) return;

        if (nodo.esFinDePalabra) {
            resultados.agregar(prefijoActual);
            if (resultados.tamaño() >= limite) return;
        }

        MapSimple<Character, NodoTrie> hijos = nodo.hijos;
        for (Character c : hijos.keys()) {
            NodoTrie hijo = hijos.get(c);
            if (hijo != null) {
                recolectar(hijo, prefijoActual + c, resultados, limite);
                if (resultados.tamaño() >= limite) return;
            }
        }
    }
}

