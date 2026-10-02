import React, { createContext, useContext, useState, useEffect } from 'react';
import { login as apiLogin, logout as apiLogout, getMe } from '../services/apiService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMe()
      .then(userData => {
        if (userData && userData.email) {
          setUser(userData);
        } else {
          setUser(null);
        }
      })
      .catch(() => setUser(null))
      .finally(() => setLoading(false));
  }, []);

  async function login(email, password) {
    const loggedInUser = await apiLogin(email, password);
    setUser(loggedInUser);
    return loggedInUser;
  }

  async function logout() {
    try {
      await apiLogout();
    } catch (e) {
      console.warn("Logout request failed:", e);
    }
    setUser(null);
  }

  // Backend seed henüz hazır değilken test edebilmek için yardımcı mock login
  function setDemoUser(role) {
    const demo = {
      id: role === 'DOCTOR' ? 'usr-doc-01' : 'usr-mun-01',
      name: role === 'DOCTOR' ? 'Dr. Selin Yılmaz' : 'Kemal Erdem (Çevre Müh.)',
      email: role === 'DOCTOR' ? 'doctor@riverguard.org' : 'staff@corlu.bel.tr',
      role: role
    };
    setUser(demo);
    return demo;
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, setDemoUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
}