import React from "react";

export default function UserDashboard({ currentView, user }) {
  const renderContent = () => {
    switch (currentView) {
      case "inicio":
        return <InicioView user={user} />;
      case "explorar":
        return <ExplorarView />;
      case "favoritos":
        return <FavoritosView user={user} />;
      case "descubrimiento":
        return <DescubrimientoView user={user} />;
      case "radio":
        return <RadioView />;
      case "social":
        return <SocialView user={user} />;
      default:
        return <InicioView user={user} />;
    }
  };

  return <div className="dashboard-container">{renderContent()}</div>;
}

function InicioView({ user }) {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Bienvenido, {user.nombre} 👋</h1>
        <p>Descubre tu música favorita y conecta con otros usuarios</p>
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <div className="card-icon">🎵</div>
          <h3>Explorar Música</h3>
          <p>Descubre nuevas canciones y artistas</p>
          <button className="card-button">Explorar</button>
        </div>

        <div className="dashboard-card">
          <div className="card-icon">❤️</div>
          <h3>Mis Favoritos</h3>
          <p>Accede a tus canciones guardadas</p>
          <button className="card-button">Ver Favoritos</button>
        </div>

        <div className="dashboard-card">
          <div className="card-icon">✨</div>
          <h3>Descubrimiento Semanal</h3>
          <p>Recomendaciones personalizadas</p>
          <button className="card-button">Ver Playlist</button>
        </div>

        <div className="dashboard-card">
          <div className="card-icon">👥</div>
          <h3>Social</h3>
          <p>Conecta con otros usuarios</p>
          <button className="card-button">Ver Amigos</button>
        </div>
      </div>

      <div className="recent-section">
        <h2>Reproducido Recientemente</h2>
        <div className="empty-state">
          <span className="empty-icon">🎧</span>
          <p>Aún no has reproducido ninguna canción</p>
          <button className="btn-secondary">Explorar Música</button>
        </div>
      </div>
    </div>
  );
}

function ExplorarView() {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Explorar Música 🎵</h1>
        <p>Busca y descubre nuevas canciones</p>
      </div>

      <div className="search-section">
        <div className="search-box-large">
          <input 
            type="text" 
            placeholder="Buscar por título, artista, género..."
            className="search-input-large"
          />
          <button className="btn-search">Buscar</button>
        </div>
      </div>

      <div className="empty-state">
        <span className="empty-icon">🔍</span>
        <p>Realiza una búsqueda para ver resultados</p>
      </div>
    </div>
  );
}

function FavoritosView({ user }) {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Mis Favoritos ❤️</h1>
        <p>Tus canciones guardadas</p>
      </div>

      <div className="empty-state">
        <span className="empty-icon">💔</span>
        <p>Aún no tienes canciones favoritas</p>
        <button className="btn-secondary">Explorar Música</button>
      </div>
    </div>
  );
}

function DescubrimientoView({ user }) {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Descubrimiento Semanal ✨</h1>
        <p>Recomendaciones basadas en tus gustos</p>
      </div>

      <div className="empty-state">
        <span className="empty-icon">🎲</span>
        <p>Agrega canciones a favoritos para recibir recomendaciones personalizadas</p>
        <button className="btn-secondary">Explorar Música</button>
      </div>
    </div>
  );
}

function RadioView() {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Radio 📻</h1>
        <p>Genera una radio basada en una canción</p>
      </div>

      <div className="empty-state">
        <span className="empty-icon">📻</span>
        <p>Selecciona una canción para iniciar tu radio personalizada</p>
      </div>
    </div>
  );
}

function SocialView({ user }) {
  return (
    <div className="dashboard-view">
      <div className="view-header">
        <h1>Social 👥</h1>
        <p>Conecta con otros usuarios</p>
      </div>

      <div className="social-tabs">
        <button className="tab-button active">Seguidos</button>
        <button className="tab-button">Seguidores</button>
        <button className="tab-button">Sugerencias</button>
      </div>

      <div className="empty-state">
        <span className="empty-icon">🤝</span>
        <p>Aún no sigues a nadie</p>
        <button className="btn-secondary">Buscar Usuarios</button>
      </div>
    </div>
  );
}