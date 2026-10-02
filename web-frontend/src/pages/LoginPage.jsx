import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Waves, Lock, Mail, ArrowLeft, AlertCircle, ShieldAlert, Stethoscope } from 'lucide-react';

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
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-center items-center p-4">
      <div className="w-full max-w-md bg-slate-900/90 border border-slate-800 rounded-2xl p-6 sm:p-8 shadow-2xl relative">
        
        <button
          onClick={onBackClick}
          className="absolute left-6 top-6 text-slate-400 hover:text-white flex items-center space-x-1 text-xs transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Home</span>
        </button>

        <div className="text-center mt-4 mb-6">
          <div className="w-12 h-12 rounded-2xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 mx-auto mb-3">
            <Waves className="w-6 h-6" />
          </div>
          <h2 className="text-xl font-bold text-white tracking-tight">Authorized Portal Access</h2>
          <p className="text-xs text-slate-400 mt-1">
            RiverGuard Operations & Clinical Decision Support System
          </p>
        </div>

        {error && (
          <div className="mb-4 p-3 rounded-xl bg-rose-950/50 border border-rose-500/40 text-rose-300 text-xs flex items-start space-x-2">
            <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-rose-400" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1.5">Institutional Email</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-500 absolute left-3 top-3" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="officer@catchment.gov"
                className="w-full bg-slate-800/80 border border-slate-700 rounded-xl pl-9 pr-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1.5">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-500 absolute left-3 top-3" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setEmail ? setPassword(e.target.value) : null}
                placeholder="••••••••"
                className="w-full bg-slate-800/80 border border-slate-700 rounded-xl pl-9 pr-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-semibold py-2.5 rounded-xl text-xs transition-colors shadow-lg shadow-cyan-500/20 disabled:opacity-50"
          >
            {loading ? "Authenticating..." : "Sign In"}
          </button>
        </form>

        {/* Demo Fast-Switch Buttons */}
        <div className="mt-6 pt-5 border-t border-slate-800/80 text-center">
          <p className="text-[11px] text-slate-400 mb-2.5 font-medium">
            Demo / Jury Fast-Track Access:
          </p>
          <div className="grid grid-cols-2 gap-2">
            <button
              type="button"
              onClick={() => handleDemoLogin('MUNICIPALITY_STAFF')}
              className="flex items-center justify-center space-x-1.5 bg-slate-800 hover:bg-slate-700 border border-slate-700 px-3 py-2 rounded-xl text-xs text-slate-200 transition-colors"
            >
              <ShieldAlert className="w-3.5 h-3.5 text-cyan-400" />
              <span>Municipality Staff</span>
            </button>
            <button
              type="button"
              onClick={() => handleDemoLogin('DOCTOR')}
              className="flex items-center justify-center space-x-1.5 bg-slate-800 hover:bg-slate-700 border border-slate-700 px-3 py-2 rounded-xl text-xs text-slate-200 transition-colors"
            >
              <Stethoscope className="w-3.5 h-3.5 text-rose-400" />
              <span>Clinician (Doctor)</span>
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}