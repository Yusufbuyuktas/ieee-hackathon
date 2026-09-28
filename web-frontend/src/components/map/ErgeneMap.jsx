import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import L from 'leaflet';
import { resolveCoordinates } from './mapHelpers';
import { THRESHOLDS } from '../../constants/apiContract';
import { Bot } from 'lucide-react';

function MapResizer() {
  const map = useMap();
  useEffect(() => {
    const timer = setTimeout(() => {
      map.invalidateSize();
    }, 150);
    return () => clearTimeout(timer);
  }, [map]);
  return null;
}

const createMarkerIcon = (type, isExceeded = false) => {
  let bgColor = '#10b981';
  let pulseColor = 'rgba(16, 185, 129, 0.4)';

  if (type === 'station') {
    if (isExceeded) {
      bgColor = '#f43f5e';
      pulseColor = 'rgba(244, 63, 94, 0.4)';
    }
  } else if (type === 'citizen') {
    bgColor = '#06b6d4';
    pulseColor = 'rgba(6, 182, 212, 0.4)';
  }

  return L.divIcon({
    className: 'custom-leaflet-marker',
    html: `
      <div style="position: relative; width: 22px; height: 22px; display: flex; align-items: center; justify-content: center;">
        <span style="position: absolute; width: 100%; height: 100%; border-radius: 50%; background: ${pulseColor};"></span>
        <span style="position: relative; width: 12px; height: 12px; border-radius: 50%; background: ${bgColor}; border: 2px solid #0f172a; box-shadow: 0 0 10px ${bgColor};"></span>
      </div>
    `,
    iconSize: [22, 22],
    iconAnchor: [11, 11],
    popupAnchor: [0, -11]
  });
};

const formatMediumLabel = (type) => {
  switch (type) {
    case 'surface_water':
      return 'Surface Water';
    case 'groundwater':
      return 'Groundwater';
    case 'sediment':
      return 'Sediment (Dry Weight)';
    default:
      return type?.replace('_', ' ') || 'Matrix';
  }
};

