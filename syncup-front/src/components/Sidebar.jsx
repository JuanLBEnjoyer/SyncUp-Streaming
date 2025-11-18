import React from "react";

export default function Sidebar({ isOpen, currentView, setCurrentView, isAdmin }) {
  const menuItemsUser = [
    { id: "inicio", label: "Inicio", icon: "🏠" },
    { id: "explorar", label: "Explorar", icon: "🎵" },
    { id: "favoritos", label: "Mis Favoritos", icon: "❤️" },
    { id: "descubrimiento", label: "Descubrimiento Semanal", icon: "✨" },
    { id: "radio", label: "Radio", icon: "📻" },
    { id: "social", label: "Social", icon: "👥" },
  ];

  const menuItemsAdmin = [
    { id: "dashboard", label: "Dashboard", icon: "📊" },
    { id: "canciones", label: "Gestionar Canciones", icon: "🎵" },
    { id: "usuarios", label: "Gestionar Usuarios", icon: "👥" },
    { id: "carga-masiva", label: "Carga Masiva", icon: "📤" },
  ];

  const menuItems = isAdmin ? menuItemsAdmin : menuItemsUser;

  return (
    <aside className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
      <div className="sidebar-header">
        <h3>{isAdmin ? "Panel Admin" : "Biblioteca"}</h3>
      </div>

      <nav className="sidebar-nav">
        {menuItems.map((item) => (
          <button
            key={item.id}
            className={`sidebar-item ${currentView === item.id ? 'active' : ''}`}
            onClick={() => setCurrentView(item.id)}
          >
            <span className="sidebar-icon">{item.icon}</span>
            {isOpen && <span className="sidebar-label">{item.label}</span>}
          </button>
        ))}
      </nav>

      {isOpen && (
        <div className="sidebar-footer">
          <div className="sidebar-info">
            <p className="sidebar-info-title">SyncUp Premium</p>
            <p className="sidebar-info-text">Disfruta sin límites</p>
            <button className="btn-premium">Mejorar Plan</button>
          </div>
        </div>
      )}
    </aside>
  );
}