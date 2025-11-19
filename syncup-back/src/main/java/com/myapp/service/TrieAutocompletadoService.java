package com.myapp.service;

import org.springframework.stereotype.Service;

import com.myapp.ClasesPropias.Trie.TrieAutocompletado;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;

@Service
public class TrieAutocompletadoService {

    private final TrieAutocompletado trie = new TrieAutocompletado();

    public void registrarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) return;
        trie.insertar(titulo);
    }

    public ListaEnlazada<String> sugerir(String prefijo) {
        return sugerir(prefijo, 10);
    }

    public ListaEnlazada<String> sugerir(String prefijo, int limite) {
        if (prefijo == null || prefijo.isBlank() || limite <= 0) {
            return new ListaEnlazada<>();
        }
        return trie.autocompletar(prefijo, limite);
    }
}
