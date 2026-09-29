import React from 'react';
import { Activity, Waves, Stethoscope, FileJson } from 'lucide-react';

export default function Header({ activeTab, setActiveTab, apiOnline }) {
  // apiService.js'teki İKİ ayrı flag'i okuyor (gözlemler ve vatandaş bildirimleri
  // bağımsız olarak mock/canlı olabilir) — Header'ın eski tek "VITE_USE_MOCK"
  // değişkenine bakması, apiService ile senkron olmadığı için yanlış rozet
  // gösteriyordu. Artık aynı kaynaktan okuyor.
  const isObservationsMock = import.meta.env.VITE_USE_MOCK_OBSERVATIONS === 'true';
  const isCitizenReportsMock = import.meta.env.VITE_USE_MOCK_CITIZEN_REPORTS === 'true';
  const isFullyMock = isObservationsMock && isCitizenReportsMock;
  const isPartiallyMock = !isFullyMock && (isObservationsMock || isCitizenReportsMock);

  return (
    <header className="bg-slate-900/95 border-b border-slate-800 sticky top-0 z-50 backdrop-blur">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row items-center justify-between py-2.5 sm:py-0 sm:h-16 gap-2.5 sm:gap-0">
          
          {/* Brand & Subtitle */}
          <div className="flex items-center justify-between w-full sm:w-auto">
            <div className="flex items-center space-x-3">
              <div className="w-9 h-9 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 shrink-0">
                <Waves className="w-5 h-5" />
              </div>
              <div>
                <div className="flex items-center space-x-2">
                  <span className="font-bold text-lg text-white tracking-tight">RiverGuard</span>
                  <span className="bg-cyan-500/20 text-cyan-400 text-xs px-2 py-0.5 rounded-full border border-cyan-500/30 font-mono">
                    Ergene Basin
                  </span>
                </div>
                <p className="text-[11px] text-slate-400 hidden xs:block">
                  Integrated Environmental & Public Health Surveillance
                </p>
              </div>
            </div>

            {/* Mobile Status Badge (Only on very small screens)
                Öncelik sırası: tam mock > bağlantı koptu > kısmi mock > canlı > kontrol ediliyor.
                Eskiden apiOnline önce kontrol edildiği için mock modda bile "Live" yazıyordu
                (getLocations mock dalında da hata fırlatmadığı için apiOnline hep true oluyordu). */}
            <div className="sm:hidden">
              {isFullyMock ? (
                <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-cyan-500/10 text-cyan-400 border border-cyan-500/30">
                  <span className="w-1.5 h-1.5 rounded-full bg-cyan-400"></span> Demo
                </span>
              ) : apiOnline === false ? (
                <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-rose-500/10 text-rose-400 border border-rose-500/30">
                  <span className="w-1.5 h-1.5 rounded-full bg-rose-400"></span> Offline
                </span>
              ) : isPartiallyMock ? (
                <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-amber-500/10 text-amber-400 border border-amber-500/30">
                  <span className="w-1.5 h-1.5 rounded-full bg-amber-400"></span> Partial
                </span>
              ) : apiOnline === true ? (
                <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Live
                </span>
              ) : (
                <span className="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[10px] font-medium bg-slate-500/10 text-slate-400 border border-slate-500/30">
                  <span className="w-1.5 h-1.5 rounded-full bg-slate-400 animate-pulse"></span> ...
                </span>
              )}
            </div>
          </div>

          {/* Navigation Tabs - Responsive with smooth mobile scrolling */}
          <nav className="flex space-x-1.5 sm:space-x-2 w-full sm:w-auto overflow-x-auto pb-1 sm:pb-0 scrollbar-none">
            <button
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

            <button
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

            <button
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

          {/* Desktop Status Badge — aynı öncelik sırası, tam metinle */}
          <div className="hidden sm:flex items-center space-x-3 text-xs">
            {isFullyMock ? (
              <div className="flex items-center space-x-1.5 bg-cyan-500/10 text-cyan-400 border border-cyan-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-cyan-400"></span>
                <span>Mode: Research Benchmark</span>
              </div>
            ) : apiOnline === false ? (
              <div className="flex items-center space-x-1.5 bg-rose-500/10 text-rose-400 border border-rose-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-rose-400"></span>
                <span>API Unreachable</span>
              </div>
            ) : isPartiallyMock ? (
              <div className="flex items-center space-x-1.5 bg-amber-500/10 text-amber-400 border border-amber-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-amber-400"></span>
                <span>
                  Live API {isCitizenReportsMock ? '(Citizen Reports: Mock)' : '(Observations: Mock)'}
                </span>
              </div>
            ) : apiOnline === true ? (
              <div className="flex items-center space-x-1.5 bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
                <span>Live API / FHIR Connected</span>
              </div>
            ) : (
              <div className="flex items-center space-x-1.5 bg-slate-500/10 text-slate-400 border border-slate-500/30 px-2.5 py-1 rounded-full">
                <span className="w-2 h-2 rounded-full bg-slate-400 animate-pulse"></span>
                <span>Checking Connectivity...</span>
              </div>
            )}
          </div>

        </div>
      </div>
    </header>
  );
}