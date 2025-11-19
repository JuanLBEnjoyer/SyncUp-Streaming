import React from "react";
import ExplorarView from "./ExplorarView";
import FavoritosView from "./FavoritosView";
import DescubrimientoView from "./DescubrimientoView";
import RadioView from "./RadioView";
import SocialView from "./SocialView";

export default function UserDashboard({ currentView, setCurrentView, user }) {
  const renderContent = () => {
    switch (currentView) {
      case "inicio":
        return <InicioView user={user} setCurrentView={setCurrentView} />;
      case "explorar":
        return <ExplorarView user={user} />;
      case "favoritos":
        return <FavoritosView user={user} setCurrentView={setCurrentView} />;
      case "descubrimiento":
        return <DescubrimientoView user={user} setCurrentView={setCurrentView} />;
      case "radio":
        return <RadioView user={user} setCurrentView={setCurrentView} />;
      case "social":
        return <SocialView user={user} />;
      default:
        return <InicioView user={user} setCurrentView={setCurrentView} />;
    }
  };

  return <div className="dashboard-container">{renderContent()}</div>;
}

function InicioView({ user, setCurrentView }) {
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
          <button 
            className="card-button"
            onClick={() => setCurrentView("explorar")}
          >
            Explorar
          </button>
        </div>

        <div className="dashboard-card">
          <div className="card-icon">❤️</div>
          <h3>Mis Favoritos</h3>
          <p>Accede a tus canciones guardadas</p>
          <button 
            className="card-button"
            onClick={() => setCurrentView("favoritos")}
          >
            Ver Favoritos
          </button>
        </div>

        <div className="dashboard-card">
          <div className="card-icon">✨</div>
          <h3>Descubrimiento Semanal</h3>
          <p>Recomendaciones personalizadas</p>
          <button 
            className="card-button"
            onClick={() => setCurrentView("descubrimiento")}
          >
            Ver Playlist
          </button>
        </div>

        <div className="dashboard-card">
          <div className="card-icon">👥</div>
          <h3>Social</h3>
          <p>Conecta con otros usuarios</p>
          <button 
            className="card-button"
            onClick={() => setCurrentView("social")}
          >
            Ver Amigos
          </button>
        </div>
      </div>

      <div className="recent-section">
        <h2>Reproducido Recientemente</h2>
        <div className="empty-state">
          <span className="empty-icon">🎧</span>
          <p>Aún no has reproducido ninguna canción</p>
          <button 
            className="btn-secondary"
            onClick={() => setCurrentView("explorar")}
          >
            Explorar Música
          </button>
        </div>
      </div>
    </div>
  );
}