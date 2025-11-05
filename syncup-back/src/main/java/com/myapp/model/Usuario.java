package com.myapp.model;

import java.util.Objects;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.model.enums.Role;  

public class Usuario {

    private String user;
    private String password;
    private String nombre;
    private ListaEnlazada<Cancion> cancionesFavoritas;
    private Role role;

    public Usuario() {
        this.cancionesFavoritas = new ListaEnlazada<>();
        this.role = Role.USER;
    }

    public Usuario(String user, String password, String nombre, Role role) {
        this.user = user;
        this.password = password;
        this.nombre = nombre;
        this.cancionesFavoritas = new ListaEnlazada<>();
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public ListaEnlazada<Cancion> getCancionesFavoritas() {
        return cancionesFavoritas;
    }

    public void setCancionesFavoritas(ListaEnlazada<Cancion> cancionesFavoritas) {
        this.cancionesFavoritas = cancionesFavoritas;
    }

    public void addCancionFavorita(Cancion cancion) {
        cancionesFavoritas.agregar(cancion);
    }

    public void removeCancionFavorita(Cancion cancion) {
        cancionesFavoritas.eliminar(cancion);
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
