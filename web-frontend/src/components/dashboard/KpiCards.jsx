import React from 'react';
import { Waves, AlertOctagon, EyeOff, Users } from 'lucide-react';
import { THRESHOLDS } from '../../constants/apiContract';

export default function KpiCards({ metrics, selectedParameter }) {
  const currentThreshold = THRESHOLDS[selectedParameter];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      
      {/* 1. En Yüksek Değer */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 relative overflow-hidden">
        <div className="flex items-center justify-between text-slate-400 mb-2">
          <span className="text-xs font-medium uppercase tracking-wider">Maksimum Konsantrasyon</span>
          <Waves className="w-4 h-4 text-cyan-400" />
        </div>
        <div className="flex items-baseline space-x-2">
          <span className="text-2xl font-bold font-mono text-white">{metrics.maxValue}</span>
          <span className="text-xs text-slate-400 font-mono">{metrics.unit}</span>
        </div>
        <p className="text-[11px] text-slate-400 mt-2">
          {currentThreshold?.label} için DSÖ sınırı: <span className="text-slate-200 font-mono">{currentThreshold?.who} {metrics.unit}</span>
        </p>
      </div>

      {/* 2. Eşik Aşımı */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 relative overflow-hidden">
        <div className="flex items-center justify-between text-slate-400 mb-2">
          <span className="text-xs font-medium uppercase tracking-wider">Eşik Aşan İstasyon</span>
          <AlertOctagon className="w-4 h-4 text-rose-400" />
        </div>
        <div className="flex items-baseline space-x-2">
          <span className="text-2xl font-bold font-mono text-rose-400">{metrics.exceededCount}</span>
          <span className="text-xs text-slate-400">/ {metrics.validMeasurementsCount} ölçüm</span>
        </div>
        <p className="text-[11px] text-rose-300/80 mt-2">
          DSÖ ve TS 2005 standartlarına göre riskli seviye
        </p>
      </div>

      {/* 3. BDL Kayıtları */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 relative overflow-hidden">
        <div className="flex items-center justify-between text-slate-400 mb-2">
          <span className="text-xs font-medium uppercase tracking-wider">Tespit Limiti Altı (BDL)</span>
          <EyeOff className="w-4 h-4 text-amber-400" />
        </div>
        <div className="flex items-baseline space-x-2">
          <span className="text-2xl font-bold font-mono text-amber-300">{metrics.bdlCount}</span>
          <span className="text-xs text-slate-400 font-mono">ölçüm</span>
        </div>
        <p className="text-[11px] text-slate-400 mt-2">
          Cihaz hassasiyet sınırı altındaki temiz/iz kayıtlar
        </p>
      </div>

      {/* 4. Yurttaş Bilimi Bildirimleri */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-xl p-4 relative overflow-hidden">
        <div className="flex items-center justify-between text-slate-400 mb-2">
          <span className="text-xs font-medium uppercase tracking-wider">Yurttaş Gözlemleri</span>
          <Users className="w-4 h-4 text-cyan-400" />
        </div>
        <div className="flex items-baseline space-x-2">
          <span className="text-2xl font-bold font-mono text-cyan-300">{metrics.citizenReportsCount}</span>
          <span className="text-xs text-emerald-400 font-medium">AI Doğrulanmış</span>
        </div>
        <p className="text-[11px] text-slate-400 mt-2">
          Mobil uygulamadan gelen saha gözlemleri
        </p>
      </div>

    </div>
  );
}