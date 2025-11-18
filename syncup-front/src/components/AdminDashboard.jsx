import React from "react";

export default function AdminDashboard({ currentView, user }) {
  const renderContent = () => {
    switch (currentView) {
      case "dashboard":
        return <DashboardView user={user} />;
      case "canciones":
        return <CancionesView />;
      case "usuarios":
        return <UsuariosView />;
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
        <h1>Panel de Administración 📊</h1>
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

function CancionesView() {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Gestionar Canciones 🎵</h1>
        <button className="btn-primary">+ Agregar Canción</button>
      </div>

      <div className="filters-section">
        <input 
          type="text" 
          placeholder="Buscar canciones..."
          className="filter-input"
        />
        <select className="filter-select">
          <option>Todos los géneros</option>
          <option>ROCK</option>
          <option>POP</option>
          <option>ELECTRONICA</option>
          <option>HIPHOP</option>
          <option>SALSA</option>
          <option>REGGAETON</option>
        </select>
      </div>

      <div className="empty-state">
        <span className="empty-icon">🎵</span>
        <p>No hay canciones en el catálogo</p>
        <button className="btn-secondary">Agregar Primera Canción</button>
      </div>
    </div>
  );
}

function UsuariosView() {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Gestionar Usuarios 👥</h1>
        <p>Administrar cuentas de usuarios</p>
      </div>

      <div className="filters-section">
        <input 
          type="text" 
          placeholder="Buscar usuarios..."
          className="filter-input"
        />
        <select className="filter-select">
          <option>Todos los roles</option>
          <option>Usuarios</option>
          <option>Administradores</option>
        </select>
      </div>

      <div className="empty-state">
        <span className="empty-icon">👤</span>
        <p>No hay usuarios registrados</p>
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

function MetricasView() {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Métricas del Sistema 📈</h1>
        <p>Estadísticas y análisis</p>
      </div>

      <div className="metrics-grid">
        <div className="metric-card">
          <h3>Canciones por Género</h3>
          <div className="chart-placeholder">
            <span>📊</span>
            <p>Gráfico de pastel</p>
          </div>
        </div>

        <div className="metric-card">
          <h3>Artistas Populares</h3>
          <div className="chart-placeholder">
            <span>📊</span>
            <p>Gráfico de barras</p>
          </div>
        </div>

        <div className="metric-card">
          <h3>Canciones por Década</h3>
          <div className="chart-placeholder">
            <span>📊</span>
            <p>Línea de tiempo</p>
          </div>
        </div>

        <div className="metric-card">
          <h3>Géneros Favoritos</h3>
          <div className="chart-placeholder">
            <span>📊</span>
            <p>Ranking</p>
          </div>
        </div>
      </div>
    </div>
  );
}