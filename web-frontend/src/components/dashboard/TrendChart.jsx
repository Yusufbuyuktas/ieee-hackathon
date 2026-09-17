import React from 'react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ReferenceLine
} from 'recharts';
import { THRESHOLDS, PARAMETERS } from '../../constants/apiContract';

// Özel Tooltip - BDL durumunu ve değerleri açıkça yazar
const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <div className="bg-slate-900 border border-slate-700 p-3 rounded-lg shadow-xl text-xs">
        <div className="font-semibold text-white mb-1">{data.location}</div>
        <div className="text-slate-400 mb-1">Tarih / Yıl: <span className="text-slate-200">{data.date || data.year}</span></div>
        
        {data.isBDL ? (
          <div className="text-amber-400 font-medium">Tespit Limiti Altında (BDL)</div>
        ) : (
          <div className="flex items-center space-x-2">
            <span className="text-slate-400">Konsantrasyon:</span>
            <span className="font-mono font-bold text-cyan-400">{data.value} mg/L</span>
          </div>
        )}

        <div className="text-slate-400 mt-1 border-t border-slate-800 pt-1">
          DSÖ Güvenli Eşik: <span className="font-mono text-rose-400">{data.threshold} mg/L</span>
        </div>
      </div>
    );
  }
  return null;
};

export default function TrendChart({ trendData, selectedParameter, onParameterChange }) {
  const currentThreshold = THRESHOLDS[selectedParameter];

  return (
    <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-5">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h3 className="text-base font-semibold text-white">Konsantrasyon Trendi ve Eşik Karşılaştırması</h3>
          <p className="text-xs text-slate-400">Ergene Havzası istasyonları boyunca ölçüm seviyeleri ve DSÖ referans çizgisi</p>
        </div>

        {/* 9 Metal Parametresi Seçici */}
        <div className="flex items-center space-x-2">
          <label className="text-xs text-slate-400 font-medium">Parametre:</label>
          <select
            value={selectedParameter}
            onChange={(e) => onParameterChange(e.target.value)}
            className="bg-slate-800 border border-slate-700 text-white text-xs rounded-lg px-3 py-1.5 focus:outline-none focus:border-cyan-500 font-medium"
          >
            {Object.entries(THRESHOLDS).map(([key, item]) => (
              <option key={key} value={key}>
                {item.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="h-72 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={trendData} margin={{ top: 10, right: 20, left: -10, bottom: 0 }}>
            <defs>
              <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.4}/>
                <stop offset="95%" stopColor="#06b6d4" stopOpacity={0.0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
            <XAxis
              dataKey="label"
              stroke="#64748b"
              fontSize={11}
              tickLine={false}
              interval="preserveStartEnd"
            />
            <YAxis
              stroke="#64748b"
              fontSize={11}
              tickLine={false}
              unit=" mg/L"
            />
            <Tooltip content={<CustomTooltip />} />
            
            {/* DSÖ Eşik Çizgisi */}
            <ReferenceLine
              y={currentThreshold?.who}
              stroke="#f43f5e"
              strokeDasharray="4 4"
              strokeWidth={2}
              label={{
                value: `DSÖ Limiti (${currentThreshold?.who} mg/L)`,
                fill: '#f43f5e',
                fontSize: 11,
                position: 'top'
              }}
            />

            <Area
              type="monotone"
              dataKey="value"
              stroke="#06b6d4"
              strokeWidth={2}
              fillOpacity={1}
              fill="url(#colorValue)"
              connectNulls={true}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>

      <div className="flex items-center justify-between text-xs text-slate-400 mt-4 border-t border-slate-800/80 pt-3">
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-1.5">
            <span className="w-3 h-3 rounded-full bg-cyan-500 inline-block"></span>
            <span>Ölçülen Değer</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <span className="w-3 h-0.5 bg-rose-500 inline-block"></span>
            <span>DSÖ Rehber Değeri</span>
          </div>
        </div>
        <span className="text-[11px] text-slate-500 italic">* BDL (Tespit Limiti Altı) veriler 0 kabul edilmez; grafikte boşluk olarak işlenir.</span>
      </div>
    </div>
  );
}