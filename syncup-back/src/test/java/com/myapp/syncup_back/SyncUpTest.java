package com.myapp.syncup_back;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.ClasesPropias.Map.HashMapSimple;
import com.myapp.ClasesPropias.Set.SetPropio;
import com.myapp.ClasesPropias.Trie.TrieAutocompletado;
import com.myapp.model.Cancion;
import com.myapp.model.Usuario;
import com.myapp.model.enums.Genero;
import com.myapp.model.enums.Role;
import com.myapp.model.grafos.GrafoSocial;

@SpringBootTest
class SyncUpTest {

	// ==================== TEST 1: ListaEnlazada ====================
    
    private ListaEnlazada<String> lista;

    @BeforeEach
    void setUp() {
        lista = new ListaEnlazada<>();
    }

    @Test
    @DisplayName("Test 1: ListaEnlazada - agregar y obtener elementos")
    void testListaEnlazadaAgregarYObtener() {
        lista.agregar("Primero");
        lista.agregar("Segundo");
        lista.agregar("Tercero");

        assertEquals(3, lista.tamaño());
        assertEquals("Primero", lista.obtener(0));
        assertEquals("Segundo", lista.obtener(1));
        assertEquals("Tercero", lista.obtener(2));
    }

    @Test
    @DisplayName("Test 2: ListaEnlazada - eliminar elementos")
    void testListaEnlazadaEliminar() {
        lista.agregar("A");
        lista.agregar("B");
        lista.agregar("C");

        boolean eliminado = lista.eliminar("B");

        assertTrue(eliminado);
        assertEquals(2, lista.tamaño());
        assertFalse(lista.contiene("B"));
        assertTrue(lista.contiene("A"));
        assertTrue(lista.contiene("C"));
    }

    @Test
    @DisplayName("Test 3: ListaEnlazada - contiene elemento")
    void testListaEnlazadaContiene() {
        lista.agregar("Elemento1");
        lista.agregar("Elemento2");

        assertTrue(lista.contiene("Elemento1"));
        assertTrue(lista.contiene("Elemento2"));
        assertFalse(lista.contiene("NoExiste"));
    }

    // ==================== TEST 4: HashMapSimple ====================

    @Test
    @DisplayName("Test 4: HashMapSimple - operaciones básicas put/get")
    void testHashMapSimpleOperaciones() {
        HashMapSimple<String, Integer> mapa = new HashMapSimple<>();

        mapa.put("uno", 1);
        mapa.put("dos", 2);
        mapa.put("tres", 3);

        assertEquals(3, mapa.size());
        assertEquals(1, mapa.get("uno"));
        assertEquals(2, mapa.get("dos"));
        assertEquals(3, mapa.get("tres"));
        assertNull(mapa.get("cuatro"));
        assertTrue(mapa.containsKey("uno"));
        assertFalse(mapa.containsKey("cuatro"));
    }

    // ==================== TEST 5: TrieAutocompletado ====================

    @Test
    @DisplayName("Test 5: TrieAutocompletado - insertar y autocompletar")
    void testTrieAutocompletado() {
        TrieAutocompletado trie = new TrieAutocompletado();
        trie.insertar("Bohemian Rhapsody");
        trie.insertar("Born to Run");
        trie.insertar("Back in Black");
        trie.insertar("Billie Jean");

        ListaEnlazada<String> resultadosB = trie.autocompletar("b", 10);
        ListaEnlazada<String> resultadosBo = trie.autocompletar("bo", 10);

        assertEquals(4, resultadosB.tamaño());
        assertEquals(2, resultadosBo.tamaño());
        assertTrue(trie.contienePalabra("bohemian rhapsody"));
        assertFalse(trie.contienePalabra("highway to hell"));
    }

    // ==================== TEST 6: SetPropio ====================

    @Test
    @DisplayName("Test 6: SetPropio - agregar sin duplicados")
    void testSetPropioSinDuplicados() {
        SetPropio<String> set = new SetPropio<>();

        set.agregar("A");
        set.agregar("B");
        set.agregar("A"); 
        set.agregar("C");
        set.agregar("B"); 
        assertEquals(3, set.tamaño());
        assertTrue(set.contiene("A"));
        assertTrue(set.contiene("B"));
        assertTrue(set.contiene("C"));
    }

    // ==================== TEST 7: GrafoSocial ====================

    @Test
    @DisplayName("Test 7: GrafoSocial - conexiones y sugerencias BFS")
    void testGrafoSocialConexiones() {
        GrafoSocial grafo = new GrafoSocial();
        grafo.agregarUsuario("juan");
        grafo.agregarUsuario("maria");
        grafo.agregarUsuario("pedro");
        grafo.agregarUsuario("ana");

        grafo.conectar("juan", "maria");
        grafo.conectar("maria", "pedro");
        grafo.conectar("pedro", "ana");

        SetPropio<String> seguidosJuan = grafo.seguidos("juan");
        assertTrue(seguidosJuan.contiene("maria"));
        assertEquals(1, seguidosJuan.tamaño());

        SetPropio<String> seguidoresMaria = grafo.seguidores("maria");
        assertTrue(seguidoresMaria.contiene("juan"));

        ListaEnlazada<String> sugerencias = grafo.sugerir("juan", 5);
        assertTrue(sugerencias.contiene("pedro"));
    }

    // ==================== TEST 8: Modelo Usuario ====================

    @Test
    @DisplayName("Test 8: Usuario - creación y favoritos")
    void testUsuarioCreacionYFavoritos() {
        Usuario usuario = new Usuario("testuser", "password123", "Test User", Role.USER);
        Cancion cancion1 = new Cancion();
        cancion1.setId(1L);
        cancion1.setTitulo("Test Song");
        cancion1.setArtista("Test Artist");
        cancion1.setGenero(Genero.ROCK);

        usuario.getCancionesFavoritas().agregar(cancion1);

        assertEquals("testuser", usuario.getUser());
        assertEquals("Test User", usuario.getNombre());
        assertEquals(Role.USER, usuario.getRole());
        assertEquals(1, usuario.getCancionesFavoritas().tamaño());
        assertTrue(usuario.getCancionesFavoritas().contiene(cancion1));
    }

    // ==================== TEST 9: Modelo Cancion ====================

    @Test
    @DisplayName("Test 9: Cancion - equals y hashCode basado en id")
    void testCancionEqualsHashCode() {
        Cancion cancion1 = new Cancion();
        cancion1.setId(1L);
        cancion1.setTitulo("Song A");

        Cancion cancion2 = new Cancion();
        cancion2.setId(1L);
        cancion2.setTitulo("Song B");

        Cancion cancion3 = new Cancion();
        cancion3.setId(2L);
        cancion3.setTitulo("Song A");

        // Assert
        assertEquals(cancion1, cancion2);
        assertNotEquals(cancion1, cancion3); 
        assertEquals(cancion1.hashCode(), cancion2.hashCode());
    }

    // ==================== TEST 10: GrafoSocial - Desconectar ====================

    @Test
    @DisplayName("Test 10: GrafoSocial - desconectar usuarios")
    void testGrafoSocialDesconectar() {
        GrafoSocial grafo = new GrafoSocial();
        grafo.agregarUsuario("user1");
        grafo.agregarUsuario("user2");
        grafo.conectar("user1", "user2");

        assertTrue(grafo.seguidos("user1").contiene("user2"));

        grafo.desconectar("user1", "user2");

        assertFalse(grafo.seguidos("user1").contiene("user2"));
        assertFalse(grafo.seguidores("user2").contiene("user1"));
    }

}
