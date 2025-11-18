package com.myapp.dto;
import com.myapp.model.enums.Role; 


public class UsuarioDto {
    private String user;
    private String nombre;
    private Role role;
    private int totalFavoritos;

    public UsuarioDto() {}

    public UsuarioDto(String user, String nombre, Role role, int totalFavoritos) {
        this.user = user;
        this.nombre = nombre;
        this.role = role;
        this.totalFavoritos = totalFavoritos;
    }

    public String getUser(){return user; }

    public void setUser(String user) {this.user = user; }
        
    public String getNombre() {return nombre; }

    public void setNombre(String nombre){this.nombre = nombre; }

    public Role getRole() {return role; }

    public void setRole(Role role) {this.role = role; }

    public int getTotalFavoritos() { return totalFavoritos; }

    public void setTotalFavoritos(int totalFavoritos) { this.totalFavoritos = totalFavoritos; }
}
