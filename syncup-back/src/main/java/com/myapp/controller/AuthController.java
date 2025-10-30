package com.myapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.dto.AuthLoginDto;
import com.myapp.dto.AuthRegisterDto;
import com.myapp.dto.UsuarioDto;
import com.myapp.model.Usuario;
import com.myapp.service.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody AuthRegisterDto dto) {
        try{
            Usuario usuario = usuarioService.registrar(dto.getUser(), dto.getPassword(), dto.getNombre());
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(usuarioDto);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginDto dto) {
        try{
            Usuario usuario = usuarioService.login(dto.getUser(), dto.getPassword());
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(usuarioDto);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
        errors.put(error.getField(), error.getDefaultMessage());
    });

    return ResponseEntity.badRequest().body(Map.of(
        "message", "Error de validación",
        "errors", errors
    ));
}

    
    
}
