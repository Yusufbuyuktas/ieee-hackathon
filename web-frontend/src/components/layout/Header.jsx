import React from 'react';
import { Activity, Waves, Stethoscope, FileJson } from 'lucide-react';

export default function Header({ activeTab, setActiveTab, apiOnline }) {
  const isMockMode = import.meta.env.VITE_USE_MOCK !== 'false';

  return (
    <header className="bg-slate-900/90 border-b border-slate-800 sticky top-0 z-50 backdrop-blur">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          
          {/* Logo & Başlık */}
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400">
              <Waves className="w-6 h-6 animate-pulse" />
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <span className="font-bold text-lg text-white tracking-tight">OneAquaHealth</span>
                <span className="bg-cyan-500/20 text-cyan-400 text-xs px-2 py-0.5 rounded-full border border-cyan-500/30 font-mono">
                  Ergene Havzası
                </span>
              </div>
              <p className="text-xs text-slate-400">Çevre & Sağlık Entegre Erken Uyarı Sistemi</p>
            </div>
          </div>

          {/* Sekmeler */}
          <nav className="flex space-x-2">
            <button
              onClick={() => setActiveTab('monitoring')}
              className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-colors ${
                activeTab === 'monitoring'
                  ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <Activity className="w-4 h-4" />
              <span>Çevresel İzleme & Harita</span>
            </button>

            <button
              onClick={() => setActiveTab('clinical')}
              className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-colors ${
                activeTab === 'clinical'
                  ? 'bg-rose-500/20 text-rose-300 border border-rose-500/40'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <Stethoscope className="w-4 h-4" />
              <span>Klinik Karar Destek (PoC)</span>
            </button>

            <button
              onClick={() => setActiveTab('fhir')}
              className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-colors ${
                activeTab === 'fhir'
                  ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/40'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <FileJson className="w-4 h-4" />
              <span>HL7 FHIR Gezgini</span>
            </button>
          </nav>

          {/* Dinamik Ortam Rozeti — gerçek modda apiOnline durumunu yansıtır, sabit metin değil */}
          <div className="flex items-center space-x-3 text-xs">
            {isMockMode ? (
              <div className="flex items-center space-x-1.5 bg-cyan-500/10 text-cyan-400 border border-cyan-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-cyan-400"></span>
                <span>Mod: Literatür Veri Havuzu</span>
              </div>
            ) : apiOnline === true ? (
              <div className="flex items-center space-x-1.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
                <span>Canlı API / FHIR Online</span>
              </div>
            ) : apiOnline === false ? (
              <div className="flex items-center space-x-1.5 bg-rose-500/10 text-rose-400 border border-rose-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-rose-400"></span>
                <span>API Erişilemiyor</span>
              </div>
            ) : (
              <div className="flex items-center space-x-1.5 bg-slate-500/10 text-slate-400 border border-slate-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-slate-400 animate-pulse"></span>
                <span>Bağlantı Kontrol Ediliyor...</span>
              </div>
            )}
          </div>

        </div>
      </div>
    </header>
  );
}