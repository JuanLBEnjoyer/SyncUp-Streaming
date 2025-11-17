package com.myapp.model.grafos;

import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.ClasesPropias.Iterador.IteradorPropio;


public class GrafoSocial {

    private final MapSimple<String, SetPropio<String>> relaciones = new HashMapSimple<>();

    public void agregarUsuario(String user) {
        if (!relaciones.containsKey(user)) {
            relaciones.put(user, new SetPropio<>());
        }
    }

    public void eliminarUsuario(String user) {
        for (String u : relaciones.keys()) {
            SetPropio<String> s = relaciones.get(u);
            if (s != null) s.eliminar(user);
        }
        relaciones.remove(user);
    }

    public void conectar(String userA, String userB) {
        relaciones.get(userA).agregar(userB);
        relaciones.get(userB).agregar(userA);
    }

    public void desconectar(String userA, String userB) {
        SetPropio<String> a = relaciones.get(userA);
        SetPropio<String> b = relaciones.get(userB);
        if (a != null) a.eliminar(userB);
        if (b != null) b.eliminar(userA);
    }

    public SetPropio<String> seguidos(String user) {
        SetPropio<String> r = new SetPropio<>();
        SetPropio<String> s = relaciones.get(user);
        if (s == null) return r;
        IteradorPropio<String> it = s.iterador();
        while (it.tieneSiguiente()) r.agregar(it.siguiente());
        return r;
    }

    public SetPropio<String> seguidores(String user) {
        SetPropio<String> r = new SetPropio<>();
        for (String u : relaciones.keys()) {
            SetPropio<String> s = relaciones.get(u);
            if (s != null && s.contiene(user)) r.agregar(u);
        }
        return r;
    }

    public ListaEnlazada<String> sugerir(String user, int limite) {
        ListaEnlazada<String> sug = new ListaEnlazada<>();
        if (limite <= 0) return sug;

        SetPropio<String> visitados = new SetPropio<>();
        ListaEnlazada<String> cola = new ListaEnlazada<>();

        visitados.agregar(user);
        cola.agregar(user);

        while (!cola.estaVacia() && sug.tamaño() < limite) {
            String actual = cola.primero();
            cola.eliminar(actual);

            SetPropio<String> vecinos = relaciones.get(actual);
            if (vecinos == null) continue;

            IteradorPropio<String> it = vecinos.iterador();
            while (it.tieneSiguiente() && sug.tamaño() < limite) {
                String v = it.siguiente();
                if (!visitados.contiene(v)) {
                    visitados.agregar(v);
                    cola.agregar(v);

                    SetPropio<String> yoSigo = relaciones.get(user);
                    boolean yaSigo = (yoSigo != null && yoSigo.contiene(v));
                    if (!v.equals(user) && !yaSigo) {
                        sug.agregar(v);
                    }
                }
            }
        }
        return sug;
    }

    public boolean contieneUsuario(String user) { return relaciones.containsKey(user); }

    public int contarSeguidos(String user) {
        SetPropio<String> s = relaciones.get(user);
        return (s == null) ? 0 : s.tamaño();
    }

    public int contarSeguidores(String user) {
        int c = 0;
        for (String u : relaciones.keys()) {
            SetPropio<String> s = relaciones.get(u);
            if (s != null && s.contiene(user)) c++;
        }
        return c;
    }
}



