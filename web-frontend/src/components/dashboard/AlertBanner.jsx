import React from 'react';
import { AlertTriangle, ShieldAlert, CheckCircle2 } from 'lucide-react';
import { THRESHOLDS } from '../../constants/apiContract';

export default function AlertBanner({ metrics, selectedParameter }) {
  const currentThreshold = THRESHOLDS[selectedParameter];

  if (!metrics.isCritical) {
    return (
      <div className="bg-emerald-950/30 border border-emerald-500/30 rounded-xl p-4 flex items-center justify-between text-emerald-300">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-emerald-500/10 rounded-lg">
            <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-white">Güvenli Düzey</h4>
            <p className="text-xs text-slate-300">
              Seçilen parametre ({currentThreshold?.label}) için ölçülen tüm değerler standartların altında.
            </p>
          </div>
        </div>
        <span className="text-xs font-mono bg-emerald-500/20 px-2.5 py-1 rounded text-emerald-300 border border-emerald-500/30">
          Eşik: {currentThreshold?.who} {metrics.unit}
        </span>
      </div>
    );
  }

  return (
    <div className="bg-rose-950/40 border border-rose-500/40 rounded-xl p-4 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 text-rose-200 shadow-lg shadow-rose-950/20">
      <div className="flex items-start space-x-3">
        <div className="p-2.5 bg-rose-500/20 border border-rose-500/40 rounded-xl text-rose-400">
          <ShieldAlert className="w-6 h-6 animate-pulse" />
        </div>
        <div>
          <div className="flex items-center space-x-2">
            <h4 className="text-sm font-bold text-white tracking-wide uppercase">Kritik Sağlık Riski Uyarısı</h4>
            <span className="bg-rose-500 text-white text-[10px] font-extrabold px-2 py-0.5 rounded-full uppercase tracking-wider">
              {metrics.exceededCount} İstasyon Eşik Üstü
            </span>
          </div>
          <p className="text-xs text-slate-300 mt-1 max-w-2xl leading-relaxed">
            Ergene Havzası'nda <span className="font-semibold text-white">{currentThreshold?.label}</span> konsantrasyonu DSÖ (WHO) limitini ({currentThreshold?.who} {metrics.unit}) aşmıştır. En yüksek ölçülen değer: <span className="text-rose-400 font-bold font-mono">{metrics.maxValue} {metrics.unit}</span>. Yerel yönetim ve sağlık kuruluşları için erken uyarı protokolü tetiklenmiştir.
          </p>
        </div>
      </div>
      <div className="flex items-center space-x-2 shrink-0">
        <div className="text-right font-mono text-xs">
          <div className="text-slate-400">DSÖ Güvenli Limit</div>
          <div className="text-rose-400 font-bold">{currentThreshold?.who} {metrics.unit}</div>
        </div>
      </div>
    </div>
  );
}