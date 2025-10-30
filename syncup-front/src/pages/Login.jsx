import React, { useState } from "react";
import API from "../services/api";
import { useNavigate } from "react-router-dom";

export default function Login({ setUser }) {
  const [form, setForm] = useState({ user: "", password: "" });
  const nav = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const { data } = await API.post("/auth/login", form);
      localStorage.setItem("user", JSON.stringify(data)); // guarda usuario en navegador
      setUser(data);
      alert("Inicio de sesión exitoso");
      nav("/");
    } catch (err) {
      alert(err.response?.data || "Usuario o contraseña incorrectos");
    }
  };

  return (
    <div style={{ padding: "2rem" }}>
      <h2>Iniciar sesión</h2>
      <form onSubmit={handleSubmit}>
        <input
          placeholder="Usuario"
          value={form.user}
          onChange={(e) => setForm({ ...form, user: e.target.value })}
        />
        <br />
        <input
          type="password"
          placeholder="Contraseña"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />
        <br />
        <button type="submit">Ingresar</button>
      </form>
    </div>
  );
}
