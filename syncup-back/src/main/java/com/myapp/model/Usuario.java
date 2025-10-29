package com.myapp.model;

import java.util.LinkedList;
import java.util.Objects;

public class Usuario {

    private String user;
    private String password;
    private String nombre;
    private LinkedList<Cancion> cancionesFavoritas;

    public Usuario() {
        this.cancionesFavoritas = new LinkedList<>();
    }

    public Usuario(String user, String password, String nombre) {
        this.user = user;
        this.password = password;
        this.nombre = nombre;
        this.cancionesFavoritas = new LinkedList<>();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LinkedList<Cancion> getCancionesFavoritas() {
        return cancionesFavoritas;
    }

    public void setCancionesFavoritas(LinkedList<Cancion> cancionesFavoritas) {
        this.cancionesFavoritas = cancionesFavoritas;
    }

    public void addCancionFavorita(Cancion cancion) {
        cancionesFavoritas.add(cancion);
    }

    public void removeCancionFavorita(Cancion cancion) {
        cancionesFavoritas.remove(cancion);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", cancionesFavoritas=" + cancionesFavoritas +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario u = (Usuario) o;
        return Objects.equals(user, u.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
    
}
