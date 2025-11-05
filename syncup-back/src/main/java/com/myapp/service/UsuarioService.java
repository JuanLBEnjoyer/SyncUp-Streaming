package com.myapp.service;

import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.dto.UsuarioDto;
import com.myapp.model.Usuario;
import com.myapp.model.enums.Role;

import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final MapSimple<String, Usuario> usuarios = new HashMapSimple<>();

    public UsuarioService() {
        // Usuario admin por defecto
        Usuario admin = new Usuario("admin", "admin123", "Administrador", Role.ADMIN);
        usuarios.put(admin.getUser(), admin);
    }

    public Usuario registrar(String user, String password, String nombre) {
        if (usuarios.containsKey(user)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Usuario nuevoUsuario = new Usuario(user, password, nombre, Role.USER);
        usuarios.put(user, nuevoUsuario);
        return nuevoUsuario;
    }

    public Usuario login(String user, String password) {
        Usuario usuario = usuarios.get(user);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (!usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        return usuario;
    }

    public Usuario obtenerUsuario(String user) {
        return usuarios.get(user);
    }

    public UsuarioDto toDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setUser(usuario.getUser());
        dto.setNombre(usuario.getNombre());
        dto.setRole(usuario.getRole());
        return dto;
    }

}
