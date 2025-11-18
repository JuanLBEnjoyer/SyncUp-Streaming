import React, { useState, useEffect } from "react";
import API from "../services/api";
import CancionesAdmin from "./CancionesAdmin";
import UsuarioAdmin from "./UsuarioAdmin";
import CargaMasivaAdmin from "./CargaMasivaAdmin";

export default function AdminDashboard({ currentView, setCurrentView, user }) {
  const renderContent = () => {
    switch (currentView) {
      case "dashboard":
        return <DashboardView user={user} setCurrentView={setCurrentView} />;
      case "canciones":
        return <CancionesAdmin />;
      case "usuarios":
        return <UsuarioAdmin />;
      case "carga-masiva":
        return <CargaMasivaAdmin />;
      default:
        return <DashboardView user={user} setCurrentView={setCurrentView} />;
    }
  };
  return <div className="dashboard-container admin">{renderContent()}</div>;
}

function DashboardView({ user, setCurrentView }) {
  const [stats, setStats] = useState({
    totalUsuarios: 0,
    totalCanciones: 0,
    favoritosTotales: 0,
    actividad: 100
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarEstadisticas();
  }, []);

  const cargarEstadisticas = async () => {
    try {
      setLoading(true);
      
      const { data: infoData } = await API.get("/admin/info");
      
      const { data: usuariosData } = await API.get("/admin/usuarios");
      
      let favoritosTotales = 0;
      if (usuariosData.usuarios) {
        usuariosData.usuarios.forEach(usuario => {
          favoritosTotales += usuario.totalFavoritos || 0;
        });
      }

      setStats({
        totalUsuarios: infoData.totalUsuarios || 0,
        totalCanciones: infoData.totalCanciones || 0,
        favoritosTotales: favoritosTotales,
        actividad: 100
      });
    } catch (error) {
      console.error("Error al cargar estadísticas:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard-view">
      <div className="view-header admin-header">
        <h1>Panel de Administración</h1>
        <p>Bienvenido, {user.nombre}</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">👥</div>
          <div className="stat-content">
            <h3>Total Usuarios</h3>
            <p className="stat-number">
              {loading ? "..." : stats.totalUsuarios}
            </p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🎵</div>
          <div className="stat-content">
            <h3>Total Canciones</h3>
            <p className="stat-number">
              {loading ? "..." : stats.totalCanciones}
            </p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">❤️</div>
          <div className="stat-content">
            <h3>Favoritos Totales</h3>
            <p className="stat-number">
              {loading ? "..." : stats.favoritosTotales}
            </p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📈</div>
          <div className="stat-content">
            <h3>Actividad</h3>
            <p className="stat-number">{stats.actividad}%</p>
          </div>
        </div>
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-card admin-card">
          <div className="card-icon">🎵</div>
          <h3>Gestionar Canciones</h3>
          <p>Agregar, editar o eliminar canciones del catálogo</p>
          <button 
            className="card-button"
            onClick={() => setCurrentView("canciones")}
          >
            Ir a Canciones
          </button>
        </div>

        <div className="dashboard-card admin-card">
          <div className="card-icon">👥</div>
          <h3>Gestionar Usuarios</h3>
          <p>Administrar cuentas de usuarios</p>
          <button 
            className="card-button"
            onClick={() => setCurrentView("usuarios")}
          >
            Ir a Usuarios
          </button>
        </div>

        <div className="dashboard-card admin-card">
          <div className="card-icon">📤</div>
          <h3>Carga Masiva</h3>
          <p>Importar canciones desde archivo</p>
          <button 
            className="card-button"
            onClick={() => setCurrentView("carga-masiva")}
          >
            Cargar Archivo
          </button>
        </div>
      </div>
    </div>
  );
}