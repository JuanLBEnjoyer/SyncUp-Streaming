package com.myapp.util;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Iterador.IteradorPropio;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.dto.CancionDto;
import com.myapp.dto.UsuarioDto;
import com.myapp.model.Cancion;
import com.myapp.model.Usuario;


public class ConversorDto {

    public static CancionDto[] listaCancionesAArray(ListaEnlazada<Cancion> lista) {
        if (lista == null || lista.estaVacia()) {
            return new CancionDto[0];
        }

        CancionDto[] array = new CancionDto[lista.tamaño()];
        
        for (int i = 0; i < lista.tamaño(); i++) {
            array[i] = new CancionDto(lista.obtener(i));
        }
        
        return array;
    }

    public static UsuarioDto[] listaUsuariosAArray(ListaEnlazada<Usuario> lista) {
        if (lista == null || lista.estaVacia()) {
            return new UsuarioDto[0];
        }

        UsuarioDto[] array = new UsuarioDto[lista.tamaño()];
        
        for (int i = 0; i < lista.tamaño(); i++) {
        Usuario u = lista.obtener(i);
        int totalFavoritos = 0;
        if (u.getCancionesFavoritas() != null) {
            totalFavoritos = u.getCancionesFavoritas().tamaño();
        }
        array[i] = new UsuarioDto(u.getUser(), u.getNombre(), u.getRole(), totalFavoritos);
    }
        
        return array;
    }

    public static UsuarioDto[] setUsuariosAArray(SetPropio<String> set, UsuarioProvider provider) {
        if (set == null || set.estaVacio()) {
        return new UsuarioDto[0];
    }
    int count = 0;
    IteradorPropio<String> it1 = set.iterador();
    while (it1.tieneSiguiente()) {
        String username = it1.siguiente();
        if (provider.obtener(username) != null) {
            count++;
        }
    }

    UsuarioDto[] array = new UsuarioDto[count];
    int index = 0;

    IteradorPropio<String> it2 = set.iterador();
    while (it2.tieneSiguiente()) {
        String username = it2.siguiente();
        Usuario u = provider.obtener(username);
        if (u != null) {
            int totalFavoritos = 0;
            if (u.getCancionesFavoritas() != null) {
                totalFavoritos = u.getCancionesFavoritas().tamaño();
            }
            array[index++] = new UsuarioDto(u.getUser(), u.getNombre(), u.getRole(), totalFavoritos);
        }
    }
    
        return array;
    }

    public static UsuarioDto[] listaUsernamesAArray(ListaEnlazada<String> lista, UsuarioProvider provider) {
    if (lista == null || lista.estaVacia()) {
        return new UsuarioDto[0];
    }

    int count = 0;
    for (int i = 0; i < lista.tamaño(); i++) {
        if (provider.obtener(lista.obtener(i)) != null) {
            count++;
        }
    }

    UsuarioDto[] array = new UsuarioDto[count];
    int index = 0;

    for (int i = 0; i < lista.tamaño(); i++) {
        Usuario u = provider.obtener(lista.obtener(i));
        if (u != null) {
            int totalFavoritos = 0;
            if (u.getCancionesFavoritas() != null) {
                totalFavoritos = u.getCancionesFavoritas().tamaño();
            }
            array[index++] = new UsuarioDto(u.getUser(), u.getNombre(), u.getRole(), totalFavoritos);
        }
    }
    
    return array;
}

    public static String[] listaStringsAArray(ListaEnlazada<String> lista) {
        if (lista == null || lista.estaVacia()) {
            return new String[0];
        }

        String[] array = new String[lista.tamaño()];
        
        for (int i = 0; i < lista.tamaño(); i++) {
            array[i] = lista.obtener(i);
        }
        
        return array;
    }

    @FunctionalInterface
    public interface UsuarioProvider {
        Usuario obtener(String username);
    }
}
