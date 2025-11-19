import React, { useState, useEffect } from "react";
import API from "../services/api";
import "../styles/Social.css";

export default function SocialView({ user }) {
  const [activeTab, setActiveTab] = useState("seguidos");
  const [seguidos, setSeguidos] = useState([]);
  const [seguidores, setSeguidores] = useState([]);
  const [sugerencias, setSugerencias] = useState([]);
  const [todosUsuarios, setTodosUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const cargarDatos = async () => {
      try {
        setLoading(true);
        setError("");

        const [seguidosRes, seguidoresRes, sugerenciasRes, todosRes] = await Promise.all([
          API.get(`/usuarios/${user.user}/seguidos`),
          API.get(`/usuarios/${user.user}/seguidores`),
          API.get(`/usuarios/${user.user}/sugerencias?limite=10`),
          API.get("/admin/usuarios")
        ]);

        setSeguidos(seguidosRes.data.seguidos || []);
        setSeguidores(seguidoresRes.data.seguidores || []);
        setSugerencias(sugerenciasRes.data.sugerencias || []);
        setTodosUsuarios(
          (todosRes.data.usuarios || []).filter(u => 
            u.user !== user.user && u.role !== "ADMIN"
          )
        );
      } catch (err) {
        setError("Error al cargar los datos sociales");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    cargarDatos();
  }, [user.user]);

  const recargarDatos = async () => {
    try {
      setLoading(true);
      setError("");

      const [seguidosRes, seguidoresRes, sugerenciasRes, todosRes] = await Promise.all([
        API.get(`/usuarios/${user.user}/seguidos`),
        API.get(`/usuarios/${user.user}/seguidores`),
        API.get(`/usuarios/${user.user}/sugerencias?limite=10`),
        API.get("/admin/usuarios")
      ]);

      setSeguidos(seguidosRes.data.seguidos || []);
      setSeguidores(seguidoresRes.data.seguidores || []);
      setSugerencias(sugerenciasRes.data.sugerencias || []);
      setTodosUsuarios(
        (todosRes.data.usuarios || []).filter(u => 
          u.user !== user.user && u.role !== "ADMIN"
        )
      );
    } catch (err) {
      setError("Error al cargar los datos sociales");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const seguirUsuario = async (username) => {
    try {
      await API.post(`/usuarios/${user.user}/seguir/${username}`);
      await recargarDatos();
    } catch (err) {
      console.error("Error al seguir usuario:", err);
      alert(err.response?.data?.message || "Error al seguir usuario");
    }
  };

  const dejarDeSeguir = async (username) => {
    try {
      await API.delete(`/usuarios/${user.user}/seguir/${username}`);
      await recargarDatos();
    } catch (err) {
      console.error("Error al dejar de seguir:", err);
      alert(err.response?.data?.message || "Error al dejar de seguir");
    }
  };

  const yaSigo = (username) => {
    return seguidos.some(s => s.user === username);
  };

  const usuariosFiltrados = todosUsuarios.filter(u =>
    u.user.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.nombre.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const renderUsuarioCard = (usuario, showFollowButton = true) => (
    <div key={usuario.user} className="usuario-card">
      <div className="usuario-avatar">
        {usuario.nombre.charAt(0).toUpperCase()}
      </div>
      <div className="usuario-info">
        <h4 className="usuario-nombre">{usuario.nombre}</h4>
        <p className="usuario-username">@{usuario.user}</p>
      </div>
      {showFollowButton && (
        <div className="usuario-actions">
          {yaSigo(usuario.user) ? (
            <button
              className="btn-unfollow"
              onClick={() => dejarDeSeguir(usuario.user)}
            >
              Siguiendo
            </button>
          ) : (
            <button
              className="btn-follow"
              onClick={() => seguirUsuario(usuario.user)}
            >
              Seguir
            </button>
          )}
        </div>
      )}
    </div>
  );

  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Social 👥</h1>
        <p>Conecta con otros usuarios</p>
      </div>

      <div className="social-stats">
        <div className="stat-box">
          <span className="stat-number">{seguidos.length}</span>
          <span className="stat-label">Siguiendo</span>
        </div>
        <div className="stat-box">
          <span className="stat-number">{seguidores.length}</span>
          <span className="stat-label">Seguidores</span>
        </div>
      </div>

      <div className="social-tabs">
        <button
          className={`tab-button ${activeTab === "seguidos" ? "active" : ""}`}
          onClick={() => setActiveTab("seguidos")}
        >
          Siguiendo ({seguidos.length})
        </button>
        <button
          className={`tab-button ${activeTab === "seguidores" ? "active" : ""}`}
          onClick={() => setActiveTab("seguidores")}
        >
          Seguidores ({seguidores.length})
        </button>
        <button
          className={`tab-button ${activeTab === "sugerencias" ? "active" : ""}`}
          onClick={() => setActiveTab("sugerencias")}
        >
          Sugerencias
        </button>
        <button
          className={`tab-button ${activeTab === "buscar" ? "active" : ""}`}
          onClick={() => setActiveTab("buscar")}
        >
          Buscar
        </button>
      </div>

      {error && (
        <div className="error-message">
          <p>{error}</p>
          <button className="btn-secondary" onClick={recargarDatos}>
            Reintentar
          </button>
        </div>
      )}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Cargando...</p>
        </div>
      ) : (
        <div className="social-content">
          {activeTab === "seguidos" && (
            <div className="usuarios-list">
              {seguidos.length === 0 ? (
                <div className="empty-state">
                  <span className="empty-icon">🤝</span>
                  <p>Aún no sigues a nadie</p>
                  <button
                    className="btn-secondary"
                    onClick={() => setActiveTab("buscar")}
                  >
                    Buscar Usuarios
                  </button>
                </div>
              ) : (
                seguidos.map(usuario => renderUsuarioCard(usuario, true))
              )}
            </div>
          )}

          {activeTab === "seguidores" && (
            <div className="usuarios-list">
              {seguidores.length === 0 ? (
                <div className="empty-state">
                  <span className="empty-icon">👤</span>
                  <p>Aún no tienes seguidores</p>
                </div>
              ) : (
                seguidores.map(usuario => renderUsuarioCard(usuario, true))
              )}
            </div>
          )}

          {activeTab === "sugerencias" && (
            <div className="usuarios-list">
              {sugerencias.length === 0 ? (
                <div className="empty-state">
                  <span className="empty-icon">✨</span>
                  <p>No hay sugerencias disponibles</p>
                  <p className="hint-text">Sigue a más usuarios para obtener sugerencias</p>
                </div>
              ) : (
                <>
                  <p className="sugerencias-hint">
                    Usuarios que podrías conocer (amigos de tus amigos)
                  </p>
                  {sugerencias.map(usuario => renderUsuarioCard(usuario, true))}
                </>
              )}
            </div>
          )}

          {activeTab === "buscar" && (
            <div className="buscar-section">
              <div className="search-box">
                <input
                  type="text"
                  placeholder="Buscar por nombre o usuario..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="search-input"
                />
              </div>
              <div className="usuarios-list">
                {usuariosFiltrados.length === 0 ? (
                  <div className="empty-state">
                    <span className="empty-icon">🔍</span>
                    <p>No se encontraron usuarios</p>
                  </div>
                ) : (
                  usuariosFiltrados.map(usuario => renderUsuarioCard(usuario, true))
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}