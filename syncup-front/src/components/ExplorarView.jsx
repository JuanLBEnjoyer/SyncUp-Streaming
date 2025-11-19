import React, { useState, useEffect } from "react";
import API from "../services/api";
import "../styles/Explorar.css";

export default function ExplorarView({ user }) {
  const [canciones, setCanciones] = useState([]);
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [searchTerm, setSearchTerm] = useState("");
  const [sugerencias, setSugerencias] = useState([]);
  const [mostrarSugerencias, setMostrarSugerencias] = useState(false);
  const [todasLasCanciones, setTodasLasCanciones] = useState([]); 

  const [mostrarAvanzada, setMostrarAvanzada] = useState(false);
  const [filtros, setFiltros] = useState({
    artista: "",
    genero: "",
    año: "",
    modo: "AND"
  });

  const generos = ["", "ROCK", "POP", "ELECTRONICA", "HIPHOP", "SALSA", "REGGAETON"];

  const cargarDatos = async () => {
    try {
      setLoading(true);
      setError("");

      const [cancionesRes, favoritosRes] = await Promise.all([
        API.get("/canciones"),
        API.get(`/usuarios/${user.user}/favoritos`)
      ]);

      setCanciones(cancionesRes.data);
      setTodasLasCanciones(cancionesRes.data); 
      setFavoritos(favoritosRes.data.map(f => f.id));
    } catch (err) {
      setError("Error al cargar las canciones");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarDatos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSearchChange = async (e) => {
    const value = e.target.value;
    setSearchTerm(value);

    if (value.length >= 2) {
      try {
        const { data } = await API.get(`/canciones/autocompletar?prefijo=${value.toLowerCase()}&limite=5`);
        setSugerencias(data);
        setMostrarSugerencias(true);
      } catch (err) {
        console.error("Error en autocompletado:", err);
        setSugerencias([]);
      }
    } else {
      setSugerencias([]);
      setMostrarSugerencias(false);
    }
  };

  const seleccionarSugerencia = (sugerencia) => {
    setSearchTerm(sugerencia);
    setMostrarSugerencias(false);
    buscarPorTitulo(sugerencia);
  };

  const buscarPorTitulo = (titulo) => {
    if (!titulo.trim()) {
      setCanciones(todasLasCanciones);
      return;
    }
    const resultado = todasLasCanciones.filter(c => 
      c.titulo.toLowerCase().includes(titulo.toLowerCase())
    );
    setCanciones(resultado);
  };

  const verTodas = () => {
    setSearchTerm("");
    setSugerencias([]);
    setCanciones(todasLasCanciones);
  };

  const handleBusquedaAvanzada = async () => {
    try {
      setLoading(true);
      setError("");

      const params = new URLSearchParams();
      if (filtros.artista) params.append("artista", filtros.artista);
      if (filtros.genero) params.append("genero", filtros.genero);
      if (filtros.año) params.append("año", filtros.año);
      params.append("modo", filtros.modo);

      const { data } = await API.get(`/canciones/buscar?${params.toString()}`);
      setCanciones(data);
    } catch (err) {
      setError("Error en la búsqueda");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const limpiarFiltros = () => {
    setSearchTerm("");
    setFiltros({ artista: "", genero: "", año: "", modo: "AND" });
    setSugerencias([]);
    setCanciones(todasLasCanciones);
  };

  const toggleFavorito = async (cancionId) => {
    try {
      const esFavorito = favoritos.includes(cancionId);

      if (esFavorito) {
        await API.delete(`/usuarios/${user.user}/favoritos/${cancionId}`);
        setFavoritos(favoritos.filter(id => id !== cancionId));
      } else {
        await API.post(`/usuarios/${user.user}/favoritos/${cancionId}`);
        setFavoritos([...favoritos, cancionId]);
      }
    } catch (err) {
      console.error("Error al modificar favorito:", err);
      alert("Error al modificar favorito");
    }
  };

  const formatDuracion = (segundos) => {
    const min = Math.floor(segundos / 60);
    const seg = Math.floor(segundos % 60);
    return `${min}:${seg.toString().padStart(2, '0')}`;
  };

  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Explorar Música 🎵</h1>
        <p>Busca y descubre nuevas canciones</p>
      </div>

      <div className="search-section">
        <div className="search-container">
          <div className="search-input-wrapper">
            <input
              type="text"
              placeholder="Buscar por título..."
              value={searchTerm}
              onChange={handleSearchChange}
              onFocus={() => sugerencias.length > 0 && setMostrarSugerencias(true)}
              onBlur={() => setTimeout(() => setMostrarSugerencias(false), 200)}
              className="search-input-large"
            />
            {mostrarSugerencias && sugerencias.length > 0 && (
              <div className="sugerencias-dropdown">
                {sugerencias.map((sug, index) => (
                  <div
                    key={index}
                    className="sugerencia-item"
                    onClick={() => seleccionarSugerencia(sug)}
                  >
                    🎵 {sug}
                  </div>
                ))}
              </div>
            )}
          </div>
          <button className="btn-search" onClick={() => buscarPorTitulo(searchTerm)}>
            Buscar
          </button>
          {(searchTerm || canciones.length !== todasLasCanciones.length) && (
            <button className="btn-secondary" onClick={verTodas}>
              Ver Todas
            </button>
          )}
          <button 
            className={`btn-advanced ${mostrarAvanzada ? 'active' : ''}`}
            onClick={() => setMostrarAvanzada(!mostrarAvanzada)}
          >
            ⚙️ Avanzada
          </button>
        </div>

        {mostrarAvanzada && (
          <div className="busqueda-avanzada">
            <div className="filtros-grid">
              <div className="filtro-group">
                <label>Artista</label>
                <input
                  type="text"
                  placeholder="Nombre del artista"
                  value={filtros.artista}
                  onChange={(e) => setFiltros({ ...filtros, artista: e.target.value })}
                />
              </div>
              <div className="filtro-group">
                <label>Género</label>
                <select
                  value={filtros.genero}
                  onChange={(e) => setFiltros({ ...filtros, genero: e.target.value })}
                >
                  <option value="">Todos</option>
                  {generos.slice(1).map(g => (
                    <option key={g} value={g}>{g}</option>
                  ))}
                </select>
              </div>
              <div className="filtro-group">
                <label>Año</label>
                <input
                  type="number"
                  placeholder="Año"
                  value={filtros.año}
                  onChange={(e) => setFiltros({ ...filtros, año: e.target.value })}
                />
              </div>
              <div className="filtro-group">
                <label>Modo</label>
                <select
                  value={filtros.modo}
                  onChange={(e) => setFiltros({ ...filtros, modo: e.target.value })}
                >
                  <option value="AND">Todos los filtros (AND)</option>
                  <option value="OR">Cualquier filtro (OR)</option>
                </select>
              </div>
            </div>
            <div className="filtros-actions">
              <button className="btn-secondary" onClick={limpiarFiltros}>
                Limpiar
              </button>
              <button className="btn-primary" onClick={handleBusquedaAvanzada}>
                Aplicar Filtros
              </button>
            </div>
          </div>
        )}
      </div>

      {error && (
        <div className="alert alert-error">
          <span>⚠️</span>
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Cargando canciones...</p>
        </div>
      ) : canciones.length === 0 ? (
        <div className="empty-state">
          <span className="empty-icon">🔍</span>
          <p>No se encontraron canciones</p>
          <button className="btn-secondary" onClick={limpiarFiltros}>
            Ver todas las canciones
          </button>
        </div>
      ) : (
        <div className="canciones-grid">
          {canciones.map((cancion) => (
            <div key={cancion.id} className="cancion-card">
              <div className="cancion-info">
                <h3 className="cancion-titulo">{cancion.titulo}</h3>
                <p className="cancion-artista">{cancion.artista}</p>
                <div className="cancion-meta">
                  <span className="cancion-genero">{cancion.genero}</span>
                  <span className="cancion-año">{cancion.anio}</span>
                  <span className="cancion-duracion">{formatDuracion(cancion.duracion)}</span>
                </div>
              </div>
              <div className="cancion-actions">
                <button
                  className={`btn-favorito ${favoritos.includes(cancion.id) ? 'active' : ''}`}
                  onClick={() => toggleFavorito(cancion.id)}
                  title={favoritos.includes(cancion.id) ? "Quitar de favoritos" : "Agregar a favoritos"}
                >
                  {favoritos.includes(cancion.id) ? '❤️' : '🤍'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {!loading && canciones.length > 0 && (
        <div className="resultados-count">
          Mostrando {canciones.length} canción{canciones.length !== 1 ? 'es' : ''}
        </div>
      )}
    </div>
  );
}