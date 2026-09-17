import React, { useState } from 'react';
import Header from './components/layout/Header';
import { ShieldAlert, Info } from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState('monitoring');

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      <Header activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {activeTab === 'monitoring' && (
          <div className="space-y-6">
            <div className="p-4 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <Info className="w-5 h-5 text-cyan-400" />
                <div>
                  <h3 className="text-sm font-medium text-white">Aşama 1 Hazırlığı: Çevresel İzleme Katmanı</h3>
                  <p className="text-xs text-slate-400">Gerçek literatür ölçümleri (2013-2025) ve BDL kuralları bir sonraki adımda yüklenecek.</p>
                </div>
              </div>
              <span className="text-xs bg-slate-800 text-slate-300 px-2.5 py-1 rounded border border-slate-700 font-mono">
                Mock Modu Aktif
              </span>
            </div>
          </div>
        )}

        {activeTab === 'clinical' && (
          <div className="p-8 text-center text-slate-400 bg-slate-900/50 rounded-2xl border border-slate-800/80">
            <h2 className="text-lg font-semibold text-white mb-2">Hekim Karar Destek Ekranı (PoC)</h2>
            <p className="text-sm">Hasta kayıtları ile Ergene çevresel maruziyet verilerinin eşleştirileceği alan.</p>
          </div>
        )}

        {activeTab === 'fhir' && (
          <div className="p-8 text-center text-slate-400 bg-slate-900/50 rounded-2xl border border-slate-800/80">
            <h2 className="text-lg font-semibold text-white mb-2">HL7 FHIR Resource Gezgini</h2>
            <p className="text-sm">HAPI FHIR sunucusuyla senkronize Observation ve RiskAssessment JSON çıktıları.</p>
          </div>
        )}
      </main>
    </div>
  );
}