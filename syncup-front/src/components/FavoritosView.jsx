import React, { useState, useEffect } from "react";
import API from "../services/api";
import "../styles/Explorar.css";
import "../styles/Favoritos.css";

export default function FavoritosView({ user, setCurrentView }) {
  const [favoritos, setFavoritos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    cargarFavoritos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const cargarFavoritos = async () => {
    try {
      setLoading(true);
      setError("");
      const { data } = await API.get(`/usuarios/${user.user}/favoritos`);
      setFavoritos(data);
    } catch (err) {
      setError("Error al cargar tus favoritos");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const quitarFavorito = async (cancionId) => {
    try {
      await API.delete(`/usuarios/${user.user}/favoritos/${cancionId}`);
      setFavoritos(favoritos.filter(c => c.id !== cancionId));
    } catch (err) {
      console.error("Error al quitar de favoritos:", err);
      alert("Error al quitar de favoritos");
    }
  };

  const descargarCSV = async () => {
    try {
      const response = await API.get(`/usuarios/${user.user}/favoritos/csv`, {
        responseType: 'blob'
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `favoritos_${user.user}.csv`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Error al descargar CSV:", err);
      alert("Error al descargar el archivo CSV");
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
        <h1>Mis Favoritos ❤️</h1>
        <p>Tus canciones guardadas</p>
      </div>

      {favoritos.length > 0 && (
        <div className="favoritos-actions">
          <button className="btn-download" onClick={descargarCSV}>
            📥 Descargar CSV
          </button>
        </div>
      )}

      {error && (
        <div className="error-message">
          <p>{error}</p>
          <button className="btn-secondary" onClick={cargarFavoritos}>
            Reintentar
          </button>
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Cargando tus favoritos...</p>
        </div>
      ) : favoritos.length === 0 ? (
        <div className="empty-state">
          <span className="empty-icon">💔</span>
          <p>Aún no tienes canciones favoritas</p>
          <button 
            className="btn-secondary"
            onClick={() => setCurrentView("explorar")}
          >
            Explorar Música
          </button>
        </div>
      ) : (
        <div className="canciones-grid">
          {favoritos.map((cancion) => (
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
                  className="btn-favorito active"
                  onClick={() => quitarFavorito(cancion.id)}
                  title="Quitar de favoritos"
                >
                  ❤️
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {!loading && favoritos.length > 0 && (
        <div className="resultados-count">
          {favoritos.length} canción{favoritos.length !== 1 ? 'es' : ''} en favoritos
        </div>
      )}
    </div>
  );
}