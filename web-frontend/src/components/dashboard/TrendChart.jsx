import React, { useState, useMemo } from 'react';
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
import { THRESHOLDS } from '../../constants/apiContract';
import { SlidersHorizontal } from 'lucide-react';

const CustomTooltip = ({ active, payload }) => {
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
            <span className="font-mono font-bold text-cyan-400">{data.value} {data.unit}</span>
          </div>
        )}

        {data.threshold && (
          <div className="text-slate-400 mt-1 border-t border-slate-800 pt-1">
            DSÖ Su Limiti: <span className="font-mono text-rose-400">{data.threshold} mg/L</span>
          </div>
        )}

        {data.exceededStandards?.length > 0 && (
          <div className="text-rose-400 text-[10px] mt-1 font-mono">
            Aşılan Standartlar: {data.exceededStandards.join(', ')}
          </div>
        )}
      </div>
    );
  }
  return null;
};

export default function TrendChart({
  trendData,
  selectedParameter,
  onParameterChange,
  sampleType,
  onSampleTypeChange
}) {
  const [fitToThreshold, setFitToThreshold] = useState(true);
  const currentThreshold = THRESHOLDS[selectedParameter];
  const isWater = sampleType === 'surface_water' || sampleType === 'groundwater';

  const yDomain = useMemo(() => {
    if (!isWater || !currentThreshold?.who) return [0, 'auto'];
    if (!fitToThreshold) return [0, 'auto'];

    const numericValues = trendData
      .map(d => d.value)
      .filter(v => typeof v === 'number' && !isNaN(v));
    const maxVal = numericValues.length > 0 ? Math.max(...numericValues) : 0;
    const threshold = currentThreshold.who;

    const ceiling = Math.max(maxVal, threshold) * 1.15;
    return [0, Number(ceiling.toFixed(4))];
  }, [trendData, isWater, currentThreshold, fitToThreshold]);

  const getSeriesColor = () => {
    if (sampleType === 'surface_water') return '#06b6d4'; // Cyan
    if (sampleType === 'groundwater') return '#3b82f6';   // Mavi
    return '#f59e0b'; // Amber (Sediment)
  };

  return (
    <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-5">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h3 className="text-base font-semibold text-white">Konsantrasyon Trendi ve Eşik Karşılaştırması</h3>
          <p className="text-xs text-slate-400">
            {sampleType === 'surface_water' && "Nehir Yüzey Suyu Ölçümleri (mg/L)"}
            {sampleType === 'groundwater' && "Yeraltı Kuyu Suyu Ölçümleri (mg/L)"}
            {sampleType === 'sediment' && "Dip Çamuru / Sediment Ölçümleri (mg/kg)"}
          </p>
        </div>

        {/* Kontroller */}
        <div className="flex flex-wrap items-center gap-2">
          
          {/* 3'lü Ortam Filtresi */}
          <div className="flex bg-slate-800 p-0.5 rounded-lg border border-slate-700 text-xs">
            <button
              onClick={() => onSampleTypeChange('surface_water')}
              className={`px-2.5 py-1 rounded-md transition-colors ${
                sampleType === 'surface_water' ? 'bg-cyan-500/20 text-cyan-300 font-semibold border border-cyan-500/40' : 'text-slate-400 hover:text-white'
              }`}
            >
              Yüzeysel Su
            </button>
            <button
              onClick={() => onSampleTypeChange('groundwater')}
              className={`px-2.5 py-1 rounded-md transition-colors ${
                sampleType === 'groundwater' ? 'bg-blue-500/20 text-blue-300 font-semibold border border-blue-500/40' : 'text-slate-400 hover:text-white'
              }`}
            >
              Yeraltı Suyu
            </button>
            <button
              onClick={() => onSampleTypeChange('sediment')}
              className={`px-2.5 py-1 rounded-md transition-colors ${
                sampleType === 'sediment' ? 'bg-amber-500/20 text-amber-300 font-semibold border border-amber-500/40' : 'text-slate-400 hover:text-white'
              }`}
            >
              Sediment
            </button>
          </div>

          {/* Eşik Ölçeği Aç/Kapa Butonu */}
          {isWater && (
            <button
              onClick={() => setFitToThreshold(!fitToThreshold)}
              className={`flex items-center space-x-1.5 px-2.5 py-1 text-xs rounded-lg border transition-colors ${
                fitToThreshold
                  ? 'bg-rose-500/10 text-rose-300 border-rose-500/40'
                  : 'bg-slate-800 text-slate-400 border-slate-700 hover:text-slate-200'
              }`}
              title="DSÖ Eşik çizgisini kadraja dahil et veya veriye yakınlaş"
            >
              <SlidersHorizontal className="w-3.5 h-3.5" />
              <span>{fitToThreshold ? 'DSÖ Çizgisi: Açık' : 'Yakınlaştırılmış'}</span>
            </button>
          )}

          {/* 9 Metal Parametresi */}
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
          <AreaChart data={trendData} margin={{ top: 15, right: 25, left: -5, bottom: 0 }}>
            <defs>
              <linearGradient id="colorVal" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={getSeriesColor()} stopOpacity={0.4}/>
                <stop offset="95%" stopColor={getSeriesColor()} stopOpacity={0.0}/>
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
            <XAxis dataKey="label" stroke="#64748b" fontSize={11} tickLine={false} />
            <YAxis
              domain={yDomain}
              stroke="#64748b"
              fontSize={11}
              tickLine={false}
              unit={isWater ? " mg/L" : " mg/kg"}
            />
            <Tooltip content={<CustomTooltip />} />
            
            {/* Su ölçümlerinde DSÖ Referans Çizgisi */}
            {isWater && currentThreshold?.who && (
              <ReferenceLine
                y={currentThreshold.who}
                stroke="#f43f5e"
                strokeDasharray="4 4"
                strokeWidth={2}
                label={{
                  value: `DSÖ: ${currentThreshold.who} mg/L`,
                  fill: '#f43f5e',
                  fontSize: 11,
                  position: 'top',
                  offset: 5
                }}
              />
            )}

            <Area
              type="monotone"
              dataKey="value"
              stroke={getSeriesColor()}
              strokeWidth={2}
              fillOpacity={1}
              fill="url(#colorVal)"
              connectNulls={false}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}