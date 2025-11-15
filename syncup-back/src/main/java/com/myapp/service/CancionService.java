package com.myapp.service;

import org.springframework.stereotype.Service;
import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.model.Cancion;
import com.myapp.model.enums.Genero;


@Service
public class CancionService {

    private final GrafoSimilitudService grafoService;
    private final TrieAutocompletadoService trieService;
    private long secuencia = 1L;

    public CancionService(GrafoSimilitudService grafoService, TrieAutocompletadoService trieService) {
        this.grafoService = grafoService;
        this.trieService = trieService;
    }

    // CRUD básico (las validaciones de campos vacíos las haces en el Controller con DTO + @Valid)

    public Cancion crear(String titulo, String artista, Genero genero, int anio, double duracion) {
        Cancion c = new Cancion();
        c.setId(secuencia++);
        c.setTitulo(titulo);
        c.setArtista(artista);
        c.setGenero(genero);
        c.setAño(anio);
        c.setDuracion(duracion);

        grafoService.registrarCancion(c);
        trieService.registrarTitulo(titulo);
        return c;
    }

    public Cancion obtener(Long id) {
        return grafoService.obtenerPorId(id);
    }

    public ListaEnlazada<Cancion> listarTodas() {
        return grafoService.obtenerTodas();
    }

    public Cancion actualizar(Cancion c) {
        if (c == null || c.getId() == null) {
            throw new IllegalArgumentException("La canción debe tener ID para actualizar");
        }
        if (!grafoService.existeCancion(c.getId())) {
            throw new IllegalArgumentException("La canción no existe");
        }
        grafoService.registrarCancion(c); // actúa como reemplazo de metadata
        trieService.registrarTitulo(c.getTitulo());
        return c;
    }

    public void eliminar(Long id) {
        grafoService.eliminarCancion(id);
    }

    public boolean existe(Long id) {
        return grafoService.existeCancion(id);
    }

    // Similitud (fachadas)

    public void relacionar(Long id1, Long id2, double similitud) {
        grafoService.establecerSimilitud(id1, id2, similitud);
    }

    public double similitud(Long id1, Long id2) {
        return grafoService.obtenerSimilitud(id1, id2);
    }

    public SetPropio<Cancion> similares(Long id) {
        return grafoService.vecinos(id);
    }

    public ListaEnlazada<Cancion> caminoMasSimilar(Long origen, Long destino) {
        return grafoService.caminoMasSimilar(origen, destino);
    }

    public ListaEnlazada<String> sugerirTitulos(String prefijo, int limite) {
        return trieService.sugerir(prefijo, limite);
    }

    public ListaEnlazada<String> sugerirTitulos(String prefijo) {
        return trieService.sugerir(prefijo);
    }

    public ListaEnlazada<Cancion> buscar(String artista,
                                         Genero genero,
                                         Integer anio,
                                         boolean usarAnd) {

        // Normalizar filtros
        String filtroArtista = (artista == null) ? null : artista.trim().toLowerCase();
        Genero filtroGenero = genero;
        Integer filtroAnio = anio;

        boolean hayFiltroArtista = (filtroArtista != null && !filtroArtista.isBlank());
        boolean hayFiltroGenero  = (filtroGenero != null);
        boolean hayFiltroAnio    = (filtroAnio != null);

        // Si no hay ningún filtro, devolvemos todas las canciones
        if (!hayFiltroArtista && !hayFiltroGenero && !hayFiltroAnio) {
            return listarTodas();
        }

        ListaEnlazada<Cancion> todas = listarTodas();
        ListaEnlazada<Cancion> resultado = new ListaEnlazada<>();

        for (int i = 0; i < todas.tamaño(); i++) {
            Cancion c = todas.obtener(i);

            boolean coincideArtista = true;
            boolean coincideGenero  = true;
            boolean coincideAnio    = true;

            if (hayFiltroArtista) {
                String artistaCancion = (c.getArtista() == null) ? "" : c.getArtista().toLowerCase();
                // puedes usar contains o equalsIgnoreCase según lo que quieras
                coincideArtista = artistaCancion.contains(filtroArtista);
            }

            if (hayFiltroGenero) {
                coincideGenero = (c.getGenero() == filtroGenero);
            }

            if (hayFiltroAnio) {
                coincideAnio = (c.getAño() == filtroAnio.intValue());
            }

            boolean coincide;
            if (usarAnd) {
                // AND: debe cumplir todos los filtros que existan
                coincide = true;
                if (hayFiltroArtista) coincide &= coincideArtista;
                if (hayFiltroGenero)  coincide &= coincideGenero;
                if (hayFiltroAnio)    coincide &= coincideAnio;
            } else {
                // OR: basta que cumpla al menos uno de los filtros presentes
                coincide = false;
                if (hayFiltroArtista) coincide |= coincideArtista;
                if (hayFiltroGenero)  coincide |= coincideGenero;
                if (hayFiltroAnio)    coincide |= coincideAnio;
            }

            if (coincide) {
                resultado.agregar(c);
            }
        }
        return resultado;
    }
}

