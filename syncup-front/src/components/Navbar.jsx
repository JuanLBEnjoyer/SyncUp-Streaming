import React, { useState } from "react";

export default function Navbar({ user, onLogout, onToggleSidebar }) {
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  return (
    <nav className="navbar">
      <button className="sidebar-toggle" onClick={onToggleSidebar}>
        <span className="hamburger-icon">☰</span>
      </button>
      <div className="navbar-brand">
        <img 
          src="/logo-syncup.png" 
          alt="SyncUp" 
          className="navbar-logo"
        />
        <span className="navbar-title">SyncUp</span>
      </div>
      <div className="navbar-search">
        <input 
          type="text" 
          placeholder="Buscar canciones, artistas..." 
          className="search-input"
        />
        <span className="search-icon">🔍</span>
      </div>
      <div className="navbar-user">
        <div className="user-info" onClick={toggleDropdown}>
          <div className="user-avatar">
            {user.nombre.charAt(0).toUpperCase()}
          </div>
          <div className="user-details">
            <span className="user-name">{user.nombre}</span>
            <span className="user-role">{user.role}</span>
          </div>
          <span className="dropdown-arrow">{dropdownOpen ? '▲' : '▼'}</span>
        </div>
        {dropdownOpen && (
          <div className="user-dropdown">
            <div className="dropdown-header">
              <p className="dropdown-username">@{user.user}</p>
              <p className="dropdown-email">{user.nombre}</p>
            </div>
            <div className="dropdown-divider"></div>
            <button className="dropdown-item" onClick={() => alert('Perfil - Próximamente')}>
              <span>👤</span> Mi Perfil
            </button>
            <button className="dropdown-item" onClick={() => alert('Configuración - Próximamente')}>
              <span>⚙️</span> Configuración
            </button>
            <div className="dropdown-divider"></div>
            <button className="dropdown-item logout" onClick={onLogout}>
              <span>🚪</span> Cerrar Sesión
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}