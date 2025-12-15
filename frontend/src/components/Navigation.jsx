import React from 'react';
import { Link } from 'react-router-dom';
import './Navigation.css';

function Navigation({ isAuthenticated, userRole, onLogout }) {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <h2>📄 Document Risk Analyzer</h2>
      </div>

      {isAuthenticated && (
        <div className="navbar-menu">
          <Link to="/" className="nav-link">Dashboard</Link>
          <Link to="/upload" className="nav-link">Upload Document</Link>

          {userRole === 'ADMIN' && (
            <Link to="/admin" className="nav-link admin-link">Admin Panel</Link>
          )}

          <div className="navbar-right">
            <span className="user-badge">{userRole}</span>
            <button onClick={onLogout} className="logout-btn">Logout</button>
          </div>
        </div>
      )}
    </nav>
  );
}

export default Navigation;
