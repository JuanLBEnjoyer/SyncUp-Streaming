import React from "react";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

export default function Home({ user }) {
  const nav = useNavigate();

  useEffect(() => {
    if (!user) nav("/register"); 
  }, [user, nav]);

  if (!user) return null; 

  return (
    <div style={{ padding: "2rem" }}>
      <h1>Bienvenido, {user.nombre}</h1>
      <p>Usuario: {user.user}</p>
      <p>Rol: {user.role}</p>
      <button
        onClick={() => {
          localStorage.removeItem("user");
          window.location.reload(); // recarga la app y borra sesión
        }}
      >
        Cerrar sesión
      </button>
    </div>
  );
}
