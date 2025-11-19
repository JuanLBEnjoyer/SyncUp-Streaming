# SyncUp-Streaming

Repositorio para trabajar proyecto de estructura de datos

## Colaboradores

- Juan Pablo Galeano Correa
- Samuel Saith Calle Santa
- Juan David Castañeda Valenzuela

## Descripción

SyncUp es una plataforma de streaming y descubrimiento social de música. Permite a los usuarios gestionar su perfil, buscar canciones, recibir recomendaciones inteligentes y conectar con otros usuarios según sus gustos musicales.

## Funcionalidades

### Usuario
- Registro e inicio de sesión
- Gestión de perfil y lista de favoritos
- Búsqueda por autocompletado (Trie)
- Búsqueda avanzada con lógica AND/OR
- Descubrimiento Semanal personalizado
- Radio basada en canciones similares
- Red social (seguir/dejar de seguir)
- Sugerencias de usuarios (BFS)
- Exportar favoritos a CSV

### Administrador
- Gestionar catálogo de canciones (CRUD)
- Gestionar usuarios
- Carga masiva desde archivo CSV

## Herramientas

- **Backend:** Java 17, Spring Boot, Maven
- **Frontend:** React 18, Axios
- **Estructuras propias:** ListaEnlazada, HashMapSimple, SetPropio, Trie, GrafoSocial, GrafoDeSimilitud
