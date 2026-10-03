import React from 'react';
import { Activity, Waves, Stethoscope, FileJson, LogOut, User } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export default function Header({ activeTab, setActiveTab, apiOnline, onLogout, onLogoClick }) {
  const { user, logout } = useAuth();

  const isDoctor = user?.role === 'DOCTOR';
  const isMunicipality = user?.role === 'MUNICIPALITY_STAFF';

  const handleLogout = async () => {
    await logout();
    if (onLogout) onLogout();
  };

  const handleLogoClick = () => {
    if (onLogoClick) {
      onLogoClick();
    } else if (onLogout) {
      onLogout();
    }
  };

  return (
    <header className="bg-slate-900/95 border-b border-slate-800 sticky top-0 z-50 backdrop-blur">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row items-center justify-between py-2.5 sm:py-0 sm:h-16 gap-2.5 sm:gap-0">
          
          {/* Brand & Subtitle (Clickable: Returns to Landing Page) */}
          <div className="flex items-center justify-between w-full sm:w-auto">
            <button
              type="button"
              onClick={handleLogoClick}
              className="flex items-center space-x-3 text-left group focus:outline-none transition-opacity hover:opacity-90 cursor-pointer"
              title="Return to Landing Page"
            >
              <div className="w-9 h-9 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 shrink-0 group-hover:border-cyan-400/60 group-hover:bg-cyan-500/20 transition-all">
                <Waves className="w-5 h-5" />
              </div>
              <div>
                <div className="flex items-center space-x-2">
                  <span className="font-bold text-lg text-white tracking-tight group-hover:text-cyan-300 transition-colors">
                    RiverGuard
                  </span>
                  <span className="bg-cyan-500/20 text-cyan-400 text-xs px-2 py-0.5 rounded-full border border-cyan-500/30 font-mono">
                    Ergene Basin
                  </span>
                </div>
                <p className="text-[11px] text-slate-400 hidden xs:block">
                  Integrated Environmental & Public Health Surveillance
                </p>
              </div>
            </button>

            {/* Mobile Sign Out Button */}
            <div className="sm:hidden flex items-center space-x-2">
              <button
                type="button"
                onClick={handleLogout}
                className="p-1.5 text-slate-400 hover:text-rose-400 bg-slate-800/80 rounded-lg border border-slate-700 transition-colors"
                title="Sign Out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Navigation Tabs - Filtered by User Role */}
          <nav className="flex space-x-1.5 sm:space-x-2 w-full sm:w-auto overflow-x-auto pb-1 sm:pb-0 scrollbar-none">
            {(!user || isMunicipality) && (
              <button
                type="button"
                onClick={() => setActiveTab('monitoring')}
                className={`flex items-center space-x-1.5 sm:space-x-2 px-3 py-1.5 sm:py-2 rounded-lg text-xs sm:text-sm font-medium transition-colors shrink-0 ${
                  activeTab === 'monitoring'
                    ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`}
              >
                <Activity className="w-4 h-4 shrink-0" />
                <span className="hidden md:inline">Environmental Surveillance</span>
                <span className="md:hidden">Surveillance</span>
              </button>
            )}

            {(!user || isDoctor) && (
              <button
                type="button"
                onClick={() => setActiveTab('clinical')}
                className={`flex items-center space-x-1.5 sm:space-x-2 px-3 py-1.5 sm:py-2 rounded-lg text-xs sm:text-sm font-medium transition-colors shrink-0 ${
                  activeTab === 'clinical'
                    ? 'bg-rose-500/20 text-rose-300 border border-rose-500/40'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`}
              >
                <Stethoscope className="w-4 h-4 shrink-0" />
                <span className="hidden md:inline">Clinical Decision Support</span>
                <span className="md:hidden">Clinical (CDS)</span>
              </button>
            )}

            <button
              type="button"
              onClick={() => setActiveTab('fhir')}
              className={`flex items-center space-x-1.5 sm:space-x-2 px-3 py-1.5 sm:py-2 rounded-lg text-xs sm:text-sm font-medium transition-colors shrink-0 ${
                activeTab === 'fhir'
                  ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/40'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <FileJson className="w-4 h-4 shrink-0" />
              <span className="hidden md:inline">HL7 FHIR Explorer</span>
              <span className="md:hidden">FHIR R4</span>
            </button>
          </nav>

          {/* Desktop User Status & Live Indicator */}
          <div className="hidden sm:flex items-center space-x-3 text-xs">
            {/* Live API Health Badge */}
            {apiOnline === true ? (
              <div className="flex items-center space-x-1.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
                <span>Live Backend</span>
              </div>
            ) : apiOnline === false ? (
              <div className="flex items-center space-x-1.5 bg-rose-500/10 text-rose-400 border border-rose-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-rose-400"></span>
                <span>Offline</span>
              </div>
            ) : (
              <div className="flex items-center space-x-1.5 bg-slate-500/10 text-slate-400 border border-slate-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-slate-400 animate-pulse"></span>
                <span>Connecting...</span>
              </div>
            )}

            {user && (
              <div className="flex items-center space-x-2 bg-slate-800/90 border border-slate-700 px-2.5 py-1 rounded-xl">
                <User className="w-3.5 h-3.5 text-cyan-400" />
                <div className="flex flex-col text-left">
                  <span className="text-white font-medium text-[11px] leading-tight">{user.name || user.email}</span>
                  <span className="text-[10px] text-cyan-400 font-mono leading-tight">
                    {user.role === 'DOCTOR' ? 'Doctor' : 'Municipality Staff'}
                  </span>
                </div>
              </div>
            )}

            <button
              type="button"
              onClick={handleLogout}
              className="flex items-center space-x-1 text-slate-400 hover:text-rose-400 bg-slate-800 hover:bg-slate-700/80 px-2.5 py-1.5 rounded-lg border border-slate-700 transition-colors"
              title="Sign Out"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Sign Out</span>
            </button>
          </div>

        </div>
      </div>
    </header>
  );
}