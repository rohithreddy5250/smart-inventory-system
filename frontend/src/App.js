import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Components
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Items from './pages/Items';
import BorrowRequests from './pages/BorrowRequests';
import MyBorrows from './pages/MyBorrows';
import AuditLogs from './pages/AuditLogs';
import QRScanner from './pages/QRScanner';
import ItemDetails from './pages/ItemDetails';

// Utils
import { getToken, removeToken } from './utils/auth';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = () => {
    const token = getToken();
    if (token) {
      const userData = JSON.parse(localStorage.getItem('user'));
      setUser(userData);
      setIsAuthenticated(true);
    }
    setLoading(false);
  };

  const handleLogin = (userData) => {
    setUser(userData);
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    removeToken();
    localStorage.removeItem('user');
    setUser(null);
    setIsAuthenticated(false);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <Router>
      <div className="App">
        {isAuthenticated && <Navbar user={user} onLogout={handleLogout} />}
        
        <Routes>
          <Route 
            path="/login" 
            element={
              !isAuthenticated ? 
              <Login onLogin={handleLogin} /> : 
              <Navigate to="/dashboard" />
            } 
          />
          <Route 
            path="/register" 
            element={
              !isAuthenticated ? 
              <Register /> : 
              <Navigate to="/dashboard" />
            } 
          />
          
          {/* Protected Routes */}
          <Route
            path="/dashboard"
            element={
              isAuthenticated ? 
              <Dashboard user={user} /> : 
              <Navigate to="/login" />
            }
          />
          <Route
            path="/items"
            element={
              isAuthenticated ? 
              <Items user={user} /> : 
              <Navigate to="/login" />
            }
          />
          <Route
            path="/items/:id"
            element={
              isAuthenticated ? 
              <ItemDetails user={user} /> : 
              <Navigate to="/login" />
            }
          />
          <Route
            path="/borrow-requests"
            element={
              isAuthenticated ? 
              <BorrowRequests user={user} /> : 
              <Navigate to="/login" />
            }
          />
          <Route
            path="/my-borrows"
            element={
              isAuthenticated ? 
              <MyBorrows user={user} /> : 
              <Navigate to="/login" />
            }
          />
          <Route
            path="/audit-logs"
            element={
              isAuthenticated ? 
              <AuditLogs user={user} /> : 
              <Navigate to="/login" />
            }
          />
          <Route
            path="/qr-scanner"
            element={
              isAuthenticated ? 
              <QRScanner /> : 
              <Navigate to="/login" />
            }
          />
          
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
