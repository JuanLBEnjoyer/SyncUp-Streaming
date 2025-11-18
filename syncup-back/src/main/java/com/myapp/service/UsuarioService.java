package com.myapp.service;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.Map.MapSimple;
import com.myapp.dto.UsuarioDto;
import com.myapp.model.Cancion;
import com.myapp.model.Usuario;
import com.myapp.model.enums.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final MapSimple<String, Usuario> usuarios = new HashMapSimple<>();
    private final GrafoSocialService grafoSocialService;
    private final CancionService cancionService;


    public UsuarioService(GrafoSocialService grafoSocialService, CancionService cancionService) {
        this.grafoSocialService = grafoSocialService;
        this.cancionService = cancionService;
    }

    @PostConstruct
    void seed() {
        Usuario admin = new Usuario("admin", "admin123", "Administrador", Role.ADMIN);
        usuarios.put(admin.getUser(), admin);
    }

    public Usuario registrar(String user, String password, String nombre) {
        if (usuarios.containsKey(user)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Usuario nuevoUsuario = new Usuario(user, password, nombre, Role.USER);
        usuarios.put(user, nuevoUsuario);
        grafoSocialService.registrarUsuarioEnGrafo(user);
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

    public Usuario actualizarPerfil(String user, String nuevoNombre, String nuevaPassword) {
        Usuario u = usuarios.get(user);

        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (nuevoNombre != null) {
            u.setNombre(nuevoNombre);
        }

        if (nuevaPassword != null) {
            u.setPassword(nuevaPassword);
        }
        
        return u;
    }   

    public Usuario obtenerUsuario(String user) {
        return usuarios.get(user);
    }

    public ListaEnlazada<Usuario> listarTodos() {
        ListaEnlazada<Usuario> lista = new ListaEnlazada<>();
        
        for (String key : usuarios.keys()) {
            Usuario u = usuarios.get(key);
            if (u != null) {
                lista.agregar(u);
            }
        }
        
        return lista;
    }

    public void eliminarUsuario(String user) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }

        Usuario u = usuarios.get(user);
        
        if (u == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        if (u.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("No se puede eliminar al administrador");
        }

        usuarios.remove(user);

        if (grafoSocialService.existeEnGrafo(user)) {
            grafoSocialService.eliminarUsuarioDelGrafo(user);
        }
    }

    public int contarUsuarios() {
        return usuarios.size();
    }

    public void agregarFavorito(String user, Long idCancion) {
        Usuario u = usuarios.get(user);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Cancion c = cancionService.obtener(idCancion);
        if (c == null) {
            throw new IllegalArgumentException("Canción no encontrada");
        }

        if (!u.getCancionesFavoritas().contiene(c)) {
            u.addCancionFavorita(c);
        }
    }

    public void eliminarFavorito(String user, Long idCancion) {
        Usuario u = usuarios.get(user);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Cancion c = cancionService.obtener(idCancion);
        if (c == null) {
            throw new IllegalArgumentException("Canción no encontrada");
        }

        u.removeCancionFavorita(c);
    }

    public ListaEnlazada<Cancion> obtenerFavoritos(String user) {
        Usuario u = usuarios.get(user);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return u.getCancionesFavoritas();
    }

    public UsuarioDto toDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setUser(usuario.getUser());
        dto.setNombre(usuario.getNombre());
        dto.setRole(usuario.getRole());
        return dto;
    }

}
