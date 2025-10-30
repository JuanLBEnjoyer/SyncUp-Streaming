import React, { useState } from "react";
import API from "../services/api";
import { useNavigate, Link } from "react-router-dom";

export default function Register() {
  const [form, setForm] = useState({ user: "", password: "", nombre: "" });
  const nav = useNavigate();
  const [errors, setErrors] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await API.post("/auth/register", form);
      alert("Usuario registrado con éxito");
      nav("/login");
    } catch (err) {
      const erorrs = err.response?.data?.errors;
      if(erorrs) {
        setErrors(erorrs);
      } else {
        alert(err.response?.data || "Error al registrar usuario");
      }
    }
}

return (
    <div style={{ padding: "2rem" }}>
      <h2>Registro</h2>
      <form onSubmit={handleSubmit}>
        <input
          placeholder="Usuario"
          value={form.user}
          onChange={(e) => setForm({ ...form, user: e.target.value })}
        />
        {errors?.user && 
        <div 
          style={{ color: "red" }}>{errors.user}
        </div> }
        <br />
        <input
          placeholder="Nombre"
          value={form.nombre}
          onChange={(e) => setForm({ ...form, nombre: e.target.value })}
        />
        {errors?.nombre && 
        <div 
          style={{ color: "red" }}>{errors.nombre}
        </div> }
        <br />
        <input
          type="password"
          placeholder="Contraseña"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />
        {errors?.password && 
        <div 
          style={{ color: "red" }}>{errors.password}
        </div> }
        <br />
        <button type="submit">Registrarse</button>
      </form>
      <p>
        ¿Ya tienes cuenta? {" "}
        <Link to="/login">Inicia sesión aqui</Link>
      </p>
    </div>
  );
}

