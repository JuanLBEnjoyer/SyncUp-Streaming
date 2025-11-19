import React, { useState } from "react";
import API from "../services/api";
import "../styles/Perfil.css";

export default function PerfilUsuario({ user, setUser, onClose }) {
  const [form, setForm] = useState({
    nombre: user.nombre || "",
    password: "",
    confirmPassword: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
    setError("");
    setSuccess("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    if (!form.nombre.trim()) {
      setError("El nombre no puede estar vacío");
      return;
    }

    if (form.password && form.password.length < 8) {
      setError("La contraseña debe tener al menos 8 caracteres");
      return;
    }

    if (form.password && form.password !== form.confirmPassword) {
      setError("Las contraseñas no coinciden");
      return;
    }

    setLoading(true);

    try {
      const payload = {
        nombre: form.nombre
      };

      if (form.password) {
        payload.password = form.password;
      }

      const { data } = await API.put(`/usuarios/${user.user}/perfil`, payload);

      const updatedUser = {
        ...user,
        nombre: data.usuario.nombre
      };
      
      localStorage.setItem("user", JSON.stringify(updatedUser));
      setUser(updatedUser);

      setSuccess("Perfil actualizado correctamente");
      setForm({ ...form, password: "", confirmPassword: "" });

    } catch (err) {
      const errorMsg = err.response?.data?.message || "Error al actualizar el perfil";
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="perfil-overlay" onClick={onClose}>
      <div className="perfil-modal" onClick={(e) => e.stopPropagation()}>
        <div className="perfil-header">
          <h2>Mi Perfil</h2>
          <button className="btn-close" onClick={onClose}>✕</button>
        </div>

        <div className="perfil-avatar">
          <div className="avatar-large">
            {user.nombre?.charAt(0).toUpperCase() || "U"}
          </div>
          <p className="username">@{user.user}</p>
        </div>

        {error && (
          <div className="alert alert-error">
            <span>⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div className="alert alert-success">
            <span>✓</span>
            <span>{success}</span>
          </div>
        )}

        <form className="perfil-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nombre">Nombre</label>
            <input
              id="nombre"
              name="nombre"
              type="text"
              value={form.nombre}
              onChange={handleChange}
              disabled={loading}
              placeholder="Tu nombre"
            />
          </div>

          <div className="form-divider">
            <span>Cambiar contraseña (opcional)</span>
          </div>

          <div className="form-group">
            <label htmlFor="password">Nueva contraseña</label>
            <input
              id="password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              disabled={loading}
              placeholder="Mínimo 8 caracteres"
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmar contraseña</label>
            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              value={form.confirmPassword}
              onChange={handleChange}
              disabled={loading}
              placeholder="Repite la contraseña"
            />
          </div>

          <div className="perfil-actions">
            <button 
              type="button" 
              className="btn-secondary"
              onClick={onClose}
              disabled={loading}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              className="btn-primary"
              disabled={loading}
            >
              {loading ? "Guardando..." : "Guardar Cambios"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}