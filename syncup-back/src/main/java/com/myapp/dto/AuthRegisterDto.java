package com.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class AuthRegisterDto {

    private String user;
    private String password;
    private String nombre;

    public AuthRegisterDto() {
    }

    @NotBlank (message = "El nombre de usuario no puede estar vacío")
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    @NotBlank (message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @NotBlank (message = "El nombre no puede estar vacío")
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
}
