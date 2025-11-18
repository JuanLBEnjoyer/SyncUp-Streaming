package com.myapp.dto;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.service.MetricasService.TopItem;

public class MetricasDto {

    private int totalUsuarios;
    private int totalCanciones;
    private MapSimple<String, Integer> cancionesPorGenero;
    private MapSimple<String, Integer> cancionesPorDecada;
    private ListaEnlazada<TopItem> topArtistas;
    private MapSimple<String, Integer> generosMasFavoritos;
    private double duracionPromedio;
    private int añoMasAntiguo;
    private int añoMasReciente;

    public MetricasDto() {}

    // Getters y Setters

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public int getTotalCanciones() {
        return totalCanciones;
    }

    public void setTotalCanciones(int totalCanciones) {
        this.totalCanciones = totalCanciones;
    }

    public MapSimple<String, Integer> getCancionesPorGenero() {
        return cancionesPorGenero;
    }

    public void setCancionesPorGenero(MapSimple<String, Integer> cancionesPorGenero) {
        this.cancionesPorGenero = cancionesPorGenero;
    }

    public MapSimple<String, Integer> getCancionesPorDecada() {
        return cancionesPorDecada;
    }

    public void setCancionesPorDecada(MapSimple<String, Integer> cancionesPorDecada) {
        this.cancionesPorDecada = cancionesPorDecada;
    }

    public ListaEnlazada<TopItem> getTopArtistas() {
        return topArtistas;
    }

    public void setTopArtistas(ListaEnlazada<TopItem> topArtistas) {
        this.topArtistas = topArtistas;
    }

    public MapSimple<String, Integer> getGenerosMasFavoritos() {
        return generosMasFavoritos;
    }

    public void setGenerosMasFavoritos(MapSimple<String, Integer> generosMasFavoritos) {
        this.generosMasFavoritos = generosMasFavoritos;
    }

    public double getDuracionPromedio() {
        return duracionPromedio;
    }

    public void setDuracionPromedio(double duracionPromedio) {
        this.duracionPromedio = duracionPromedio;
    }

    public int getAñoMasAntiguo() {
        return añoMasAntiguo;
    }

    public void setAñoMasAntiguo(int añoMasAntiguo) {
        this.añoMasAntiguo = añoMasAntiguo;
    }

    public int getAñoMasReciente() {
        return añoMasReciente;
    }

    public void setAñoMasReciente(int añoMasReciente) {
        this.añoMasReciente = añoMasReciente;
    }
}
