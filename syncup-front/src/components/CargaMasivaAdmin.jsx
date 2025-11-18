import React, { useState, useRef } from "react";
import API from "../services/api";
import "../styles/CargaMasiva.css";

export default function CargaMasivaAdmin() {
  const [archivo, setArchivo] = useState(null);
  const [arrastrando, setArrastrando] = useState(false);
  const [cargando, setCargando] = useState(false);
  const [resultado, setResultado] = useState(null);
  const [error, setError] = useState("");
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setArrastrando(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setArrastrando(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setArrastrando(false);
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      validarYSetArchivo(files[0]);
    }
  };

  const handleFileSelect = (e) => {
    const files = e.target.files;
    if (files.length > 0) {
      validarYSetArchivo(files[0]);
    }
  };

  const validarYSetArchivo = (file) => {
    setError("");
    setResultado(null);
    
    const nombre = file.name.toLowerCase();
    if (!nombre.endsWith('.csv') && !nombre.endsWith('.txt')) {
      setError("El archivo debe ser .csv o .txt");
      return;
    }
    
    setArchivo(file);
  };

  const handleCargar = async () => {
    if (!archivo) return;

    setCargando(true);
    setError("");
    setResultado(null);

    try {
      const formData = new FormData();
      formData.append('file', archivo);

      const response = await API.post('/admin/canciones/cargar', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      setResultado(response.data);
      setArchivo(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    } catch (err) {
      const errorMsg = err.response?.data?.message || "Error al cargar el archivo";
      setError(errorMsg);
      if (err.response?.data) {
        setResultado(err.response.data);
      }
    } finally {
      setCargando(false);
    }
  };

  const limpiar = () => {
    setArchivo(null);
    setResultado(null);
    setError("");
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  return (
    <div className="carga-masiva-view">
      <div className="view-header">
        <h1>Carga Masiva 📤</h1>
        <p>Importa múltiples canciones desde un archivo</p>
      </div>

      <div className="carga-masiva-container">
        <div 
          className={`upload-card ${arrastrando ? 'dragging' : ''} ${archivo ? 'has-file' : ''}`}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            accept=".csv,.txt"
            style={{ display: 'none' }}
          />
          
          {archivo ? (
            <div className="file-selected">
              <div className="file-icon">📄</div>
              <h3>{archivo.name}</h3>
              <p>{(archivo.size / 1024).toFixed(2)} KB</p>
              <div className="file-actions">
                <button 
                  className="btn-primary"
                  onClick={handleCargar}
                  disabled={cargando}
                >
                  {cargando ? "Cargando..." : "Cargar Canciones"}
                </button>
                <button 
                  className="btn-secondary"
                  onClick={limpiar}
                  disabled={cargando}
                >
                  Cancelar
                </button>
              </div>
            </div>
          ) : (
            <>
              <div className="upload-icon">📁</div>
              <h3>Arrastra tu archivo aquí</h3>
              <p>o haz clic en el botón para seleccionar</p>
              <button 
                className="btn-primary"
                onClick={() => fileInputRef.current.click()}
                style={{ marginTop: '1rem' }}
              >
                Seleccionar Archivo
              </button>
            </>
          )}
        </div>

        {error && (
          <div className="alert alert-error">
            <span>⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {resultado && (
          <div className="resultado-carga">
            <div className={`resultado-header ${resultado.errores > 0 ? 'con-errores' : 'exitoso'}`}>
              <h3>{resultado.errores > 0 ? '⚠️ Carga completada con errores' : '✅ Carga exitosa'}</h3>
              <div className="resultado-stats">
                <span className="stat-exitosas">✓ {resultado.exitosas} exitosas</span>
                {resultado.errores > 0 && (
                  <span className="stat-errores">✗ {resultado.errores} errores</span>
                )}
              </div>
            </div>

            {resultado.detalleErrores && resultado.detalleErrores.length > 0 && (
              <div className="errores-detalle">
                <h4>Detalle de errores:</h4>
                <div className="errores-lista">
                  {resultado.detalleErrores.map((err, index) => (
                    <div key={index} className="error-item">
                      <span className="error-linea">Línea {err.linea}:</span>
                      <span className="error-mensaje">{err.error}</span>
                      <code className="error-contenido">{err.contenido}</code>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        <div className="upload-info">
          <h4>📋 Formato esperado (CSV o TXT)</h4>
          <code>titulo,artista,genero,año,duracion</code>
          
          <h4>🎵 Géneros válidos</h4>
          <p>ROCK, POP, ELECTRONICA, HIPHOP, SALSA, REGGAETON</p>
          
          <h4>📝 Ejemplo</h4>
          <code>Bohemian Rhapsody,Queen,ROCK,1975,354</code>
          <code>Blinding Lights,The Weeknd,POP,2020,200</code>
        </div>
      </div>
    </div>
  );
}