import React, { useState } from 'react';
import { ShieldAlert, CheckCircle, EyeOff, Camera, Bot, Clock } from 'lucide-react';

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

export default function RecentObservations({ measurements, citizenReports, selectedParameter }) {
  const [viewType, setViewType] = useState('measurements'); // 'measurements' | 'citizen'

  const filteredMeasurements = measurements
    .filter(m => m.parameter === selectedParameter)
    .slice(0, 8);

  return (
    <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 sm:p-5">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4 pb-3 border-b border-slate-800">
        <div>
          <h3 className="text-sm sm:text-base font-semibold text-white">Basin Telemetry & Observation Feed</h3>
          <p className="text-xs text-slate-400 mt-0.5">Standardized laboratory telemetry and verified citizen science reports</p>
        </div>

        {/* View Toggle */}
        <div className="flex bg-slate-800 p-1 rounded-lg border border-slate-700/60 shrink-0 self-start sm:self-auto">
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
      </div>

      {/* Laboratory Measurements Table */}
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

      {/* Citizen Science Feed Cards */}
      {viewType === 'citizen' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {citizenReports.map((report) => (
            <div key={report.id} className="bg-slate-800/60 border border-slate-700/60 rounded-xl p-3 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-2">
                  <span className="bg-cyan-500/20 text-cyan-300 text-[10px] font-bold px-2 py-0.5 rounded-full border border-cyan-500/30 uppercase">
                    {report.category_label}
                  </span>
                  <div className="flex items-center space-x-1 text-[11px] text-slate-400 font-mono">
                    <Clock className="w-3 h-3" />
                    <span>{report.timestamp.split('T')[1]?.substring(0, 5) || report.timestamp}</span>
                  </div>
                </div>

                <div className="relative h-28 rounded-lg overflow-hidden mb-2 bg-slate-900 border border-slate-800">
                  <img src={report.photo_url} alt="Observation" className="w-full h-full object-cover" />
                  <div className="absolute bottom-1 right-1 bg-slate-900/90 text-emerald-400 border border-emerald-500/40 text-[10px] px-1.5 py-0.5 rounded flex items-center space-x-1">
                    <Bot className="w-2.5 h-2.5" />
                    <span>{Math.round(report.ai_verification.confidence * 100)}% AI-Verified</span>
                  </div>
                </div>

                <p className="text-xs text-white font-medium line-clamp-1">{report.location_name}</p>
                <p className="text-[11px] text-slate-300 mt-1 italic line-clamp-2 leading-relaxed">"{report.note}"</p>
              </div>

              <div className="mt-3 pt-2 border-t border-slate-700/50 flex items-center justify-between text-[10px] text-slate-400">
                <span>Model: {report.ai_verification.model}</span>
                <span className="text-cyan-400 font-mono">Geotagged (GPS)</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}