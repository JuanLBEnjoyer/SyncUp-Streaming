package com.myapp.dto;

import com.myapp.model.Cancion;
import com.myapp.model.enums.Genero;

public class CancionDto {

    private Long id;
    private String titulo;
    private String artista;
    private Genero genero;
    private int año;
    private double duracion;

    public CancionDto() {}

    public CancionDto(Cancion c) {
        this.id = c.getId();
        this.titulo = c.getTitulo();
        this.artista = c.getArtista();
        this.genero = c.getGenero();
        this.año = c.getAño();       
        this.duracion = c.getDuracion();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public int getAnio() { return año; }
    public void setAnio(int año) { this.año = año; }

    public double getDuracion() { return duracion; }
    public void setDuracion(double duracion) { this.duracion = duracion; }
}

