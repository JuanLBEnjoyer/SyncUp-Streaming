import React, { useState, useEffect } from "react";
import API from "../services/api";
import Modal from "./Modal";
import "../styles/UsuariosAdmin.css";

export default function UsuariosAdmin() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedRole, setSelectedRole] = useState("");
  const [deleteModal, setDeleteModal] = useState(false);
  const [usuarioToDelete, setUsuarioToDelete] = useState(null);

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const cargarUsuarios = async () => {
    try {
      setLoading(true);
      setError("");
      const { data } = await API.get("/admin/usuarios");
      setUsuarios(data.usuarios);
    } catch (err) {
      setError("Error al cargar los usuarios");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteClick = (usuario) => {
    if (usuario.role === "ADMIN") {
      alert("No se puede eliminar al administrador");
      return;
    }
    
    setUsuarioToDelete(usuario);
    setDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await API.delete(`/admin/usuarios/${usuarioToDelete.user}`);
      setUsuarios(usuarios.filter(u => u.user !== usuarioToDelete.user));
      setDeleteModal(false);
      setUsuarioToDelete(null);
    } catch (err) {
      const errorMsg = err.response?.data?.message || "Error al eliminar el usuario";
      alert(errorMsg);
      console.error(err);
    }
  };

  const usuariosFiltrados = usuarios.filter(usuario => {
    const matchSearch = usuario.user.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       usuario.nombre.toLowerCase().includes(searchTerm.toLowerCase());
    const matchRole = !selectedRole || usuario.role === selectedRole;
    return matchSearch && matchRole;
  });

  return (
    <div className="usuarios-admin-view">
      <div className="view-header">
        <div>
          <h1>Gestionar Usuarios 👥</h1>
          <p>Total: {usuarios.length} usuarios registrados</p>
        </div>
      </div>

      <div className="filters-section">
        <input 
          type="text" 
          placeholder="Buscar por usuario o nombre..."
          className="filter-input"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <select 
          className="filter-select"
          value={selectedRole}
          onChange={(e) => setSelectedRole(e.target.value)}
        >
          <option value="">Todos los roles</option>
          <option value="USER">Usuarios</option>
          <option value="ADMIN">Administradores</option>
        </select>
      </div>

      {error && (
        <div className="alert alert-error">
          <span>⚠️</span>
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Cargando usuarios...</p>
        </div>
      ) : usuariosFiltrados.length === 0 ? (
        <div className="empty-state">
          <span className="empty-icon">👤</span>
          <p>{searchTerm || selectedRole ? "No se encontraron usuarios" : "No hay usuarios registrados"}</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="usuarios-table">
            <thead>
              <tr>
                <th>Username</th>
                <th>Nombre</th>
                <th>Rol</th>
                <th>Favoritos</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {usuariosFiltrados.map((usuario) => (
                <tr key={usuario.user}>
                  <td>
                    <div className="user-avatar-cell">
                      <div className="user-avatar-small">
                        {usuario.nombre.charAt(0).toUpperCase()}
                      </div>
                      <span className="username">@{usuario.user}</span>
                    </div>
                  </td>
                  <td className="user-name">{usuario.nombre}</td>
                  <td>
                    <span className={`role-badge role-${usuario.role.toLowerCase()}`}>
                      {usuario.role === "ADMIN" ? "Administrador" : "Usuario"}
                    </span>
                  </td>
                  <td className="text-center">
                    <span className="favorites-count">0</span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      {usuario.role === "ADMIN" ? (
                        <button 
                          className="btn-icon btn-protected"
                          title="No se puede eliminar"
                          disabled
                        >
                          🔒
                        </button>
                      ) : (
                        <button 
                          className="btn-icon btn-delete"
                          onClick={() => handleDeleteClick(usuario)}
                          title="Eliminar usuario"
                        >
                          🗑️
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal
        isOpen={deleteModal}
        onClose={() => setDeleteModal(false)}
        title="Confirmar Eliminación"
        size="small"
      >
        <div className="delete-confirmation">
          <p>¿Estás seguro de que deseas eliminar este usuario?</p>
          <div className="delete-info">
            <strong>@{usuarioToDelete?.user}</strong>
            <span>{usuarioToDelete?.nombre}</span>
          </div>
          <p className="warning-text">
            ⚠️ Esta acción también eliminará sus canciones favoritas y conexiones sociales.
          </p>
          <div className="modal-actions">
            <button 
              className="btn-secondary" 
              onClick={() => setDeleteModal(false)}
            >
              Cancelar
            </button>
            <button 
              className="btn-danger" 
              onClick={handleDeleteConfirm}
            >
              Eliminar Usuario
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}