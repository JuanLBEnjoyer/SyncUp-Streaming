import React, { useState } from "react";
import API from "../services/api";
import { Link, useNavigate } from "react-router-dom";

export default function Login({ setUser }) {
  const [form, setForm] = useState({ user: "", password: "" });
  const [errors, setErrors] = useState({});
  const [generalError, setGeneralError] = useState("");
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
    
    if (errors[name]) {
      setErrors({ ...errors, [name]: "" });
    }
    if (generalError) {
      setGeneralError("");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setGeneralError("");
    setLoading(true);

    try {
      const { data } = await API.post("/auth/login", form);
      
      localStorage.setItem("user", JSON.stringify(data.usuario));
      setUser(data.usuario);
      
      nav("/");
      
    } catch (err) {
      setLoading(false);
      
      const errorData = err.response?.data;
      
      if (errorData?.errors) {
        setErrors(errorData.errors);
      } else if (errorData?.message) {
        setGeneralError(errorData.message);
      } else {
        setGeneralError("Error de conexión con el servidor");
      }
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-logo">
          <img 
            src="/logo-syncup.png" 
            alt="SyncUp Logo" 
          />
          <h1>Bienvenido a SyncUp</h1>
          <p>Inicia sesión para continuar</p>
        </div>

        {generalError && (
          <div className="alert alert-error">
            <span>⚠️</span>
            <span>{generalError}</span>
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className={`form-group ${errors.user ? 'has-error' : ''}`}>
            <label htmlFor="user">Usuario</label>
            <input
              id="user"
              name="user"
              type="text"
              placeholder="Ingresa tu usuario"
              value={form.user}
              onChange={handleChange}
              disabled={loading}
            />
            {errors.user && (
              <span className="form-error">{errors.user}</span>
            )}
          </div>
          <div className={`form-group ${errors.password ? 'has-error' : ''}`}>
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              name="password"
              type="password"
              placeholder="Ingresa tu contraseña"
              value={form.password}
              onChange={handleChange}
              disabled={loading}
            />
            {errors.password && (
              <span className="form-error">{errors.password}</span>
            )}
          </div>

          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="spinner"></span>
                <span style={{ marginLeft: '0.5rem' }}>Iniciando sesión...</span>
              </>
            ) : (
              'Iniciar sesión'
            )}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            ¿No tienes cuenta?{" "}
            <Link to="/register">Regístrate aquí</Link>
          </p>
        </div>
      </div>
    </div>
  );
}