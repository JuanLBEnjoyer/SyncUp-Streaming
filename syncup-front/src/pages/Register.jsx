import React, { useState } from "react";
import API from "../services/api";
import { useNavigate, Link } from "react-router-dom";

export default function Register() {
  const [form, setForm] = useState({ 
    user: "", 
    password: "", 
    nombre: "" 
  });
  const [errors, setErrors] = useState({});
  const [generalError, setGeneralError] = useState("");
  const [success, setSuccess] = useState(false);
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
    setSuccess(false);
    setLoading(true);

    try {
      await API.post("/auth/register", form);
      
      setSuccess(true);
      setTimeout(() => {
        nav("/login");
      }, 2000);
      
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
        {/* Logo y header */}
        <div className="auth-logo">
          <img 
            src="/logo-syncup.png" 
            alt="SyncUp Logo"
          />
          <h1>Únete a SyncUp</h1>
          <p>Crea tu cuenta y descubre música increíble</p>
        </div>
        {success && (
          <div className="alert alert-success">
            <span>✓</span>
            <span>¡Registro exitoso! Redirigiendo al login...</span>
          </div>
        )}
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
              placeholder="Elige un nombre de usuario"
              value={form.user}
              onChange={handleChange}
              disabled={loading || success}
            />
            {errors.user && (
              <span className="form-error">{errors.user}</span>
            )}
          </div>
          <div className={`form-group ${errors.nombre ? 'has-error' : ''}`}>
            <label htmlFor="nombre">Nombre completo</label>
            <input
              id="nombre"
              name="nombre"
              type="text"
              placeholder="Tu nombre completo"
              value={form.nombre}
              onChange={handleChange}
              disabled={loading || success}
            />
            {errors.nombre && (
              <span className="form-error">{errors.nombre}</span>
            )}
          </div>
          <div className={`form-group ${errors.password ? 'has-error' : ''}`}>
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              name="password"
              type="password"
              placeholder="Mínimo 8 caracteres"
              value={form.password}
              onChange={handleChange}
              disabled={loading || success}
            />
            {errors.password && (
              <span className="form-error">{errors.password}</span>
            )}
          </div>
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={loading || success}
          >
            {loading ? (
              <>
                <span className="spinner"></span>
                <span style={{ marginLeft: '0.5rem' }}>Registrando...</span>
              </>
            ) : success ? (
              '✓ Registro exitoso'
            ) : (
              'Crear cuenta'
            )}
          </button>
        </form>
        <div className="auth-footer">
          <p>
            ¿Ya tienes cuenta?{" "}
            <Link to="/login">Inicia sesión aquí</Link>
          </p>
        </div>
      </div>
    </div>
  );
}

