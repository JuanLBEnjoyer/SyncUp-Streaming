import React from "react";
import CancionesAdmin from "./CancionesAdmin";
import UsuariosAdmin from "./UsuarioAdmin";

export default function AdminDashboard({ currentView, user }) {
  const renderContent = () => {
    switch (currentView) {
      case "dashboard":
        return <DashboardView user={user} />;
      case "canciones":
        return <CancionesAdmin />;
      case "usuarios":
        return <UsuariosAdmin />;
      case "carga-masiva":
        return <CargaMasivaView />;
      default:
        return <DashboardView user={user} />;
    }
  };

  return <div className="dashboard-container admin">{renderContent()}</div>;
}

function DashboardView({ user }) {
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
            <p className="stat-number">0</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🎵</div>
          <div className="stat-content">
            <h3>Total Canciones</h3>
            <p className="stat-number">0</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">❤️</div>
          <div className="stat-content">
            <h3>Favoritos Totales</h3>
            <p className="stat-number">0</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📈</div>
          <div className="stat-content">
            <h3>Actividad</h3>
            <p className="stat-number">100%</p>
          </div>
        </div>
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-card admin-card">
          <div className="card-icon">🎵</div>
          <h3>Gestionar Canciones</h3>
          <p>Agregar, editar o eliminar canciones del catálogo</p>
          <button className="card-button">Ir a Canciones</button>
        </div>

        <div className="dashboard-card admin-card">
          <div className="card-icon">👥</div>
          <h3>Gestionar Usuarios</h3>
          <p>Administrar cuentas de usuarios</p>
          <button className="card-button">Ir a Usuarios</button>
        </div>

        <div className="dashboard-card admin-card">
          <div className="card-icon">📤</div>
          <h3>Carga Masiva</h3>
          <p>Importar canciones desde archivo</p>
          <button className="card-button">Cargar Archivo</button>
        </div>
      </div>
    </div>
  );
}

function CargaMasivaView() {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Carga Masiva 📤</h1>
        <p>Importar canciones desde archivo CSV o TXT</p>
      </div>

      <div className="upload-section">
        <div className="upload-card">
          <div className="upload-icon">📁</div>
          <h3>Arrastra un archivo aquí</h3>
          <p>o haz clic para seleccionar</p>
          <button className="btn-primary">Seleccionar Archivo</button>
          
          <div className="upload-info">
            <p><strong>Formato esperado:</strong></p>
            <code>titulo,artista,genero,año,duracion</code>
            <p><strong>Géneros válidos:</strong> ROCK, POP, ELECTRONICA, HIPHOP, SALSA, REGGAETON</p>
          </div>
        </div>
      </div>
    </div>
  );
}
