import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import UserDashboard from "../components/UserDashboard";
import AdminDashboard from "../components/AdminDashboard";
import "../styles/Home.css";

export default function Home({ user, setUser }) {
  const nav = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [currentView, setCurrentView] = useState("inicio");

  useEffect(() => {
    if (!user) {
      nav("/login");
    }
  }, [user, nav]);

  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
    nav("/login");
  };

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  if (!user) {
    return (
      <div className="loading-screen">
        <div className="spinner"></div>
        <p>Cargando...</p>
      </div>
    );
  }

  const isAdmin = user.role === "ADMIN";

  return (
    <div className="home-container">
      <Navbar 
        user={user} 
        onLogout={handleLogout}
        onToggleSidebar={toggleSidebar}
      />

      <div className="home-content">
        <Sidebar 
          isOpen={sidebarOpen}
          currentView={currentView}
          setCurrentView={setCurrentView}
          isAdmin={isAdmin}
        />

        <main className={`main-content ${sidebarOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
          {isAdmin ? (
            <AdminDashboard currentView={currentView} user={user} />
          ) : (
            <UserDashboard currentView={currentView} user={user} />
          )}
        </main>
      </div>
    </div>
  );
}