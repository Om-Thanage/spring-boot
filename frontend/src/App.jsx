import { useState, useEffect } from "react";
import "./App.css";

const API_BASE_URL = "http://localhost:8080/students";

export default function App() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modal, setModal] = useState(null); // "add", "edit", "marks", or null
  const [current, setCurrent] = useState(null);
  const [form, setForm] = useState({ name: "", email: "", course: "", marks: "" });
  const [marks, setMarks] = useState("");

  const fetchStudents = async () => {
    setLoading(true);
    try {
      const res = await fetch(API_BASE_URL);
      if (!res.ok) throw new Error("Failed to fetch students");
      const data = await res.json();
      setStudents(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchStudents(); }, []);

  const closeModal = () => {
    setModal(null);
    setCurrent(null);
    setForm({ name: "", email: "", course: "", marks: "" });
    setMarks("");
  };

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const method = modal === "add" ? "POST" : "PUT";
    const url = modal === "add"
      ? API_BASE_URL
      : `${API_BASE_URL}/${current.id}`;

    try {
      const res = await fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...form,
          marks: form.marks ? parseFloat(form.marks) : null,
        }),
      });
      if (!res.ok) throw new Error(`Failed to ${modal === "add" ? "add" : "update"} student`);
      await fetchStudents();
      closeModal();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleMarksUpdate = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_BASE_URL}/${current.id}/marks`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ marks: parseFloat(marks) }),
      });
      if (!res.ok) throw new Error("Failed to update marks");
      await fetchStudents();
      closeModal();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (email) => {
    if (!window.confirm("Are you sure you want to delete this student?")) return;
    try {
      const res = await fetch(`${API_BASE_URL}/email/${email}`, { method: "DELETE" });
      if (!res.ok) throw new Error("Failed to delete student");
      await fetchStudents();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="min-h-screen p-8">
      <header className="mb-12 text-center animate-fade-in">
        <h1 className="retro-title text-6xl mb-4 text-[#1B5E20]">
          STUDENT MANAGEMENT PORTAL
        </h1>
      </header>

      {error && (
        <div className="max-w-4xl mx-auto retro-card bg-red-50 border-red-400 p-4 mb-6 animate-slide-up">
          <p className="text-red-800 font-bold text-center">Warning: {error}</p>
        </div>
      )}

      <div className="text-center mb-8">
        <button
          onClick={() => setModal("add")}
          className="retro-btn bg-[#4CAF50] text-white px-8 py-4 text-lg"
        >
          Add New Student
        </button>
      </div>

      {loading ? (
        <div className="text-center">
          <div className="retro-loader mx-auto mb-4"></div>
          <p className="text-2xl font-bold text-[#2E7D32]">Loading Students...</p>
        </div>
      ) : students.length === 0 ? (
        <div className="max-w-2xl mx-auto text-center retro-card p-12 bg-white animate-slide-up">
          <p className="text-3xl font-bold text-[#2E7D32] mb-2">No Students Found</p>
          <p className="text-lg text-gray-600">Add your first student to get started.</p>
        </div>
      ) : (
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 justify-items-center">
          {students.map((s, i) => (
            <div
              key={s.id}
              className="retro-card bg-white p-6 animate-slide-up w-full max-w-sm"
              style={{ animationDelay: `${i * 0.1}s` }}
            >
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-2xl font-black text-[#1B5E20]">{s.name}</h3>
                <div className="retro-badge bg-[#81C784] px-3 py-1 text-sm">
                  {s.marks !== null ? `${s.marks}%` : "N/A"}
                </div>
              </div>

              <div className="retro-divider"></div>

              <div className="space-y-2 mb-6">
                <div className="info-row">
                  <span className="font-bold text-[#2E7D32]">Email:</span>
                  <span className="text-sm text-gray-700">{s.email}</span>
                </div>
                <div className="info-row">
                  <span className="font-bold text-[#2E7D32]">Course:</span>
                  <span className="text-sm text-gray-700">{s.course}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <button
                  onClick={() => { setCurrent(s); setMarks(s.marks || ""); setModal("marks"); }}
                  className="retro-btn bg-[#81C784] text-black text-sm py-2"
                >
                  Marks
                </button>
                <button
                  onClick={() => { setCurrent(s); setForm(s); setModal("edit"); }}
                  className="retro-btn bg-[#66BB6A] text-white text-sm py-2"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDelete(s.email)}
                  className="retro-btn bg-[#C62828] text-white col-span-2 py-2 text-sm"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add/Edit Modal */}
      {(modal === "add" || modal === "edit") && (
        <Modal title={modal === "add" ? "Add Student" : "Edit Student"} onClose={closeModal}>
          <form onSubmit={handleSubmit} className="space-y-4">
            {["name", "email", "course", "marks"].map((field) => (
              <div key={field}>
                <label className="block text-sm font-bold mb-2 text-[#2E7D32] capitalize">
                  {field} {field !== "marks" && "*"}
                </label>
                <input
                  type={field === "marks" ? "number" : field === "email" ? "email" : "text"}
                  name={field}
                  value={form[field]}
                  required={field !== "marks"}
                  onChange={handleChange}
                  className="retro-input w-full px-4 py-3"
                  placeholder={`Enter ${field}`}
                />
              </div>
            ))}
            <div className="flex gap-3 pt-4">
              <button type="submit" className="retro-btn bg-[#4CAF50] text-white flex-1 py-3">
                {modal === "add" ? "Add Student" : "Update Student"}
              </button>
              <button onClick={closeModal} type="button" className="retro-btn bg-gray-300 text-black flex-1 py-3">
                Cancel
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Marks Modal */}
      {modal === "marks" && (
        <Modal title="Update Marks" onClose={closeModal}>
          <p className="text-center text-lg font-semibold text-[#2E7D32] mb-4">
            Student: {current.name}
          </p>
          <form onSubmit={handleMarksUpdate} className="space-y-4">
            <input
              type="number"
              value={marks}
              min="0"
              max="100"
              step="0.01"
              required
              onChange={(e) => setMarks(e.target.value)}
              className="retro-input w-full px-4 py-3 text-center text-2xl font-bold"
              placeholder="Enter marks (0-100)"
            />
            <div className="flex gap-3 pt-4">
              <button type="submit" className="retro-btn bg-[#81C784] text-black flex-1 py-3">
                Update Marks
              </button>
              <button onClick={closeModal} type="button" className="retro-btn bg-gray-300 text-black flex-1 py-3">
                Cancel
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}


function Modal({ title, children, onClose }) {
  return (
    <div className="fixed inset-0 retro-modal-overlay flex items-center justify-center z-50 p-4 animate-fade-in">
      <div className="retro-card bg-white p-8 max-w-md w-full animate-slide-up relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-600 hover:text-black font-bold text-2xl transition-colors"
          aria-label="Close"
        >
          ×
        </button>
        <h2 className="retro-title text-3xl mb-6 text-[#1B5E20] text-center">{title}</h2>
        {children}
      </div>
    </div>
  );
}
