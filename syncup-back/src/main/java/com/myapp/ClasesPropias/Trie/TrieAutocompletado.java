package com.myapp.ClasesPropias.Trie;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;

public class TrieAutocompletado {

    private final NodoTrie raiz;

    public TrieAutocompletado() {
        this.raiz = new NodoTrie();
    }

    public void insertar(String palabra) {
        if (palabra == null || palabra.isBlank()) return;

        String claveMinuscula = palabra.toLowerCase();
        NodoTrie actual = raiz;
        
        for (int i = 0; i < claveMinuscula.length(); i++) {
            char c = claveMinuscula.charAt(i);
            MapSimple<Character, NodoTrie> hijos = actual.hijos;
            NodoTrie hijo = hijos.get(c);
            if (hijo == null) {
                hijo = new NodoTrie();
                hijos.put(c, hijo);
            }
            actual = hijo;
        }
        actual.esFinDePalabra = true;
        actual.palabraOriginal = palabra;
    }

    public boolean contienePalabra(String palabra) {
        if (palabra == null) return false;

        String claveMinuscula = palabra.toLowerCase();
        NodoTrie actual = raiz;
        
        for (int i = 0; i < claveMinuscula.length(); i++) {
            char c = claveMinuscula.charAt(i);
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

        String prefijoMinuscula = prefijo.toLowerCase();
        NodoTrie nodoPrefijo = raiz;

        for (int i = 0; i < prefijoMinuscula.length(); i++) {
            char c = prefijoMinuscula.charAt(i);
            NodoTrie hijo = nodoPrefijo.hijos.get(c);
            if (hijo == null) {
                return resultados;
            }
            nodoPrefijo = hijo;
        }

        recolectar(nodoPrefijo, resultados, limite);
        return resultados;
    }

    private void recolectar(NodoTrie nodo, ListaEnlazada<String> resultados, int limite) {

        if (resultados.tamaño() >= limite) return;

        if (nodo.esFinDePalabra && nodo.palabraOriginal != null) {
            resultados.agregar(nodo.palabraOriginal);
            if (resultados.tamaño() >= limite) return;
        }

        MapSimple<Character, NodoTrie> hijos = nodo.hijos;
        for (Character c : hijos.keys()) {
            NodoTrie hijo = hijos.get(c);
            if (hijo != null) {
                recolectar(hijo, resultados, limite);
                if (resultados.tamaño() >= limite) return;
            }
        }
    }
}