export default function ErgeneMap({ measurements, locations = [], citizenReports, selectedParameter, sampleType = 'all' }) {
  const [filter, setFilter] = useState('all');
  const currentThreshold = THRESHOLDS[selectedParameter];

  const stations = measurements
    .filter(m => m.parameter === selectedParameter)
    .filter(m => sampleType === 'all' ? true : m.sample_type === sampleType)
    .map(m => ({
      ...m,
      geo: resolveCoordinates(m, locations)
    }));

  const filteredStations = stations.filter(s => {
    if (filter === 'exceeded') return s.isExceeded;
    if (filter === 'citizen') return false;
    return true;
  });

  const showCitizen = filter === 'all' || filter === 'citizen';

  return (
    <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 sm:p-5 overflow-hidden">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 mb-4 pb-3 border-b border-slate-800">
        <div>
          <div className="flex items-center space-x-2">
            <h3 className="text-sm sm:text-base font-semibold text-white">Ergene Basin Spatial Surveillance</h3>
            <span className="text-[10px] bg-slate-800 text-cyan-400 border border-cyan-500/30 px-2 py-0.5 rounded font-mono">
              Thrace Region
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-0.5">
            Real-time telemetry stations correlated with AI-verified crowdsourced reports
          </p>
        </div>

        {/* Responsive Filter Buttons */}
        <div className="flex items-center space-x-1.5 bg-slate-800/80 p-1 rounded-lg border border-slate-700 overflow-x-auto scrollbar-none shrink-0 self-start md:self-auto">
          <button
            onClick={() => setFilter('all')}
            className={`px-2.5 py-1 text-xs rounded-md font-medium transition-colors shrink-0 ${
              filter === 'all' ? 'bg-slate-700 text-white' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            All Stations
          </button>
          <button
            onClick={() => setFilter('exceeded')}
            className={`px-2.5 py-1 text-xs rounded-md font-medium transition-colors shrink-0 ${
              filter === 'exceeded' ? 'bg-rose-500/20 text-rose-300 border border-rose-500/40' : 'text-slate-400 hover:text-rose-300'
            }`}
          >
            Exceedance Hotspots
          </button>
          <button
            onClick={() => setFilter('citizen')}
            className={`px-2.5 py-1 text-xs rounded-md font-medium transition-colors shrink-0 ${
              filter === 'citizen' ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40' : 'text-slate-400 hover:text-cyan-300'
            }`}
          >
            Citizen Reports
          </button>
        </div>
      </div>

      <div className="h-[380px] sm:h-[440px] w-full rounded-xl overflow-hidden border border-slate-800 relative z-10 bg-slate-950">
        <MapContainer
          center={[41.28, 27.55]}
          zoom={9}
          scrollWheelZoom={false}
          className="w-full h-full"
        >
          <MapResizer />

          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
            maxZoom={18}
          />

          {filteredStations.map((station) => (
            <Marker
              key={station.id}
              position={[station.geo.lat, station.geo.lon]}
              icon={createMarkerIcon('station', station.isExceeded)}
            >
              <Popup className="custom-dark-popup">
                <div className="p-2 text-slate-100 text-xs min-w-[210px]">
                  <div className="font-bold text-white mb-1">{station.location_name}</div>
                  
                  <div className="text-[10px] text-slate-400 mb-1">
                    Matrix: <span className="text-cyan-300 uppercase">{formatMediumLabel(station.sample_type)}</span>
                  </div>

                  <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-700">
                    <span className="text-slate-400">{currentThreshold?.label || station.parameter}:</span>
                    {station.below_detection_limit ? (
                      <span className="text-amber-400 font-mono">Below Detection Limit (BDL)</span>
                    ) : (
                      <span className={`font-mono font-bold ${station.isExceeded ? 'text-rose-400' : 'text-emerald-400'}`}>
                        {station.value} {station.unit}
                      </span>
                    )}
                  </div>

                  {station.exceededStandards?.length > 0 && (
                    <div className="mt-1.5 text-[10px] text-rose-300 font-mono">
                      Exceeded: {station.exceededStandards.join(', ')}
                    </div>
                  )}

                  <div className="mt-2 text-[10px]">
                    {station.isExceeded ? (
                      <span className="bg-rose-500/20 text-rose-300 border border-rose-500/40 px-2 py-0.5 rounded block text-center font-bold tracking-wide uppercase">
                        Critical (Exceedance)
                      </span>
                    ) : (
                      <span className="bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-2 py-0.5 rounded block text-center font-medium">
                        Within Safe Baseline
                      </span>
                    )}
                  </div>
                </div>
              </Popup>
            </Marker>
          ))}

          {showCitizen && citizenReports.map((report) => (
            <Marker
              key={report.id}
              position={[report.coordinates.lat, report.coordinates.lon]}
              icon={createMarkerIcon('citizen', false)}
            >
              <Popup className="custom-dark-popup">
                <div className="p-2 text-slate-100 text-xs min-w-[220px]">
                  <div className="flex items-center justify-between mb-1.5">
                    <span className="bg-cyan-500/20 text-cyan-300 text-[10px] font-bold px-2 py-0.5 rounded border border-cyan-500/40 uppercase">
                      {report.category_label}
                    </span>
                    <span className="text-[10px] text-slate-400 font-mono">
                      {report.timestamp.split('T')[1]?.substring(0, 5) || report.timestamp}
                    </span>
                  </div>

                  <div className="font-semibold text-white mb-1">{report.location_name}</div>
                  
                  {report.photo_url && (
                    <div className="relative h-24 rounded my-1.5 overflow-hidden border border-slate-700">
                      <img src={report.photo_url} alt="Field observation" className="w-full h-full object-cover" />
                      <div className="absolute bottom-1 right-1 bg-slate-900/90 text-emerald-400 text-[9px] px-1.5 py-0.5 rounded flex items-center space-x-1 border border-emerald-500/30">
                        <Bot className="w-2.5 h-2.5" />
                        <span>{Math.round(report.ai_verification.confidence * 100)}% AI-Verified</span>
                      </div>
                    </div>
                  )}

                  <p className="text-slate-300 italic text-[11px] mb-2 leading-relaxed">"{report.note}"</p>
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>

      {/* Map Legend */}
      <div className="flex flex-wrap items-center justify-between gap-3 text-xs text-slate-400 mt-3 pt-2">
        <div className="flex flex-wrap items-center gap-x-4 gap-y-2">
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-rose-500 border border-slate-900 inline-block"></span>
            <span>Threshold Exceeded (Telemetry Confirmed)</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 border border-slate-900 inline-block"></span>
            <span>Safe Baseline / BDL</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-cyan-400 border border-slate-900 inline-block"></span>
            <span>Citizen Report (AI-Verified)</span>
          </div>
        </div>
      </div>
    </div>
  );
}