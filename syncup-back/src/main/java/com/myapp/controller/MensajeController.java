package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MensajeController {

    @GetMapping("/") // cuando entres a http://localhost:8080/
    public String inicio() {
        return "¡Servidor Spring Boot funcionando correctamente! 🚀";
    }

    @GetMapping("/api/hola") // ruta de prueba
    public String hola() {
        return "Hola desde Spring Boot 👋";
    }
}