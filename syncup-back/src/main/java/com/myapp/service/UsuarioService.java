package com.myapp.service;

import java.util.HashMap;
import java.util.Map;
import com.myapp.model.Usuario;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final Map<String, Usuario> usuarios = new HashMap<>();

    
    
}
