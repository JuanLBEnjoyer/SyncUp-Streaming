package com.myapp.dto;

import com.myapp.model.enums.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CancionCreateDto {

    private String titulo;
    private String artista;
    private Genero genero;
    private int año;
    private double duracion;

    public CancionCreateDto() {}

    @NotBlank(message = "El título no puede estar vacío")
    public String getTitulo() { 
        return titulo; 
    }
    public void setTitulo(String titulo) { 
        this.titulo = titulo;
    }

    @NotBlank(message = "El artista no puede estar vacío")
    public String getArtista() { 
        return artista; 
    }
    public void setArtista(String artista) { 
        this.artista = artista; 
    }

    @NotNull(message = "Debe seleccionar un género")
    public Genero getGenero() { 
        return genero; 
    }
    public void setGenero(Genero genero) { 
        this.genero = genero; 
    }

    @Positive(message = "El año debe ser positivo")
    public int getAnio() { 
        return año; 
    }
    public void setAnio(int año) { 
        this.año = año; 
    }

    @Positive(message = "La duración debe ser mayor a cero")
    public double getDuracion() { 
        return duracion; 
    }
    public void setDuracion(double duracion) { 
        this.duracion = duracion; 
    }
}

