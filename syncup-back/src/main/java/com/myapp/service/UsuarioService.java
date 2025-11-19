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
         // Crear administrador
        Usuario admin = new Usuario("admin", "admin123", "Administrador", Role.ADMIN);
        usuarios.put(admin.getUser(), admin);

        // Crear usuarios de prueba
        String[][] usuariosPrueba = {
            {"juan", "123456", "Juan García"},
            {"maria", "123456", "María López"},
            {"carlos", "123456", "Carlos Rodríguez"},
            {"ana", "123456", "Ana Martínez"},
            {"pedro", "123456", "Pedro Sánchez"},
            {"laura", "123456", "Laura Fernández"},
            {"diego", "123456", "Diego Torres"},
            {"sofia", "123456", "Sofía Ramírez"}
        };

        // Primero crear todos los usuarios y registrarlos en el grafo
        for (String[] datos : usuariosPrueba) {
            Usuario u = new Usuario(datos[0], datos[1], datos[2], Role.USER);
            usuarios.put(u.getUser(), u);
            grafoSocialService.registrarUsuarioEnGrafo(u.getUser());
        }

        // Luego crear las conexiones sociales de prueba
        try {
            // Juan sigue a María, Carlos y Ana
            grafoSocialService.seguir("juan", "maria");
            grafoSocialService.seguir("juan", "carlos");
            grafoSocialService.seguir("juan", "ana");

            // María sigue a Juan y Pedro
            grafoSocialService.seguir("maria", "juan");
            grafoSocialService.seguir("maria", "pedro");

            // Carlos sigue a Ana y Laura
            grafoSocialService.seguir("carlos", "ana");
            grafoSocialService.seguir("carlos", "laura");

            // Ana sigue a Diego
            grafoSocialService.seguir("ana", "diego");

            // Pedro sigue a Laura y Sofía
            grafoSocialService.seguir("pedro", "laura");
            grafoSocialService.seguir("pedro", "sofia");

        } catch (Exception e) {
            // Ignorar errores en seed
            System.err.println("Error al crear conexiones de prueba: " + e.getMessage());
        }
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
        int totalFavoritos = 0;
        if (usuario.getCancionesFavoritas() != null) {
            totalFavoritos = usuario.getCancionesFavoritas().tamaño();
        }
        dto.setTotalFavoritos(totalFavoritos);
        
        return dto;
    }

}
