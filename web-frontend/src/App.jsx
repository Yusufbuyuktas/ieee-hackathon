import React, { useState, useEffect, useMemo } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import Header from './components/layout/Header';
import AlertBanner from './components/dashboard/AlertBanner';
import KpiCards from './components/dashboard/KpiCards';
import ErgeneMap from './components/map/ErgeneMap';
import TrendChart from './components/dashboard/TrendChart';
import RecentObservations from './components/dashboard/RecentObservations';
import CitizenReportsManager from './components/dashboard/CitizenReportsManager';
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

function DashboardContent() {
  const { user } = useAuth();
  const [view, setView] = useState('landing');
  const [activeTab, setActiveTab] = useState('monitoring');
  const [selectedParameter, setSelectedParameter] = useState('chromium');
  const [sampleType, setSampleType] = useState('surface_water');
  
  const [locations, setLocations] = useState([]);
  const [measurements, setMeasurements] = useState([]);
  const [allMeasurements, setAllMeasurements] = useState([]);
  const [citizenReports, setCitizenReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [apiOnline, setApiOnline] = useState(null);

  const handleLoginSuccess = (loggedInUser) => {
    if (loggedInUser?.role === 'DOCTOR') {
      setActiveTab('clinical');
    } else {
      setActiveTab('monitoring');
    }
    setView('app');
  };

  // Kullanıcı oturumu zaten açıksa (sayfa yenilendiğinde) doğrudan panele yönlendir
  useEffect(() => {
    if (user && view === 'landing') {
      if (user.role === 'DOCTOR') {
        setActiveTab('clinical');
      } else {
        setActiveTab('monitoring');
      }
      setView('app');
    }
  }, [user]);

  // 1. GENEL VERİLER: Yalnızca Dashboard'a girildiğinde (view === 'app') yüklenir
  useEffect(() => {
    if (view !== 'app') return;

    getLocations()
      .then(locs => {
        setLocations(locs);
        setApiOnline(true);
      })
      .catch(err => {
        console.error("Failed to load monitoring stations:", err);
        setApiOnline(false);
      });

    getAllMeasurements({})
      .then(data => setAllMeasurements(data))
      .catch(err => console.error("Failed to load comprehensive FHIR observations:", err));

    getCitizenReports()
      .then(reports => setCitizenReports(reports))
      .catch(err => console.error("Failed to load live citizen reports:", err));
  }, [view]);

  // 2. TELEMETRİ VERİLERİ: Dashboard'a girildiğinde VE parametre her değiştiğinde yüklenir
  useEffect(() => {
    if (view !== 'app') return;

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
  }, [selectedParameter, view]);

  const metrics = useMemo(() => 
    getDashboardMetrics(measurements, selectedParameter, sampleType, citizenReports.length),
    [measurements, selectedParameter, sampleType, citizenReports.length]
  );

  const trendData = useMemo(() => 
    getTrendData(measurements, selectedParameter, sampleType),
    [measurements, selectedParameter, sampleType]
  );

  if (view === 'landing') {
    return <LandingPage onLoginClick={() => setView('login')} />;
  }

  if (view === 'login') {
    return (
      <LoginPage 
        onSuccess={handleLoginSuccess} 
        onBackClick={() => setView('landing')} 
      />
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      <Header 
        activeTab={activeTab} 
        setActiveTab={setActiveTab} 
        apiOnline={apiOnline} 
        onLogout={() => setView('landing')} 
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {error && (
          <div className="mb-6 p-4 rounded-xl bg-rose-950/60 border border-rose-500/50 text-rose-200 text-xs">
            <strong>System Notice:</strong> {error} — Please verify backend services and network proxy connectivity.
          </div>
        )}

        {/* TAB 1: Environmental Surveillance & GIS (Municipality View) */}
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

                <CitizenReportsManager 
                  reports={citizenReports} 
                  onReportsUpdate={setCitizenReports} 
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

export default function App() {
  return (
    <AuthProvider>
      <DashboardContent />
    </AuthProvider>
  );
}