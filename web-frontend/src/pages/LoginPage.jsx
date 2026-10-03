import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
// Arka plan görseli assets klasöründen alınıyor
import wallpaperImg from '../assets/WALLPAPER.webp';

import { 
  Waves, 
  Lock, 
  Mail, 
  ArrowLeft, 
  AlertCircle, 
  ShieldAlert, 
  Stethoscope 
} from 'lucide-react';

export default function LoginPage({ onSuccess, onBackClick }) {
  const { login, setDemoUser } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const user = await login(email, password);
      onSuccess(user);
    } catch (err) {
      setError("Authentication failed: Invalid email/password or server credentials session unverified.");
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = (role) => {
    const user = setDemoUser(role);
    onSuccess(user);
  };

  return (
    <div className="relative min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-center items-center p-4 overflow-hidden selection:bg-cyan-500/30">
      
      {/* ========================================================= */}
      {/* CANLI VE SİNEMATİK WALLPAPER ARKA PLAN KATMANI */}
      {/* ========================================================= */}
      <div className="fixed inset-0 pointer-events-none z-0">
        {/* Ana Duvar Kâğıdı */}
        <img 
          src={wallpaperImg} 
          alt="RiverGuard Catchment" 
          className="absolute inset-0 w-full h-full object-cover opacity-60"
        />

        {/* Hafif Kontrast ve Karartma Katmanı */}
        <div className="absolute inset-0 bg-gradient-to-b from-slate-950/70 via-slate-950/45 to-slate-950/80" />

        {/* Siber Ambient Işık Parıltıları */}
        <div className="absolute -top-32 left-1/2 -translate-x-1/2 w-[600px] h-[350px] bg-cyan-500/15 rounded-full blur-[130px]" />
        <div className="absolute bottom-10 right-10 w-[450px] h-[450px] bg-blue-600/15 rounded-full blur-[140px]" />
      </div>

      {/* ========================================================= */}
      {/* GİRİŞ KARTI (GLASSMORPHISM PORTAL PANELİ) */}
      {/* ========================================================= */}
      <div className="relative z-10 w-full max-w-md bg-slate-950/80 backdrop-blur-xl border border-slate-800/90 hover:border-slate-700 rounded-3xl p-6 sm:p-8 shadow-2xl shadow-black/90 transition-all">
        
        {/* Ana Sayfaya Dön Butonu */}
        <button
          onClick={onBackClick}
          className="absolute left-6 top-6 text-slate-400 hover:text-cyan-300 flex items-center space-x-1.5 text-xs transition-colors cursor-pointer"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Home</span>
        </button>

        {/* Logo ve Başlık */}
        <div className="text-center mt-6 mb-6">
          <div className="w-12 h-12 rounded-2xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 mx-auto mb-3 shadow-inner shadow-cyan-500/20">
            <Waves className="w-6 h-6 animate-pulse" />
          </div>
          <h2 className="text-xl font-bold text-white tracking-tight">Authorized Portal Access</h2>
          <p className="text-xs text-slate-400 mt-1">
            RiverGuard Operations & Clinical Decision Support System
          </p>
        </div>

        {/* Hata Bildirimi */}
        {error && (
          <div className="mb-4 p-3 rounded-xl bg-rose-950/60 border border-rose-500/40 text-rose-300 text-xs flex items-start space-x-2 backdrop-blur-md">
            <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-rose-400" />
            <span>{error}</span>
          </div>
        )}

        {/* Form Alanı (Erişilebilir Label/Input Eşleşmeleri) */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="login-email" className="block text-xs font-medium text-slate-300 mb-1.5">
              Institutional Email
            </label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-500 absolute left-3 top-3" />
              <input
                id="login-email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="officer@catchment.gov"
                className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl pl-9 pr-3 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-400 focus:ring-1 focus:ring-cyan-400 transition-all"
              />
            </div>
          </div>

          <div>
            <label htmlFor="login-password" className="block text-xs font-medium text-slate-300 mb-1.5">
              Password
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-500 absolute left-3 top-3" />
              <input
                id="login-password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl pl-9 pr-3 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-400 focus:ring-1 focus:ring-cyan-400 transition-all"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold py-2.5 rounded-xl text-xs transition-all shadow-lg shadow-cyan-500/25 hover:shadow-cyan-400/35 disabled:opacity-50 cursor-pointer"
          >
            {loading ? "Authenticating..." : "Sign In"}
          </button>
        </form>

        {/* Jüri & Demo Hızlı Giriş Butonları (Yalnızca Yerel Geliştirmede Görünür) */}
        {import.meta.env.DEV && (
          <div className="mt-6 pt-5 border-t border-slate-800 text-center">
            <p className="text-[11px] text-slate-400 mb-2.5 font-medium">
              Demo / Jury Fast-Track Access:
            </p>
            <div className="grid grid-cols-2 gap-2.5">
              <button
                type="button"
                onClick={() => handleDemoLogin('MUNICIPALITY_STAFF')}
                className="flex items-center justify-center space-x-1.5 bg-slate-900/90 hover:bg-slate-800 border border-slate-700/80 hover:border-cyan-500/40 px-3 py-2 rounded-xl text-xs text-slate-200 transition-all cursor-pointer group"
              >
                <ShieldAlert className="w-3.5 h-3.5 text-cyan-400 group-hover:scale-110 transition-transform" />
                <span>Municipality Staff</span>
              </button>
              <button
                type="button"
                onClick={() => handleDemoLogin('DOCTOR')}
                className="flex items-center justify-center space-x-1.5 bg-slate-900/90 hover:bg-slate-800 border border-slate-700/80 hover:border-rose-500/40 px-3 py-2 rounded-xl text-xs text-slate-200 transition-all cursor-pointer group"
              >
                <Stethoscope className="w-3.5 h-3.5 text-rose-400 group-hover:scale-110 transition-transform" />
                <span>Clinician (Doctor)</span>
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}