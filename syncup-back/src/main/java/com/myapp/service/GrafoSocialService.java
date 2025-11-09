package com.myapp.service;

import com.myapp.model.grafos.GrafoSocial;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;
import org.springframework.stereotype.Service;

@Service
public class GrafoSocialService {

    private final GrafoSocial grafo = new GrafoSocial();

    public void registrarUsuarioEnGrafo(String username) {
        validarUsername(username);
        grafo.agregarUsuario(username);
    }

    public void eliminarUsuarioDelGrafo(String username) {
        validarUsername(username);
        if (!grafo.contieneUsuario(username))
            throw new IllegalArgumentException("El usuario no existe en el grafo");
        grafo.eliminarUsuario(username);
    }

    public void seguir(String userA, String userB) {
        validarUsername(userA);
        validarUsername(userB);
        if (userA.equals(userB))
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo");
        if (!grafo.contieneUsuario(userA) || !grafo.contieneUsuario(userB))
            throw new IllegalArgumentException("Uno o ambos usuarios no existen en el grafo");

        grafo.conectar(userA, userB);
    }

    public void dejarDeSeguir(String userA, String userB) {
        validarUsername(userA);
        validarUsername(userB);
        if (!grafo.contieneUsuario(userA) || !grafo.contieneUsuario(userB))
            throw new IllegalArgumentException("Uno o ambos usuarios no existen en el grafo");

        grafo.desconectar(userA, userB);
    }

    public SetPropio<String> seguidos(String user) {
        validarUsername(user);
        if (!grafo.contieneUsuario(user))
            throw new IllegalArgumentException("El usuario no existe en el grafo");
        return grafo.seguidos(user);
    }

    public SetPropio<String> seguidores(String user) {
        validarUsername(user);
        if (!grafo.contieneUsuario(user))
            throw new IllegalArgumentException("El usuario no existe en el grafo");
        return grafo.seguidores(user);
    }

    public ListaEnlazada<String> sugerenciasIds(String user, int limite) {
        validarUsername(user);
        if (limite <= 0) return new ListaEnlazada<>();
        if (!grafo.contieneUsuario(user))
            throw new IllegalArgumentException("El usuario no existe en el grafo");
        return grafo.sugerir(user, limite);
    }

    public boolean existeEnGrafo(String user) { return grafo.contieneUsuario(user); }

    private void validarUsername(String u) {
        if (u == null || u.trim().isEmpty())
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
    }
}

