import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./Login.css";

const API_BASE_URL = "http://localhost:8080/api/auth";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetch(`${API_BASE_URL}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Login failed");
      }

      const data = await response.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("adminEmail", data.email);
      localStorage.setItem("adminName", data.name);
      
      navigate("/dashboard");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-background"></div>
      
      <div className="login-card retro-card animate-slide-up">
        <div className="login-header">
          <h1 className="retro-title text-5xl text-[#1B5E20] mb-2">
            ADMIN LOGIN
          </h1>
          <p className="text-lg text-[#2E7D32] font-semibold">
            Student Management Portal
          </p>
        </div>

        {error && (
          <div className="error-box animate-slide-up">
            <p className="text-red-800 font-bold">⚠ {error}</p>
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="retro-input"
              placeholder="admin@example.com"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="retro-input"
              placeholder="Enter your password"
              disabled={loading}
            />
          </div>

          <button
            type="submit"
            className="retro-btn bg-[#4CAF50] text-white w-full py-4 text-lg mt-4"
            disabled={loading}
          >
            {loading ? (
              <span className="flex items-center justify-center">
                <div className="small-loader mr-3"></div>
                Logging in...
              </span>
            ) : (
              "LOGIN"
            )}
          </button>
        </form>

        <div className="login-footer">
          
          <p className="text-sm text-gray-600">
            Secure access to student management system
          </p>
        </div>
      </div>
    </div>
  );
}
