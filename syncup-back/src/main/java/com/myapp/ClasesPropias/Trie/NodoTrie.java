package com.myapp.ClasesPropias.Trie;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.Map.HashMapSimple;


public class NodoTrie {

    MapSimple<Character, NodoTrie> hijos;
    boolean esFinDePalabra;
    String palabraOriginal; 

    public NodoTrie() {
        this.hijos = new HashMapSimple<>();
        this.esFinDePalabra = false;
        this.palabraOriginal = null;
    }

    public MapSimple<Character, NodoTrie> getHijos() {
        return hijos;
    }

    public boolean isEsFinDePalabra() {
        return esFinDePalabra;
    }

    public void setEsFinDePalabra(boolean esFinDePalabra) {
        this.esFinDePalabra = esFinDePalabra;
    }

    public String getPalabraOriginal() {
        return palabraOriginal;
    }

    public void setPalabraOriginal(String palabraOriginal) {
        this.palabraOriginal = palabraOriginal;
    }
}

