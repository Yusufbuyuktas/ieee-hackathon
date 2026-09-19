import React, { useState, useMemo } from 'react';
import Header from './components/layout/Header';
import AlertBanner from './components/dashboard/AlertBanner';
import KpiCards from './components/dashboard/KpiCards';
import ErgeneMap from './components/map/ErgeneMap';
import TrendChart from './components/dashboard/TrendChart';
import RecentObservations from './components/dashboard/RecentObservations';
import ClinicalDecisionSupport from './components/clinical/ClinicalDecisionSupport';
import FhirExplorer from './components/clinical/FhirExplorer';
import {
  getAllMeasurements,
  getDashboardMetrics,
  getTrendData,
  getCitizenReports
} from './services/apiService';

export default function App() {
  const [activeTab, setActiveTab] = useState('monitoring');
  const [selectedParameter, setSelectedParameter] = useState('arsenic');
  const [sampleType, setSampleType] = useState('surface_water'); // 'surface_water' | 'sediment'

  // Veri katmanı hesaplamaları (sampleType duyarlı)
  const metrics = useMemo(() => getDashboardMetrics(selectedParameter, sampleType), [selectedParameter, sampleType]);
  const trendData = useMemo(() => getTrendData(selectedParameter, sampleType), [selectedParameter, sampleType]);
  const measurements = useMemo(() => getAllMeasurements(), []);
  const citizenReports = useMemo(() => getCitizenReports(), []);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      <Header activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        
        {/* SEKME 1: Çevresel İzleme & Harita */}
        {activeTab === 'monitoring' && (
          <div className="space-y-6">
            <AlertBanner metrics={metrics} selectedParameter={selectedParameter} />
            <KpiCards metrics={metrics} selectedParameter={selectedParameter} />
            
            <ErgeneMap
              measurements={measurements}
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