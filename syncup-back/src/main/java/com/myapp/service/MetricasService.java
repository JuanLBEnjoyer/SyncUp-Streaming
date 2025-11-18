package com.myapp.service;

import org.springframework.stereotype.Service;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.dto.MetricasDto;
import com.myapp.model.Cancion;
import com.myapp.model.Usuario;


@Service
public class MetricasService {

    private final UsuarioService usuarioService;
    private final CancionService cancionService;

    public MetricasService(UsuarioService usuarioService, CancionService cancionService) {
        this.usuarioService = usuarioService;
        this.cancionService = cancionService;
    }

    public MetricasDto calcularMetricas() {
        MetricasDto metricas = new MetricasDto();

        metricas.setTotalUsuarios(usuarioService.contarUsuarios());
        
        ListaEnlazada<Cancion> todasCanciones = cancionService.listarTodas();
        metricas.setTotalCanciones(todasCanciones.tamaño());

        MapSimple<String, Integer> cancionesPorGenero = contarPorGenero(todasCanciones);
        metricas.setCancionesPorGenero(cancionesPorGenero);

        MapSimple<String, Integer> cancionesPorDecada = contarPorDecada(todasCanciones);
        metricas.setCancionesPorDecada(cancionesPorDecada);

        MapSimple<String, Integer> cancionesPorArtista = contarPorArtista(todasCanciones);
        ListaEnlazada<TopItem> topArtistas = obtenerTop(cancionesPorArtista, 10);
        metricas.setTopArtistas(topArtistas);

        MapSimple<String, Integer> generosFavoritos = contarGenerosFavoritos();
        metricas.setGenerosMasFavoritos(generosFavoritos);

        double duracionPromedio = calcularDuracionPromedio(todasCanciones);
        metricas.setDuracionPromedio(duracionPromedio);

        int añoMasAntiguo = encontrarAñoMinimo(todasCanciones);
        int añoMasReciente = encontrarAñoMaximo(todasCanciones);
        metricas.setAñoMasAntiguo(añoMasAntiguo);
        metricas.setAñoMasReciente(añoMasReciente);

        return metricas;
    }

    private MapSimple<String, Integer> contarPorGenero(ListaEnlazada<Cancion> canciones) {
        MapSimple<String, Integer> contador = new HashMapSimple<>();

        for (int i = 0; i < canciones.tamaño(); i++) {
            Cancion c = canciones.obtener(i);
            String genero = c.getGenero().toString();

            Integer actual = contador.get(genero);
            if (actual == null) {
                contador.put(genero, 1);
            } else {
                contador.put(genero, actual + 1);
            }
        }

        return contador;
    }

    private MapSimple<String, Integer> contarPorDecada(ListaEnlazada<Cancion> canciones) {
        MapSimple<String, Integer> contador = new HashMapSimple<>();

        for (int i = 0; i < canciones.tamaño(); i++) {
            Cancion c = canciones.obtener(i);
            int año = c.getAño();
            
            int decada = (año / 10) * 10;
            String decadaStr = decada + "s";

            Integer actual = contador.get(decadaStr);
            if (actual == null) {
                contador.put(decadaStr, 1);
            } else {
                contador.put(decadaStr, actual + 1);
            }
        }

        return contador;
    }

    private MapSimple<String, Integer> contarPorArtista(ListaEnlazada<Cancion> canciones) {
        MapSimple<String, Integer> contador = new HashMapSimple<>();

        for (int i = 0; i < canciones.tamaño(); i++) {
            Cancion c = canciones.obtener(i);
            String artista = c.getArtista();

            Integer actual = contador.get(artista);
            if (actual == null) {
                contador.put(artista, 1);
            } else {
                contador.put(artista, actual + 1);
            }
        }

        return contador;
    }

    private MapSimple<String, Integer> contarGenerosFavoritos() {
        MapSimple<String, Integer> contador = new HashMapSimple<>();

        ListaEnlazada<Usuario> usuarios = usuarioService.listarTodos();

        for (int i = 0; i < usuarios.tamaño(); i++) {
            Usuario u = usuarios.obtener(i);
            ListaEnlazada<Cancion> favoritos = u.getCancionesFavoritas();

            for (int j = 0; j < favoritos.tamaño(); j++) {
                Cancion c = favoritos.obtener(j);
                String genero = c.getGenero().toString();

                Integer actual = contador.get(genero);
                if (actual == null) {
                    contador.put(genero, 1);
                } else {
                    contador.put(genero, actual + 1);
                }
            }
        }

        return contador;
    }

    private ListaEnlazada<TopItem> obtenerTop(MapSimple<String, Integer> mapa, int n) {
        ListaEnlazada<TopItem> items = new ListaEnlazada<>();
        
        for (String key : mapa.keys()) {
            Integer value = mapa.get(key);
            if (value != null) {
                items.agregar(new TopItem(key, value));
            }
        }

        for (int i = 0; i < items.tamaño() && i < n; i++) {
            int maxIdx = i;
            
            for (int j = i + 1; j < items.tamaño(); j++) {
                if (items.obtener(j).getValor() > items.obtener(maxIdx).getValor()) {
                    maxIdx = j;
                }
            }

            if (maxIdx != i) {
                TopItem temp = items.obtener(i);
                TopItem max = items.obtener(maxIdx);
                
                ListaEnlazada<TopItem> nuevaLista = new ListaEnlazada<>();
                for (int k = 0; k < items.tamaño(); k++) {
                    if (k == i) {
                        nuevaLista.agregar(max);
                    } else if (k == maxIdx) {
                        nuevaLista.agregar(temp);
                    } else {
                        nuevaLista.agregar(items.obtener(k));
                    }
                }
                items = nuevaLista;
            }
        }

        ListaEnlazada<TopItem> resultado = new ListaEnlazada<>();
        for (int i = 0; i < items.tamaño() && i < n; i++) {
            resultado.agregar(items.obtener(i));
        }

        return resultado;
    }

    private double calcularDuracionPromedio(ListaEnlazada<Cancion> canciones) {
        if (canciones.estaVacia()) {
            return 0.0;
        }

        double suma = 0.0;
        for (int i = 0; i < canciones.tamaño(); i++) {
            suma += canciones.obtener(i).getDuracion();
        }

        return suma / canciones.tamaño();
    }

    private int encontrarAñoMinimo(ListaEnlazada<Cancion> canciones) {
        if (canciones.estaVacia()) {
            return 0;
        }

        int minimo = canciones.obtener(0).getAño();
        for (int i = 1; i < canciones.tamaño(); i++) {
            int año = canciones.obtener(i).getAño();
            if (año < minimo) {
                minimo = año;
            }
        }

        return minimo;
    }

    private int encontrarAñoMaximo(ListaEnlazada<Cancion> canciones) {
        if (canciones.estaVacia()) {
            return 0;
        }

        int maximo = canciones.obtener(0).getAño();
        for (int i = 1; i < canciones.tamaño(); i++) {
            int año = canciones.obtener(i).getAño();
            if (año > maximo) {
                maximo = año;
            }
        }

        return maximo;
    }

    public static class TopItem {
        private final String nombre;
        private final int valor;

        public TopItem(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public int getValor() {
            return valor;
        }
    }
}
