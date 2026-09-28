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
  const [selectedParameter, setSelectedParameter] = useState('chromium'); // Default rich telemetry
  const [sampleType, setSampleType] = useState('surface_water'); // 'surface_water' | 'groundwater' | 'sediment'
  
  const [locations, setLocations] = useState([]);
  const [measurements, setMeasurements] = useState([]); // Surveillance tab: filtered by selected parameter
  const [allMeasurements, setAllMeasurements] = useState([]); // FHIR Explorer: complete dataset across all 9 heavy metals
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [apiOnline, setApiOnline] = useState(null); // null: checking, true: connected, false: unreachable

  // 1. Fetch monitoring stations (GET /api/locations) — acts as API health check
  useEffect(() => {
    getLocations()
      .then(locs => {
        setLocations(locs);
        setApiOnline(true);
      })
      .catch(err => {
        console.error("Failed to load monitoring stations:", err);
        setApiOnline(false);
      });
  }, []);

  // 2. Fetch observations filtered by active parameter (GET /api/observations)
  useEffect(() => {
    setLoading(true);
    getAllMeasurements({ parameter: selectedParameter })
      .then(data => {
        setMeasurements(data);
        setError(null);
        setApiOnline(true);
      })
      .catch(err => {
        console.error("Failed to fetch observation telemetry:", err);
        setError("Unable to retrieve live telemetry from backend server.");
        setApiOnline(false);
      })
      .finally(() => setLoading(false));
  }, [selectedParameter]);

  // 3. Fetch comprehensive multi-metal dataset for HL7 FHIR Explorer
  useEffect(() => {
    getAllMeasurements({})
      .then(data => setAllMeasurements(data))
      .catch(err => console.error("Failed to load comprehensive FHIR observations:", err));
  }, []);

  // Metrics and longitudinal trends calculated against regulatory benchmarks
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
      <Header activeTab={activeTab} setActiveTab={setActiveTab} apiOnline={apiOnline} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        
        {/* Error Notification */}
        {error && (
          <div className="mb-6 p-4 rounded-xl bg-rose-950/60 border border-rose-500/50 text-rose-200 text-xs">
            <strong>System Notice:</strong> {error} — Please verify backend services and network proxy connectivity.
          </div>
        )}

        {/* TAB 1: Environmental Surveillance & GIS */}
        {activeTab === 'monitoring' && (
          <div className="space-y-6">
            <AlertBanner metrics={metrics} selectedParameter={selectedParameter} />
            <KpiCards metrics={metrics} selectedParameter={selectedParameter} />

            {loading ? (
              <div className="h-64 rounded-xl bg-slate-900/50 border border-slate-800 flex flex-col items-center justify-center space-y-3">
                <Loader2 className="w-8 h-8 text-cyan-400 animate-spin" />
                <span className="text-xs text-slate-400 font-mono">Loading telemetry stream from backend...</span>
              </div>
            ) : (
              <>
                <ErgeneMap
                  measurements={measurements}
                  locations={locations}
                  citizenReports={citizenReports}
                  selectedParameter={selectedParameter}
                  sampleType={sampleType}
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

        {/* TAB 2: Clinical Decision Support (CDS) */}
        {activeTab === 'clinical' && (
          <ClinicalDecisionSupport />
        )}

        {/* TAB 3: HL7 FHIR Standard Explorer */}
        {activeTab === 'fhir' && (
          <FhirExplorer measurements={allMeasurements} />
        )}

      </main>
    </div>
  );
}