import React, { useState } from "react";
import API from "../services/api";
import { Link, useNavigate } from "react-router-dom";


export default function Login({ setUser }) {
  const [form, setForm] = useState({ user: "", password: "" });
  const nav = useNavigate();
  const [errors, setErrors] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const { data } = await API.post("/auth/login", form);
      localStorage.setItem("user", JSON.stringify(data)); // guarda usuario en navegador
      setUser(data);
      alert("Inicio de sesión exitoso");
      nav("/");
    } catch (err) {
      const erorrs = err.response?.data?.errors;
      if(erorrs){
        setErrors(erorrs)
      }
      else{
        alert(err.response?.data || "Error al iniciar sesión");
      }
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
        {errors?.user && 
        <div 
          style={{ color: "red" }}>{errors.user}
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
        <button type="submit">Ingresar</button>
      </form>
      <p>
        ¿No tienes cuenta? {" "}
        <Link to="/register">Regístrate aqui</Link>
      </p>
    </div>
  );
}
