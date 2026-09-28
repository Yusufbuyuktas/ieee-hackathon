import React from 'react';
import { AlertTriangle, ShieldAlert, CheckCircle2 } from 'lucide-react';
import { THRESHOLDS } from '../../constants/apiContract';

export default function AlertBanner({ metrics, selectedParameter }) {
  const currentThreshold = THRESHOLDS[selectedParameter];

  if (!metrics.isCritical) {
    return (
      <div className="bg-emerald-950/30 border border-emerald-500/30 rounded-xl p-4 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-emerald-300">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-emerald-500/10 rounded-lg shrink-0">
            <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-white">Within Regulatory Limits</h4>
            <p className="text-xs text-slate-300">
              All monitored telemetry for <span className="text-white font-medium">{currentThreshold?.label || selectedParameter}</span> complies with international drinking water baselines.
            </p>
          </div>
        </div>
        <span className="text-xs font-mono bg-emerald-500/20 px-2.5 py-1 rounded text-emerald-300 border border-emerald-500/30 shrink-0 self-end sm:self-auto">
          Baseline: {currentThreshold?.who} {metrics.unit}
        </span>
      </div>
    );
  }

  return (
    <div className="bg-rose-950/40 border border-rose-500/40 rounded-xl p-4 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 text-rose-200 shadow-lg shadow-rose-950/20">
      <div className="flex items-start space-x-3">
        <div className="p-2.5 bg-rose-500/20 border border-rose-500/40 rounded-xl text-rose-400 shrink-0 mt-0.5 md:mt-0">
          <ShieldAlert className="w-6 h-6" />
        </div>
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <h4 className="text-sm font-bold text-white tracking-wide uppercase">Critical Public Health Alert</h4>
            <span className="bg-rose-500 text-white text-[10px] font-extrabold px-2 py-0.5 rounded-full uppercase tracking-wider">
              {metrics.exceededCount} Stations Non-Compliant
            </span>
          </div>
          <p className="text-xs text-slate-300 mt-1 max-w-2xl leading-relaxed">
            In the Ergene Basin, observed <span className="font-semibold text-white">{currentThreshold?.label || selectedParameter}</span> levels exceed the WHO drinking water guideline ({currentThreshold?.who} {metrics.unit}). Peak concentration: <span className="text-rose-400 font-bold font-mono">{metrics.maxValue} {metrics.unit}</span>. Early notification protocols triggered for regional health authorities.
          </p>
        </div>
      </div>
      <div className="flex items-center space-x-2 shrink-0 self-end md:self-auto border-t border-rose-900/50 pt-2 md:pt-0 md:border-0 w-full md:w-auto justify-end">
        <div className="text-right font-mono text-xs">
          <div className="text-slate-400 text-[11px]">WHO Benchmark</div>
          <div className="text-rose-400 font-bold">{currentThreshold?.who} {metrics.unit}</div>
        </div>
      </div>
    </div>
  );
}