import React, { useState, useEffect } from "react";
import API from "../services/api";
import "../styles/Explorar.css";
import "../styles/Radio.css";

export default function RadioView({ user, setCurrentView }) {
  const [canciones, setCanciones] = useState([]);
  const [colaRadio, setColaRadio] = useState([]);
  const [cancionBase, setCancionBase] = useState(null);
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingRadio, setLoadingRadio] = useState(false);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [sugerencias, setSugerencias] = useState([]);
  const [mostrarSugerencias, setMostrarSugerencias] = useState(false);

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        setLoading(true);
        const [cancionesRes, favoritosRes] = await Promise.all([
          API.get("/canciones"),
          API.get(`/usuarios/${user.user}/favoritos`)
        ]);
        setCanciones(cancionesRes.data);
        setFavoritos(favoritosRes.data.map(f => f.id));
      } catch (err) {
        setError("Error al cargar las canciones");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    cargarDatos();
  }, [user.user]);

  const generarRadio = async (cancion) => {
    try {
      setLoadingRadio(true);
      setError("");
      setCancionBase(cancion);
      
      const { data } = await API.get(`/canciones/${cancion.id}/radio?max=15`);
      setColaRadio(data);
    } catch (err) {
      setError("Error al generar la radio");
      console.error(err);
    } finally {
      setLoadingRadio(false);
    }
  };

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
    const cancion = canciones.find(c => 
      c.titulo.toLowerCase() === sugerencia.toLowerCase()
    );
    if (cancion) {
      generarRadio(cancion);
    }
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
    }
  };

  const limpiarRadio = () => {
    setColaRadio([]);
    setCancionBase(null);
    setSearchTerm("");
  };

  const formatDuracion = (segundos) => {
    const min = Math.floor(segundos / 60);
    const seg = Math.floor(segundos % 60);
    return `${min}:${seg.toString().padStart(2, '0')}`;
  };

  const duracionTotal = () => {
    const totalSegundos = colaRadio.reduce((acc, c) => acc + c.duracion, 0);
    const horas = Math.floor(totalSegundos / 3600);
    const minutos = Math.floor((totalSegundos % 3600) / 60);
    if (horas > 0) {
      return `${horas}h ${minutos}min`;
    }
    return `${minutos} min`;
  };

  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Radio 📻</h1>
        <p>Genera una radio basada en una canción</p>
      </div>
      <div className="radio-search-section">
        <h3>Selecciona una canción para iniciar tu radio</h3>
        <div className="search-container">
          <div className="search-input-wrapper">
            <input
              type="text"
              placeholder="Buscar canción..."
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
        </div>
        {!cancionBase && !loading && (
          <div className="canciones-seleccion">
            <p className="seleccion-hint">O selecciona de tus favoritos:</p>
            <div className="canciones-mini-grid">
              {canciones
                .filter(c => favoritos.includes(c.id))
                .slice(0, 6)
                .map((cancion) => (
                  <div 
                    key={cancion.id} 
                    className="cancion-mini-card"
                    onClick={() => generarRadio(cancion)}
                  >
                    <span className="mini-titulo">{cancion.titulo}</span>
                    <span className="mini-artista">{cancion.artista}</span>
                  </div>
                ))}
            </div>
            {favoritos.length === 0 && (
              <p className="no-favoritos-hint">
                No tienes favoritos aún. 
                <button 
                  className="link-button"
                  onClick={() => setCurrentView("explorar")}
                >
                  Explora música
                </button>
              </p>
            )}
          </div>
        )}
      </div>

      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Cargando canciones...</p>
        </div>
      ) : loadingRadio ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Generando tu radio personalizada...</p>
        </div>
      ) : colaRadio.length > 0 && (
        <>
          <div className="radio-info">
            <div className="radio-base">
              <span className="radio-label">Radio basada en:</span>
              <span className="radio-cancion">{cancionBase?.titulo} - {cancionBase?.artista}</span>
            </div>
            <div className="radio-stats">
              <span className="stat-item">🎵 {colaRadio.length} canciones</span>
              <span className="stat-item">⏱️ {duracionTotal()}</span>
            </div>
            <button className="btn-limpiar" onClick={limpiarRadio}>
              ✕ Nueva Radio
            </button>
          </div>
          <div className="playlist-container">
            {colaRadio.map((cancion, index) => (
              <div 
                key={cancion.id} 
                className={`playlist-item ${index === 0 ? 'now-playing' : ''}`}
              >
                <div className="playlist-number">
                  {index === 0 ? '▶' : index + 1}
                </div>
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
        </>
      )}
      {!loading && !loadingRadio && colaRadio.length === 0 && !cancionBase && favoritos.length === 0 && (
        <div className="empty-state">
          <span className="empty-icon">📻</span>
          <p>Busca una canción arriba para iniciar tu radio personalizada</p>
        </div>
      )}
    </div>
  );
}