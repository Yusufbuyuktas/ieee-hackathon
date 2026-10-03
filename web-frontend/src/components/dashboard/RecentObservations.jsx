import React, { useState, useRef } from 'react';
import { 
  ShieldAlert, 
  CheckCircle, 
  EyeOff, 
  Bot, 
  Clock, 
  ChevronLeft, 
  ChevronRight,
  MapPin 
} from 'lucide-react';

const formatMediumName = (type = '') => {
  switch (type.toLowerCase()) {
    case 'surface_water':
      return 'Surface Water';
    case 'groundwater':
      return 'Groundwater';
    case 'sediment':
      return 'Sediment';
    default:
      return type.replace('_', ' ');
  }
};

export default function RecentObservations({ measurements = [], citizenReports = [], selectedParameter }) {
  const [viewType, setViewType] = useState('measurements'); // 'measurements' | 'citizen'
  const scrollContainerRef = useRef(null);

  const filteredMeasurements = measurements
    .filter(m => m.parameter === selectedParameter)
    .slice(0, 8);

  // Yatay kaydırma yöneticisi
  const scroll = (direction) => {
    if (scrollContainerRef.current) {
      const container = scrollContainerRef.current;
      const scrollAmount = container.clientWidth * 0.8;
      container.scrollBy({
        left: direction === 'left' ? -scrollAmount : scrollAmount,
        behavior: 'smooth'
      });
    }
  };

  return (
    <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 sm:p-5">
      
      {/* Üst Bar: Başlık, Sekmeler ve Kaydırma Kontrolleri */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4 pb-3 border-b border-slate-800">
        <div>
          <h3 className="text-sm sm:text-base font-semibold text-white">Basin Telemetry & Observation Feed</h3>
          <p className="text-xs text-slate-400 mt-0.5">Standardized laboratory telemetry and verified citizen science reports</p>
        </div>

        <div className="flex items-center gap-2 self-start sm:self-auto">
          {/* Görünüm Değiştirici */}
          <div className="flex bg-slate-800 p-1 rounded-lg border border-slate-700/60 shrink-0">
            <button
              onClick={() => setViewType('measurements')}
              className={`px-3 py-1 text-xs font-medium rounded-md transition-colors ${
                viewType === 'measurements'
                  ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              Telemetry Stations ({filteredMeasurements.length})
            </button>
            <button
              onClick={() => setViewType('citizen')}
              className={`px-3 py-1 text-xs font-medium rounded-md transition-colors ${
                viewType === 'citizen'
                  ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              Citizen Reports ({citizenReports.length})
            </button>
          </div>

          {/* Citizen Sekmesinde Çıkan Kaydırma Okları */}
          {viewType === 'citizen' && citizenReports.length > 0 && (
            <div className="flex items-center space-x-1 pl-1">
              <button
                onClick={() => scroll('left')}
                className="p-1.5 rounded-lg bg-slate-800 border border-slate-700 text-slate-400 hover:text-cyan-400 hover:border-slate-600 transition-colors"
                title="Scroll Left"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => scroll('right')}
                className="p-1.5 rounded-lg bg-slate-800 border border-slate-700 text-slate-400 hover:text-cyan-400 hover:border-slate-600 transition-colors"
                title="Scroll Right"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Laboratuvar Ölçümleri Tablosu */}
      {viewType === 'measurements' && (
        <div className="overflow-x-auto scrollbar-none">
          <table className="w-full text-left text-xs min-w-[540px]">
            <thead className="text-slate-400 border-b border-slate-800">
              <tr>
                <th className="pb-2 font-medium">Monitoring Station</th>
                <th className="pb-2 font-medium">Timestamp</th>
                <th className="pb-2 font-medium">Matrix</th>
                <th className="pb-2 font-medium">Observed Level</th>
                <th className="pb-2 font-medium">Regulatory Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {filteredMeasurements.map((m) => (
                <tr key={m.id} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-2.5 font-medium text-white">{m.location_name}</td>
                  <td className="py-2.5 text-slate-400 font-mono">{m.year || m.timestamp?.split('T')[0]}</td>
                  <td className="py-2.5 text-slate-300">{formatMediumName(m.sample_type)}</td>
                  <td className="py-2.5 font-mono">
                    {m.below_detection_limit ? (
                      <span className="text-amber-400 font-medium">&lt; Detection Limit</span>
                    ) : (
                      <span className={m.isExceeded ? "text-rose-400 font-bold" : "text-slate-200"}>
                        {m.value} {m.unit}
                      </span>
                    )}
                  </td>
                  <td className="py-2.5">
                    {m.below_detection_limit ? (
                      <span className="inline-flex items-center space-x-1 text-slate-400 bg-slate-800 px-2 py-0.5 rounded border border-slate-700">
                        <EyeOff className="w-3 h-3" />
                        <span>BDL</span>
                      </span>
                    ) : m.isExceeded ? (
                      <span className="inline-flex items-center space-x-1 text-rose-400 bg-rose-500/10 px-2 py-0.5 rounded border border-rose-500/30 font-medium">
                        <ShieldAlert className="w-3 h-3" />
                        <span>WHO Exceeded</span>
                      </span>
                    ) : (
                      <span className="inline-flex items-center space-x-1 text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/30">
                        <CheckCircle className="w-3 h-3" />
                        <span>Compliant</span>
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Yatay Kaydırmalı Yurttaş Bildirim Akışı (Maksimum 4'lü Görünüm) */}
      {viewType === 'citizen' && (
        citizenReports.length === 0 ? (
          <div className="py-10 text-center text-slate-500 text-xs font-mono">
            No citizen observations available.
          </div>
        ) : (
          <div
            ref={scrollContainerRef}
            className="flex gap-4 overflow-x-auto pb-3 pt-1 scroll-smooth snap-x snap-mandatory focus:outline-none [scrollbar-width:thin] [scrollbar-color:#334155_transparent]"
          >
            {citizenReports.map((report) => (
              <div 
                key={report.id} 
                className="shrink-0 w-[85%] sm:w-[calc(50%-8px)] lg:w-[calc(33.333%-11px)] xl:w-[calc(25%-12px)] snap-start bg-slate-950/70 border border-slate-800 rounded-xl p-3 flex flex-col justify-between hover:border-slate-700 transition-all space-y-3 shadow-md"
              >
                <div>
                  {/* Başlık ve Zaman */}
                  <div className="flex items-center justify-between mb-2 gap-2">
                    <span className="bg-cyan-500/10 text-cyan-400 text-[10px] font-semibold px-2 py-0.5 rounded-full border border-cyan-500/30 uppercase truncate">
                      {report.category_label || report.category}
                    </span>
                    <div className="flex items-center space-x-1 text-[10px] text-slate-400 font-mono shrink-0">
                      <Clock className="w-3 h-3" />
                      <span>{report.timestamp?.split('T')[1]?.substring(0, 5) || report.timestamp || ''}</span>
                    </div>
                  </div>

                  {/* Fotoğraf */}
                  <div className="relative h-28 rounded-lg overflow-hidden mb-2 bg-slate-900 border border-slate-800">
                    <img 
                      src={report.photo_url} 
                      alt="Observation" 
                      className="w-full h-full object-cover" 
                      onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = 'https://images.unsplash.com/photo-1617155093730-a8bf47be792d?auto=format&fit=crop&w=400&q=80';
                      }}
                    />
                    <div className="absolute bottom-1 right-1 bg-slate-900/90 text-emerald-400 border border-emerald-500/40 text-[9px] px-1.5 py-0.5 rounded flex items-center space-x-1 backdrop-blur-sm">
                      <Bot className="w-2.5 h-2.5" />
                      <span>{Math.round((report.ai_verification?.confidence ?? 0.85) * 100)}% AI</span>
                    </div>
                  </div>

                  <div className="flex items-center text-[11px] text-white font-medium space-x-1">
                    <MapPin className="w-3 h-3 text-cyan-400 shrink-0" />
                    <span className="truncate">{report.location_name}</span>
                  </div>

                  <p className="text-[11px] text-slate-300 mt-1.5 italic line-clamp-2 leading-relaxed bg-slate-900/50 p-2 rounded border border-slate-800/80">
                    "{report.note || 'No notes provided.'}"
                  </p>
                </div>

                {/* Alt Detay */}
                <div className="pt-2 border-t border-slate-800/80 flex items-center justify-between text-[10px] text-slate-400">
                  <span className="truncate font-mono">
                    {report.ai_verification?.model || 'Gemini Vision'}
                  </span>
                  <span className="text-cyan-400 font-mono shrink-0">Geotagged</span>
                </div>
              </div>
            ))}
          </div>
        )
      )}

    </div>
  );
}