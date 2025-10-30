package com.myapp.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthLoginDto {
    private String user;
    private String password;

    public AuthLoginDto() {
    }

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @NotBlank(message = "La contraseña no puede estar vacía")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
