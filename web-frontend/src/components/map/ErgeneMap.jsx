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
        <span style="position: absolute; width: 100%; height: 100%; border-radius: 50%; background: ${pulseColor}; animation: ping 2s cubic-bezier(0, 0, 0.2, 1) infinite;"></span>
        <span style="position: relative; width: 12px; height: 12px; border-radius: 50%; background: ${bgColor}; border: 2px solid #0f172a; box-shadow: 0 0 10px ${bgColor};"></span>
      </div>
    `,
    iconSize: [22, 22],
    iconAnchor: [11, 11],
    popupAnchor: [0, -11]
  });
};

export default function ErgeneMap({ measurements, locations = [], citizenReports, selectedParameter }) {
  const [filter, setFilter] = useState('all');
  const currentThreshold = THRESHOLDS[selectedParameter];

  const stations = measurements
    .filter(m => m.parameter === selectedParameter)
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
    <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-5 overflow-hidden">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4 pb-3 border-b border-slate-800">
        <div>
          <div className="flex items-center space-x-2">
            <h3 className="text-base font-semibold text-white">Ergene Havzası Coğrafi İzleme</h3>
            <span className="text-[10px] bg-slate-800 text-cyan-400 border border-cyan-500/30 px-2 py-0.5 rounded font-mono">
              Trakya Bölgesi
            </span>
          </div>
          <p className="text-xs text-slate-400">İstasyon ölçümleri ve sahadan bildirilen yurttaş gözlemleri</p>
        </div>

        <div className="flex items-center space-x-1.5 bg-slate-800/80 p-1 rounded-lg border border-slate-700">
          <button
            onClick={() => setFilter('all')}
            className={`px-2.5 py-1 text-xs rounded-md font-medium transition-colors ${
              filter === 'all' ? 'bg-slate-700 text-white' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Tümü
          </button>
          <button
            onClick={() => setFilter('exceeded')}
            className={`px-2.5 py-1 text-xs rounded-md font-medium transition-colors ${
              filter === 'exceeded' ? 'bg-rose-500/20 text-rose-300 border border-rose-500/40' : 'text-slate-400 hover:text-rose-300'
            }`}
          >
            Yalnızca Riskli Noktalar
          </button>
          <button
            onClick={() => setFilter('citizen')}
            className={`px-2.5 py-1 text-xs rounded-md font-medium transition-colors ${
              filter === 'citizen' ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40' : 'text-slate-400 hover:text-cyan-300'
            }`}
          >
            Yurttaş Bildirimleri
          </button>
        </div>
      </div>

      <div className="h-[440px] w-full rounded-xl overflow-hidden border border-slate-800 relative z-10 bg-slate-950">
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
                    Ortam: <span className="text-cyan-300 uppercase">{station.sample_type}</span>
                  </div>

                  <div className="flex items-center justify-between mt-2 pt-2 border-t border-slate-700">
                    <span className="text-slate-400">{currentThreshold?.label || station.parameter}:</span>
                    {station.below_detection_limit ? (
                      <span className="text-amber-400 font-mono">Tespit Limiti Altı (BDL)</span>
                    ) : (
                      <span className={`font-mono font-bold ${station.isExceeded ? 'text-rose-400' : 'text-emerald-400'}`}>
                        {station.value} {station.unit}
                      </span>
                    )}
                  </div>

                  {station.exceededStandards?.length > 0 && (
                    <div className="mt-1.5 text-[10px] text-rose-300 font-mono">
                      Aşılan: {station.exceededStandards.join(', ')}
                    </div>
                  )}

                  <div className="mt-2 text-[10px]">
                    {station.isExceeded ? (
                      <span className="bg-rose-500/20 text-rose-300 border border-rose-500/40 px-2 py-0.5 rounded block text-center font-bold">
                        RİSKLİ (EŞİK AŞILDI)
                      </span>
                    ) : (
                      <span className="bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 px-2 py-0.5 rounded block text-center">
                        Güvenli Aralık
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
                    <span className="text-[10px] text-slate-400">
                      {report.timestamp.split('T')[1].substring(0, 5)}
                    </span>
                  </div>

                  <div className="font-semibold text-white mb-1">{report.location_name}</div>
                  
                  {report.photo_url && (
                    <div className="relative h-24 rounded my-1.5 overflow-hidden border border-slate-700">
                      <img src={report.photo_url} alt="Bildirim" className="w-full h-full object-cover" />
                      <div className="absolute bottom-1 right-1 bg-slate-900/90 text-emerald-400 text-[9px] px-1 py-0.5 rounded flex items-center space-x-1">
                        <Bot className="w-2.5 h-2.5" />
                        <span>%{Math.round(report.ai_verification.confidence * 100)} Doğrulandı</span>
                      </div>
                    </div>
                  )}

                  <p className="text-slate-300 italic text-[11px] mb-2">"{report.note}"</p>
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>

      {/* Lejant */}
      <div className="flex flex-wrap items-center justify-between gap-3 text-xs text-slate-400 mt-3 pt-2">
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-1.5">
            <span className="w-3 h-3 rounded-full bg-rose-500 border border-slate-900 inline-block"></span>
            <span>Eşik Aşan İstasyon (Backend Onaylı)</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-3 h-3 rounded-full bg-emerald-500 border border-slate-900 inline-block"></span>
            <span>Güvenli İstasyon / BDL</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-3 h-3 rounded-full bg-cyan-500 border border-slate-900 inline-block"></span>
            <span>Yurttaş Bildirimi (AI Doğrulanmış)</span>
          </div>
        </div>
      </div>
    </div>
  );
}