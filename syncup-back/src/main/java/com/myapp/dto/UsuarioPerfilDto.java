package com.myapp.dto;
import jakarta.validation.constraints.Size;

public class UsuarioPerfilDto {

    private String nombre;
    private String password;

    public UsuarioPerfilDto() {}

    @Size(min = 1, message = "El nombre debe tener al menos 1 caracter")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

