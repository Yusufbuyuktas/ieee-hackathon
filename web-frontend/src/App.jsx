import React, { useState, useEffect, useMemo } from 'react';
import Header from './components/layout/Header';
import AlertBanner from './components/dashboard/AlertBanner';
import KpiCards from './components/dashboard/KpiCards';
import ErgeneMap from './components/map/ErgeneMap';
import TrendChart from './components/dashboard/TrendChart';
import RecentObservations from './components/dashboard/RecentObservations';
import ClinicalDecisionSupport from './components/clinical/ClinicalDecisionSupport';
import FhirExplorer from './components/clinical/FhirExplorer';
import {
  getLocations,
  getAllMeasurements,
  getDashboardMetrics,
  getTrendData,
  getCitizenReports
} from './services/apiService';
import { Loader2 } from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState('monitoring');
  const [selectedParameter, setSelectedParameter] = useState('chromium'); // Veri setinde zengin olan krom ile başlayalım
  const [sampleType, setSampleType] = useState('surface_water'); // 'surface_water' | 'groundwater' | 'sediment'
  
  const [locations, setLocations] = useState([]);
  const [measurements, setMeasurements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 1. Bilinen Konumları Tek Seferde Çek (GET /api/locations)
  useEffect(() => {
    getLocations()
      .then(locs => setLocations(locs))
      .catch(err => console.error("Konumlar yüklenemedi:", err));
  }, []);

  // 2. Seçili Parametreye Göre Gözlemleri Çek (GET /api/observations)
  useEffect(() => {
    setLoading(true);
    getAllMeasurements({ parameter: selectedParameter })
      .then(data => {
        setMeasurements(data);
        setError(null);
      })
      .catch(err => {
        console.error("Gözlem verileri çekilemedi:", err);
        setError("Backend servisinden veri alınamadı.");
      })
      .finally(() => setLoading(false));
  }, [selectedParameter]);

  // Metrikler ve Trend Verisi (Backend'in isExceeded alanına göre hesaplanır)
  const metrics = useMemo(() => 
    getDashboardMetrics(measurements, selectedParameter, sampleType),
    [measurements, selectedParameter, sampleType]
  );

  const trendData = useMemo(() => 
    getTrendData(measurements, selectedParameter, sampleType),
    [measurements, selectedParameter, sampleType]
  );

  const citizenReports = useMemo(() => getCitizenReports(), []);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      <Header activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        
        {/* Hata Durumu */}
        {error && (
          <div className="mb-6 p-4 rounded-xl bg-rose-950/60 border border-rose-500/50 text-rose-200 text-xs">
            <strong>Hata:</strong> {error} — Docker servislerinin ayakta olduğundan emin olun.
          </div>
        )}

        {/* SEKME 1: Çevresel İzleme & Harita */}
        {activeTab === 'monitoring' && (
          <div className="space-y-6">
            <AlertBanner metrics={metrics} selectedParameter={selectedParameter} />
            <KpiCards metrics={metrics} selectedParameter={selectedParameter} />

            {loading ? (
              <div className="h-64 rounded-xl bg-slate-900/50 border border-slate-800 flex flex-col items-center justify-center space-y-3">
                <Loader2 className="w-8 h-8 text-cyan-400 animate-spin" />
                <span className="text-xs text-slate-400 font-mono">Backend'den gözlem verileri çekiliyor...</span>
              </div>
            ) : (
              <>
                <ErgeneMap
                  measurements={measurements}
                  locations={locations}
                  citizenReports={citizenReports}
                  selectedParameter={selectedParameter}
                />

                <TrendChart
                  trendData={trendData}
                  selectedParameter={selectedParameter}
                  onParameterChange={setSelectedParameter}
                  sampleType={sampleType}
                  onSampleTypeChange={setSampleType}
                />

                <RecentObservations
                  measurements={measurements}
                  citizenReports={citizenReports}
                  selectedParameter={selectedParameter}
                />
              </>
            )}
          </div>
        )}

        {/* SEKME 2: Klinik Karar Destek Paneli (PoC) */}
        {activeTab === 'clinical' && (
          <ClinicalDecisionSupport measurements={measurements} />
        )}

        {/* SEKME 3: HL7 FHIR Standart Gezgini */}
        {activeTab === 'fhir' && (
          <FhirExplorer measurements={measurements} />
        )}

      </main>
    </div>
  );
}