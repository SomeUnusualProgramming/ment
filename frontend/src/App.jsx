import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navigation from './components/Navigation';
import UploadPage from './pages/UploadPage';
import ResultsPage from './pages/ResultsPage';
import DashboardPage from './pages/DashboardPage';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    const role = localStorage.getItem('userRole');
    if (userId && role) {
      setIsAuthenticated(true);
      setUserRole(role);
    }
  }, []);

  const handleLogin = (role, userId) => {
    setIsAuthenticated(true);
    setUserRole(role);
    localStorage.setItem('userId', userId);
    localStorage.setItem('userRole', role);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUserRole(null);
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
  };

  return (
    <Router>
      <div className="App">
        <Navigation isAuthenticated={isAuthenticated} userRole={userRole} onLogout={handleLogout} />
        {isAuthenticated ? (
          <Routes>
            <Route path="/" element={<DashboardPage userRole={userRole} />} />
            <Route path="/upload" element={<UploadPage />} />
            <Route path="/results/:documentId" element={<ResultsPage />} />
          </Routes>
        ) : (
          <LoginPage onLogin={handleLogin} />
        )}
      </div>
    </Router>
  );
}

function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8080/api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (response.ok) {
        const data = await response.json();
        onLogin(data.role || 'ANALYST', data.id || '1');
      } else {
        setError('Invalid credentials. Try: demo@example.com / password');
        onLogin('ANALYST', '1');
      }
    } catch (err) {
      setError('Authentication failed. Using demo mode.');
      onLogin('ANALYST', '1');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit}>
        <h1>Document Analyzer Login</h1>
        {error && <div className="error-message">{error}</div>}
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
        <small>Demo: Use any email/password to enter demo mode</small>
      </form>
    </div>
  );
}

export default App;
