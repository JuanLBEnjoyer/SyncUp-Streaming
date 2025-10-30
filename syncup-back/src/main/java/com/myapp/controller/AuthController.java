package com.myapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.dto.UsuarioDto;
import com.myapp.model.Usuario;
import com.myapp.service.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario u) {
        try{
            Usuario usuario = usuarioService.registrar(u.getUser(), u.getPassword(), u.getNombre());
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(usuarioDto);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario u) {
        try{
            Usuario usuario = usuarioService.login(u.getUser(), u.getPassword());
            UsuarioDto usuarioDto = usuarioService.toDto(usuario);
            return ResponseEntity.ok(usuarioDto);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
}
