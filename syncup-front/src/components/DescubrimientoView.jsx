import React, { useState, useEffect } from "react";
import API from "../services/api";
import "../styles/Explorar.css";
import "../styles/Descubrimiento.css";

export default function DescubrimientoView({ user, setCurrentView }) {
  const [recomendaciones, setRecomendaciones] = useState([]);
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        setLoading(true);
        setError("");

        const [recomendacionesRes, favoritosRes] = await Promise.all([
          API.get(`/usuarios/${user.user}/descubrimiento-semanal?max=20`),
          API.get(`/usuarios/${user.user}/favoritos`)
        ]);

        setRecomendaciones(recomendacionesRes.data);
        setFavoritos(favoritosRes.data.map(f => f.id));
      } catch (err) {
        setError("Error al cargar las recomendaciones");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    cargarDatos();
  }, [user.user]);

  const regenerarPlaylist = async () => {
    try {
      setLoading(true);
      setError("");

      const [recomendacionesRes, favoritosRes] = await Promise.all([
        API.get(`/usuarios/${user.user}/descubrimiento-semanal?max=20`),
        API.get(`/usuarios/${user.user}/favoritos`)
      ]);

      setRecomendaciones(recomendacionesRes.data);
      setFavoritos(favoritosRes.data.map(f => f.id));
    } catch (err) {
      setError("Error al cargar las recomendaciones");
      console.error(err);
    } finally {
      setLoading(false);
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
      alert("Error al modificar favorito");
    }
  };

  const formatDuracion = (segundos) => {
    const min = Math.floor(segundos / 60);
    const seg = Math.floor(segundos % 60);
    return `${min}:${seg.toString().padStart(2, '0')}`;
  };

  const duracionTotal = () => {
    const totalSegundos = recomendaciones.reduce((acc, c) => acc + c.duracion, 0);
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
        <h1>Descubrimiento Semanal ✨</h1>
        <p>Recomendaciones basadas en tus gustos</p>
      </div>

      {recomendaciones.length > 0 && (
        <div className="descubrimiento-info">
          <div className="playlist-stats">
            <span className="stat-item">
              🎵 {recomendaciones.length} canciones
            </span>
            <span className="stat-item">
              ⏱️ {duracionTotal()}
            </span>
          </div>
          <button className="btn-regenerar" onClick={regenerarPlaylist}>
            🔄 Regenerar Playlist
          </button>
        </div>
      )}

      {error && (
        <div className="error-message">
          <p>{error}</p>
          <button className="btn-secondary" onClick={regenerarPlaylist}>
            Reintentar
          </button>
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Generando tu playlist personalizada...</p>
        </div>
      ) : recomendaciones.length === 0 ? (
        <div className="empty-state">
          <span className="empty-icon">🎲</span>
          <p>Agrega canciones a favoritos para recibir recomendaciones personalizadas</p>
          <button 
            className="btn-secondary"
            onClick={() => setCurrentView("explorar")}
          >
            Explorar Música
          </button>
        </div>
      ) : (
        <div className="playlist-container">
          {recomendaciones.map((cancion, index) => (
            <div key={cancion.id} className="playlist-item">
              <div className="playlist-number">{index + 1}</div>
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
    </div>
  );
}