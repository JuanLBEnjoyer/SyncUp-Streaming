import React, { useState, useEffect } from "react";
import API from "../services/api";
import Modal from "./Modal";
import "../styles/CancionesAdmin.css";

export default function CancionesAdmin() {
  const [canciones, setCanciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedGenero, setSelectedGenero] = useState("");
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState("create");
  const [currentCancion, setCurrentCancion] = useState(null);
  const [deleteModal, setDeleteModal] = useState(false);
  const [cancionToDelete, setCancionToDelete] = useState(null);

  const generos = ["ROCK", "POP", "ELECTRONICA", "HIPHOP", "SALSA", "REGGAETON"];

  useEffect(() => {
    cargarCanciones();
  }, []);

  const cargarCanciones = async () => {
    try {
      setLoading(true);
      setError("");
      const { data } = await API.get("/canciones");
      setCanciones(data);
    } catch (err) {
      setError("Error al cargar las canciones");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setModalMode("create");
    setCurrentCancion(null);
    setModalOpen(true);
  };

  const handleEdit = (cancion) => {
    setModalMode("edit");
    setCurrentCancion(cancion);
    setModalOpen(true);
  };

  const handleDeleteClick = (cancion) => {
    setCancionToDelete(cancion);
    setDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await API.delete(`/canciones/${cancionToDelete.id}`);
      setCanciones(canciones.filter(c => c.id !== cancionToDelete.id));
      setDeleteModal(false);
      setCancionToDelete(null);
    } catch (err) {
      alert("Error al eliminar la canción");
      console.error(err);
    }
  };

  const handleSave = async (formData) => {
    try {
      if (modalMode === "create") {
        const { data } = await API.post("/canciones", formData);
        setCanciones([...canciones, data.cancion]);
      } else {
        const { data } = await API.put(`/canciones/${currentCancion.id}`, formData);
        setCanciones(canciones.map(c => 
          c.id === currentCancion.id ? data.cancion : c
        ));
      }
      setModalOpen(false);
      setCurrentCancion(null);
    } catch (err) {
      throw err; 
    }
  };

  const cancionesFiltradas = canciones.filter(cancion => {
    const matchSearch = cancion.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       cancion.artista.toLowerCase().includes(searchTerm.toLowerCase());
    const matchGenero = !selectedGenero || cancion.genero === selectedGenero;
    return matchSearch && matchGenero;
  });

  return (
    <div className="canciones-admin-view">
      <div className="view-header">
        <div>
          <h1>Gestionar Canciones 🎵</h1>
          <p>Total: {canciones.length} canciones</p>
        </div>
        <button className="btn-primary" onClick={handleCreate}>
          + Agregar Canción
        </button>
      </div>

      <div className="filters-section">
        <input 
          type="text" 
          placeholder="Buscar por título o artista..."
          className="filter-input"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <select 
          className="filter-select"
          value={selectedGenero}
          onChange={(e) => setSelectedGenero(e.target.value)}
        >
          <option value="">Todos los géneros</option>
          {generos.map(g => (
            <option key={g} value={g}>{g}</option>
          ))}
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
          <p>Cargando canciones...</p>
        </div>
      ) : cancionesFiltradas.length === 0 ? (
        <div className="empty-state">
          <span className="empty-icon">🎵</span>
          <p>{searchTerm || selectedGenero ? "No se encontraron canciones" : "No hay canciones en el catálogo"}</p>
          {!searchTerm && !selectedGenero && (
            <button className="btn-secondary" onClick={handleCreate}>
              Agregar Primera Canción
            </button>
          )}
        </div>
      ) : (
        <div className="table-container">
          <table className="canciones-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Artista</th>
                <th>Género</th>
                <th>Año</th>
                <th>Duración</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {cancionesFiltradas.map((cancion) => (
                <tr key={cancion.id}>
                  <td>{cancion.id}</td>
                  <td className="song-title">{cancion.titulo}</td>
                  <td>{cancion.artista}</td>
                  <td>
                    <span className={`genre-badge genre-${cancion.genero.toLowerCase()}`}>
                      {cancion.genero}
                    </span>
                  </td>
                  <td>{cancion.anio}</td>
                  <td>{cancion.duracion}s</td>
                  <td>
                    <div className="action-buttons">
                      <button 
                        className="btn-icon btn-edit"
                        onClick={() => handleEdit(cancion)}
                        title="Editar"
                      >
                        ✏️
                      </button>
                      <button 
                        className="btn-icon btn-delete"
                        onClick={() => handleDeleteClick(cancion)}
                        title="Eliminar"
                      >
                        🗑️
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={modalMode === "create" ? "Agregar Canción" : "Editar Canción"}
      >
        <CancionForm
          mode={modalMode}
          cancion={currentCancion}
          onSave={handleSave}
          onCancel={() => setModalOpen(false)}
          generos={generos}
        />
      </Modal>

      <Modal
        isOpen={deleteModal}
        onClose={() => setDeleteModal(false)}
        title="Confirmar Eliminación"
        size="small"
      >
        <div className="delete-confirmation">
          <p>¿Estás seguro de que deseas eliminar esta canción?</p>
          <div className="delete-info">
            <strong>{cancionToDelete?.titulo}</strong>
            <span>{cancionToDelete?.artista}</span>
          </div>
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
              Eliminar
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

function CancionForm({ mode, cancion, onSave, onCancel, generos }) {
  const [formData, setFormData] = useState({
    titulo: cancion?.titulo || "",
    artista: cancion?.artista || "",
    genero: cancion?.genero || "",
    anio: cancion?.anio || "",
    duracion: cancion?.duracion || "",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    
    if (errors[name]) {
      setErrors({ ...errors, [name]: "" });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    setLoading(true);

    try {
      await onSave(formData);
    } catch (err) {
      setLoading(false);
      const errorData = err.response?.data;
      
      if (errorData?.errors) {
        setErrors(errorData.errors);
      } else if (errorData?.message) {
        setErrors({ general: errorData.message });
      } else {
        setErrors({ general: "Error al guardar la canción" });
      }
    }
  };

  return (
    <form className="modal-form" onSubmit={handleSubmit}>
      {errors.general && (
        <div className="alert alert-error">
          <span>⚠️</span>
          <span>{errors.general}</span>
        </div>
      )}

      <div className={`form-group ${errors.titulo ? 'has-error' : ''}`}>
        <label htmlFor="titulo">Título *</label>
        <input
          id="titulo"
          name="titulo"
          type="text"
          placeholder="Título de la canción"
          value={formData.titulo}
          onChange={handleChange}
          disabled={loading}
        />
        {errors.titulo && <span className="form-error">{errors.titulo}</span>}
      </div>

      <div className={`form-group ${errors.artista ? 'has-error' : ''}`}>
        <label htmlFor="artista">Artista *</label>
        <input
          id="artista"
          name="artista"
          type="text"
          placeholder="Nombre del artista"
          value={formData.artista}
          onChange={handleChange}
          disabled={loading}
        />
        {errors.artista && <span className="form-error">{errors.artista}</span>}
      </div>

      <div className={`form-group ${errors.genero ? 'has-error' : ''}`}>
        <label htmlFor="genero">Género *</label>
        <select
          id="genero"
          name="genero"
          value={formData.genero}
          onChange={handleChange}
          disabled={loading}
        >
          <option value="">Selecciona un género</option>
          {generos.map(g => (
            <option key={g} value={g}>{g}</option>
          ))}
        </select>
        {errors.genero && <span className="form-error">{errors.genero}</span>}
      </div>

      <div className="form-row">
        <div className={`form-group ${errors.anio ? 'has-error' : ''}`}>
          <label htmlFor="anio">Año *</label>
          <input
            id="anio"
            name="anio"
            type="number"
            placeholder="2024"
            value={formData.anio}
            onChange={handleChange}
            disabled={loading}
          />
          {errors.anio && <span className="form-error">{errors.anio}</span>}
        </div>

        <div className={`form-group ${errors.duracion ? 'has-error' : ''}`}>
          <label htmlFor="duracion">Duración (segundos) *</label>
          <input
            id="duracion"
            name="duracion"
            type="number"
            step="0.01"
            placeholder="180.5"
            value={formData.duracion}
            onChange={handleChange}
            disabled={loading}
          />
          {errors.duracion && <span className="form-error">{errors.duracion}</span>}
        </div>
      </div>

      <div className="modal-actions">
        <button 
          type="button" 
          className="btn-secondary" 
          onClick={onCancel}
          disabled={loading}
        >
          Cancelar
        </button>
        <button 
          type="submit" 
          className="btn-primary"
          disabled={loading}
        >
          {loading ? (
            <>
              <span className="spinner"></span>
              <span style={{ marginLeft: '0.5rem' }}>Guardando...</span>
            </>
          ) : (
            mode === "create" ? "Crear Canción" : "Guardar Cambios"
          )}
        </button>
      </div>
    </form>
  );
}