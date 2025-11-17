package com.myapp.service;

import org.springframework.stereotype.Service;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.ClasesPropias.Iterador.IteradorPropio;
import com.myapp.model.Cancion;
import com.myapp.model.Usuario;

@Service
public class RecomendacionService {

    private final UsuarioService usuarioService;
    private final GrafoSimilitudService grafoService;

    public RecomendacionService(UsuarioService usuarioService,
                                GrafoSimilitudService grafoService) {
        this.usuarioService = usuarioService;
        this.grafoService = grafoService;
    }

    public ListaEnlazada<Cancion> generarDescubrimientoSemanal(String username, int maxResultados) {
        ListaEnlazada<Cancion> playlist = new ListaEnlazada<>();

        if (maxResultados <= 0) return playlist;

        Usuario u = usuarioService.obtenerUsuario(username);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        ListaEnlazada<Cancion> favoritos = u.getCancionesFavoritas();

        if (favoritos == null || favoritos.tamaño() == 0) {
            ListaEnlazada<Cancion> todas = grafoService.obtenerTodas();
            for (int i = 0; i < todas.tamaño() && playlist.tamaño() < maxResultados; i++) {
                playlist.agregar(todas.obtener(i));
            }
            return playlist;
        }

        MapSimple<Long, Double> puntajes = new HashMapSimple<>();

        for (int i = 0; i < favoritos.tamaño(); i++) {
            Cancion fav = favoritos.obtener(i);
            if (fav == null || fav.getId() == null) continue;
            if (!grafoService.existeCancion(fav.getId())) continue;

            SetPropio<Cancion> vecinos = grafoService.vecinos(fav.getId());
            if (vecinos == null) continue;

            IteradorPropio<Cancion> it = vecinos.iterador();
            while (it.tieneSiguiente()) {
                Cancion vecino = it.siguiente();
                if (vecino == null || vecino.getId() == null) continue;

                if (favoritos.contiene(vecino)) continue;

                double similitud = grafoService.obtenerSimilitud(fav.getId(), vecino.getId());
                Double actual = puntajes.get(vecino.getId());
                if (actual == null) actual = 0.0;
                puntajes.put(vecino.getId(), actual + similitud);
            }
        }

        if (puntajes.isEmpty()) {
            ListaEnlazada<Cancion> todas = grafoService.obtenerTodas();
            for (int i = 0; i < todas.tamaño() && playlist.tamaño() < maxResultados; i++) {
                Cancion c = todas.obtener(i);
                if (!favoritos.contiene(c)) {
                    playlist.agregar(c);
                }
            }
            return playlist;
        }

        SetPropio<Long> usados = new SetPropio<>();

        while (playlist.tamaño() < maxResultados && usados.tamaño() < puntajes.size()) {
            Long mejorId = null;
            double mejorScore = 0.0;

            for (Long id : puntajes.keys()) {
                if (usados.contiene(id)) continue;
                Double score = puntajes.get(id);
                if (score != null && (mejorId == null || score > mejorScore)) {
                    mejorId = id;
                    mejorScore = score;
                }
            }

            if (mejorId == null) break;

            Cancion c = grafoService.obtenerPorId(mejorId);
            if (c != null) {
                playlist.agregar(c);
            }
            usados.agregar(mejorId);
        }

        return playlist;
    }
}

