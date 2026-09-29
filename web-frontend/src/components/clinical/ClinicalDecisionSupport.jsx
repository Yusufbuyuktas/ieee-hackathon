import React, { useState, useEffect, useRef } from 'react';
import { mockPatients } from '../../mock/mockPatients';
import { getRiskStatus, getRiskAssessments } from '../../services/apiService';
import {
  Stethoscope,
  User,
  MapPin,
  AlertTriangle,
  ShieldCheck,
  FileCode,
  Search,
  ChevronDown,
  X,
  Activity,
  Check,
  Loader2,
  AlertCircle
} from 'lucide-react';

export default function ClinicalDecisionSupport() {
  const [selectedPatient, setSelectedPatient] = useState(mockPatients[0]);
  const [searchTerm, setSearchTerm] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [showFhirModal, setShowFhirModal] = useState(false);
  const dropdownRef = useRef(null);

  // Live Backend Data States
  const [riskStatus, setRiskStatus] = useState(null);
  const [riskAssessments, setRiskAssessments] = useState([]);
  const [loading, setLoading] = useState(true);

  // Close dropdown on click outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Fetch contextual risk telemetry on patient change
  useEffect(() => {
    if (!selectedPatient?.location_name) return;

    setLoading(true);

    Promise.all([
      getRiskStatus(selectedPatient.location_name).catch(() => null),
      getRiskAssessments(selectedPatient.location_name).catch(() => [])
    ])
      .then(([statusData, assessmentsData]) => {
        setRiskStatus(statusData);
        setRiskAssessments(assessmentsData || []);
      })
      .finally(() => setLoading(false));
  }, [selectedPatient]);

  const filteredPatients = mockPatients.filter((p) => {
    const term = searchTerm.toLowerCase();
    return (
      p.name.toLowerCase().includes(term) ||
      p.id.toLowerCase().includes(term) ||
      p.district.toLowerCase().includes(term) ||
      p.location_name.toLowerCase().includes(term)
    );
  });

  // Risk Classification Logic
  const latestAssessment = riskAssessments[0] || null;
  const isHighRisk =
    riskStatus?.current_risk_level === 'high' ||
    latestAssessment?.risk_level === 'high' ||
    latestAssessment?.source_concluded_high_risk === true;

  // HL7 FHIR RiskAssessment JSON Generator
  const generateFhirRiskAssessment = () => {
    return {
      resourceType: "RiskAssessment",
      id: latestAssessment?.fhir_risk_assessment_id || latestAssessment?.id || `risk-${selectedPatient.id.toLowerCase()}`,
      status: "final",
      subject: {
        reference: `Patient/${selectedPatient.id}`,
        display: `${selectedPatient.name} (${selectedPatient.location_name})`
      },
      occurrenceDateTime: latestAssessment?.timestamp || new Date().toISOString(),
      basis: [
        {
          display: latestAssessment?.citation || `Environmental Monitoring Catchment: ${selectedPatient.location_name}`
        }
      ],
      prediction: [
        {
          outcome: {
            text: latestAssessment?.basis_note || (isHighRisk ? "High Environmental Heavy Metal Exposure Hazard" : "Acceptable Environmental Exposure Baseline")
          },
          qualitativeRisk: {
            coding: [
              {
                system: "http://terminology.hl7.org/CodeSystem/risk-probability",
                code: isHighRisk ? "high" : "low",
                display: isHighRisk ? "High Risk" : "Low Risk / Compliant"
              }
            ]
          },
          rationale: isHighRisk
            ? (riskStatus?.reason || `Total Hazard Index (Child THI: ${latestAssessment?.total_hazard_index?.child || '1.0+'}) exceeds toxicological safety threshold.`)
            : "Catchment telemetry and longitudinal health indices remain within regulatory baseline."
        }
      ]
    };
  };

  return (
    <div className="space-y-6">
      
      {/* Top Banner & Patient Selector */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-4 sm:p-5 flex flex-col md:flex-row md:items-center justify-between gap-4 relative z-30">
        <div>
          <div className="flex items-center space-x-2">
            <Stethoscope className="w-5 h-5 text-rose-400 shrink-0" />
            <h2 className="text-sm sm:text-base font-bold text-white tracking-wide">
              Clinical Decision Support & Environmental Exposure
            </h2>
            <span className="bg-rose-500/20 text-rose-300 border border-rose-500/40 text-[10px] px-2 py-0.5 rounded font-mono shrink-0">
              One Health PoC
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Correlating Electronic Health Records (EHR) with live Ergene Basin environmental surveillance telemetry
          </p>
        </div>

        {/* Patient Selection Dropdown */}
        <div className="relative w-full md:w-80 shrink-0" ref={dropdownRef}>
          <label className="block text-[11px] text-slate-400 mb-1 font-medium">Active Patient Record:</label>
          <button
            type="button"
            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
            className="w-full bg-slate-800/90 border border-slate-700 hover:border-slate-600 text-left px-3.5 py-2 rounded-xl flex items-center justify-between text-xs text-white transition-colors focus:outline-none focus:border-cyan-500 shadow-sm"
          >
            <div className="flex items-center space-x-2.5 truncate">
              <div className="w-6 h-6 rounded-full bg-cyan-500/20 text-cyan-400 flex items-center justify-center shrink-0">
                <User className="w-3.5 h-3.5" />
              </div>
              <div className="truncate">
                <span className="font-semibold text-white">{selectedPatient.name}</span>
                <span className="text-slate-400 text-[11px] ml-1.5 font-mono">({selectedPatient.district})</span>
              </div>
            </div>
            <ChevronDown className={`w-4 h-4 text-slate-400 shrink-0 transition-transform duration-200 ${isDropdownOpen ? 'rotate-180 text-cyan-400' : ''}`} />
          </button>

          {isDropdownOpen && (
            <div className="absolute right-0 mt-2 w-full md:w-96 bg-slate-900 border border-slate-700/80 rounded-xl shadow-2xl overflow-hidden z-50">
              <div className="p-2.5 border-b border-slate-800 bg-slate-950/60">
                <div className="relative">
                  <Search className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" />
                  <input
                    type="text"
                    placeholder="Search by name, ID, or district..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full bg-slate-800/80 border border-slate-700 rounded-lg pl-8 pr-3 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-cyan-500"
                    autoFocus
                  />
                </div>
              </div>

              <div className="max-h-64 overflow-y-auto divide-y divide-slate-800/60">
                {filteredPatients.map((p) => {
                  const isSelected = selectedPatient.id === p.id;
                  return (
                    <button
                      key={p.id}
                      type="button"
                      onClick={() => {
                        setSelectedPatient(p);
                        setIsDropdownOpen(false);
                        setSearchTerm('');
                      }}
                      className={`w-full p-3 text-left flex items-center justify-between hover:bg-slate-800/60 transition-colors text-xs ${
                        isSelected ? 'bg-cyan-500/10 border-l-2 border-cyan-500' : ''
                      }`}
                    >
                      <div className="space-y-0.5">
                        <div className="flex items-center space-x-2">
                          <span className="font-semibold text-white">{p.name}</span>
                          <span className="text-[10px] font-mono text-slate-400 bg-slate-800 px-1.5 py-0.5 rounded">
                            {p.id}
                          </span>
                        </div>
                        <div className="text-[11px] text-slate-400 flex items-center space-x-2">
                          <span>{p.gender}, {p.age} yrs</span>
                          <span>•</span>
                          <span className="text-cyan-400">{p.district}</span>
                        </div>
                      </div>
                      {isSelected && <Check className="w-4 h-4 text-cyan-400 shrink-0 ml-2" />}
                    </button>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Two-Column Clinical PoC Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* LEFT COLUMN: Patient EHR Summary Card */}
        <div className="lg:col-span-5 bg-slate-900/80 border border-slate-800 rounded-2xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-4">
              <div className="flex items-center space-x-3">
                <div className="w-10 h-10 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300 shrink-0">
                  <User className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-white">{selectedPatient.name}</h3>
                  <p className="text-[11px] font-mono text-slate-400">{selectedPatient.id} • {selectedPatient.gender}, {selectedPatient.age} yrs</p>
                </div>
              </div>
              <span className="text-[11px] bg-slate-800 text-slate-300 border border-slate-700 px-2 py-1 rounded">
                Active Encounter
              </span>
            </div>

            <div className="space-y-3 text-xs">
              <div className="space-y-1 bg-slate-800/50 p-2.5 rounded-xl border border-slate-800">
                <div className="flex items-center space-x-2 text-slate-300">
                  <MapPin className="w-4 h-4 text-cyan-400 shrink-0" />
                  <span>Residence: <strong className="text-white">{selectedPatient.district}</strong> / {selectedPatient.neighborhood}</span>
                </div>
                <div className="text-[10px] text-slate-400 pl-6 font-mono truncate" title={selectedPatient.location_name}>
                  Catchment Station: {selectedPatient.location_name}
                </div>
              </div>

              <div>
                <label className="text-slate-400 font-medium block mb-1">Chief Complaint & Exposure History:</label>
                <div className="bg-slate-950/60 p-3 rounded-xl border border-slate-800 text-slate-200 text-xs leading-relaxed">
                  "{selectedPatient.chief_complaint}"
                </div>
              </div>

              <div>
                <label className="text-slate-400 font-medium block mb-1">Provisional Clinical Assessment:</label>
                <div className="text-white font-medium bg-slate-800/40 p-2.5 rounded-xl border border-slate-700/50">
                  {selectedPatient.primary_concern}
                </div>
              </div>

              <div className="grid grid-cols-3 gap-2 pt-2">
                <div className="bg-slate-950/40 p-2 rounded-lg border border-slate-800/80 text-center">
                  <div className="text-[10px] text-slate-400">BP (mmHg)</div>
                  <div className="font-mono text-white text-xs font-bold">{selectedPatient.vitals.blood_pressure}</div>
                </div>
                <div className="bg-slate-950/40 p-2 rounded-lg border border-slate-800/80 text-center">
                  <div className="text-[10px] text-slate-400">Pulse (bpm)</div>
                  <div className="font-mono text-white text-xs font-bold">{selectedPatient.vitals.heart_rate}</div>
                </div>
                <div className="bg-slate-950/40 p-2 rounded-lg border border-slate-800/80 text-center">
                  <div className="text-[10px] text-slate-400">SpO2</div>
                  <div className="font-mono text-white text-xs font-bold">{selectedPatient.vitals.spO2}</div>
                </div>
              </div>
            </div>
          </div>

          <div className="mt-4 pt-3 border-t border-slate-800 text-[11px] text-slate-500 flex justify-between items-center">
            <span>Source: Regional Hospital EHR Registry</span>
            <span className="text-cyan-400 font-mono">Patient ID: {selectedPatient.id}</span>
          </div>
        </div>

        {/* RIGHT COLUMN: Real-Time Environmental Risk Correlation */}
        <div className="lg:col-span-7 bg-slate-900/80 border border-slate-800 rounded-2xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-4">
              <div className="flex items-center space-x-2">
                <Activity className="w-5 h-5 text-cyan-400 shrink-0" />
                <h3 className="text-sm font-bold text-white">Live Environmental Risk Evaluation</h3>
              </div>
              <span className="text-xs text-slate-400 font-mono">
                API Endpoint: /risk-status
              </span>
            </div>

            {loading ? (
              <div className="h-48 flex flex-col items-center justify-center space-y-2">
                <Loader2 className="w-6 h-6 text-cyan-400 animate-spin" />
                <span className="text-xs text-slate-400">Querying catchment toxicological telemetry...</span>
              </div>
            ) : isHighRisk ? (
              <div className="space-y-4">
                {/* 1. Chemical Threshold Exceedance Card */}
                {riskStatus && (
                  <div className="bg-rose-950/40 border border-rose-500/50 rounded-xl p-4 text-rose-200">
                    <div className="flex items-start space-x-3">
                      <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
                      <div>
                        <div className="flex flex-wrap items-center gap-2">
                          <h4 className="text-xs font-bold text-white uppercase tracking-wider">
                            Critical Exposure Hazard: {riskStatus.parameter?.toUpperCase()}
                          </h4>
                          <span className="bg-rose-500/20 text-rose-300 text-[10px] px-2 py-0.5 rounded border border-rose-500/40 font-mono">
                            {riskStatus.standard} Guideline Exceeded
                          </span>
                        </div>
                        <p className="text-xs text-slate-300 mt-1.5 leading-relaxed">
                          {riskStatus.reason}
                        </p>
                        <div className="mt-3 flex flex-wrap gap-2 text-[11px] font-mono">
                          <span className="bg-slate-900 text-rose-300 px-2 py-0.5 rounded border border-rose-500/40">
                            Observed: {riskStatus.value} {riskStatus.unit}
                          </span>
                          <span className="bg-slate-900 text-slate-300 px-2 py-0.5 rounded border border-slate-700">
                            Baseline: {riskStatus.threshold} {riskStatus.unit}
                          </span>
                          <span className="bg-slate-900 text-slate-400 px-2 py-0.5 rounded border border-slate-800">
                            Timestamp: {riskStatus.last_updated?.split('T')[0]}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {/* 2. Toxicological Risk Indices (USEPA Model) */}
                {latestAssessment && (
                  <div className="bg-slate-950/60 border border-indigo-500/30 rounded-xl p-4">
                    <div className="flex items-center justify-between mb-2">
                      <h4 className="text-xs font-bold text-indigo-300 flex items-center space-x-1.5">
                        <AlertCircle className="w-4 h-4 shrink-0" />
                        <span>Health Risk Assessment (USEPA Model)</span>
                      </h4>
                      <span className="text-[10px] bg-rose-500/20 text-rose-300 px-2 py-0.5 rounded border border-rose-500/40 font-mono">
                        HIGH RISK
                      </span>
                    </div>

                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 my-2 text-xs">
                      <div className="bg-slate-900 p-2 rounded-lg border border-slate-800">
                        <div className="text-[10px] text-slate-400">THI (Child)</div>
                        <div className="font-mono text-rose-400 font-bold text-sm">
                          {latestAssessment.total_hazard_index?.child?.toFixed(2)}
                        </div>
                        <div className="text-[9px] text-slate-500">Threshold &gt; 1.0</div>
                      </div>
                      <div className="bg-slate-900 p-2 rounded-lg border border-slate-800">
                        <div className="text-[10px] text-slate-400">THI (Adult)</div>
                        <div className="font-mono text-rose-400 font-bold text-sm">
                          {latestAssessment.total_hazard_index?.adult?.toFixed(2)}
                        </div>
                        <div className="text-[9px] text-slate-500">Threshold &gt; 1.0</div>
                      </div>
                      <div className="bg-slate-900 p-2 rounded-lg border border-slate-800">
                        <div className="text-[10px] text-slate-400">Carcinogenic (Child)</div>
                        <div className="font-mono text-amber-400 font-bold text-sm">
                          {latestAssessment.carcinogenic_risk?.child?.toExponential(2)}
                        </div>
                      </div>
                      <div className="bg-slate-900 p-2 rounded-lg border border-slate-800">
                        <div className="text-[10px] text-slate-400">Carcinogenic (Adult)</div>
                        <div className="font-mono text-amber-400 font-bold text-sm">
                          {latestAssessment.carcinogenic_risk?.adult?.toExponential(2)}
                        </div>
                      </div>
                    </div>

                    <p className="text-[11px] text-slate-400 italic mt-2 leading-relaxed">
                      "{latestAssessment.basis_note}"
                    </p>
                    <div className="text-[10px] text-slate-500 mt-1 font-mono">
                      Citation: {latestAssessment.citation}
                    </div>
                  </div>
                )}
              </div>
            ) : (
              /* Safe Baseline State */
              <div className="bg-emerald-950/30 border border-emerald-500/30 rounded-xl p-5 text-emerald-200">
                <div className="flex items-start space-x-3">
                  <ShieldCheck className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-xs font-bold text-white">No Toxicological Hazard Detected</h4>
                    <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                      Monitored telemetry and health risk indices (THI &lt; 1.0) within the patient's residential catchment area (<strong className="text-white">{selectedPatient.district} / {selectedPatient.location_name}</strong>) comply with international safety baselines.
                    </p>
                    {latestAssessment && (
                      <div className="mt-3 flex flex-wrap items-center gap-3 text-[11px] font-mono">
                        <span className="bg-slate-900 text-emerald-400 px-2 py-0.5 rounded border border-emerald-500/40">
                          THI Child: {latestAssessment.total_hazard_index?.child?.toFixed(2)} (Compliant)
                        </span>
                        <span className="bg-slate-900 text-emerald-400 px-2 py-0.5 rounded border border-emerald-500/40">
                          THI Adult: {latestAssessment.total_hazard_index?.adult?.toFixed(2)}
                        </span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Action Bar & FHIR Modal Trigger */}
          <div className="mt-5 pt-3 border-t border-slate-800 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <span className="text-xs text-slate-400">HL7 FHIR R4 RiskAssessment Protocol</span>
            <button
              onClick={() => setShowFhirModal(true)}
              className="flex items-center space-x-1.5 bg-indigo-600 hover:bg-indigo-500 text-white px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-colors shadow-lg shadow-indigo-600/20 shrink-0"
            >
              <FileCode className="w-4 h-4" />
              <span>Inspect FHIR RiskAssessment JSON</span>
            </button>
          </div>
        </div>

      </div>

      {/* FHIR JSON Modal */}
      {showFhirModal && (
        <div className="fixed inset-0 bg-black/75 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-2xl w-full p-4 sm:p-5 shadow-2xl flex flex-col max-h-[85vh]">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-3">
              <div className="flex items-center space-x-2">
                <FileCode className="w-5 h-5 text-indigo-400 shrink-0" />
                <h3 className="text-sm font-bold text-white">HL7 FHIR RiskAssessment Resource</h3>
              </div>
              <button
                onClick={() => setShowFhirModal(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg hover:bg-slate-800"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <p className="text-xs text-slate-400 mb-2">
              Standardized HL7 FHIR R4 payload linking catchment environmental telemetry to the patient EHR profile:
            </p>

            <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 overflow-y-auto font-mono text-[11px] text-cyan-300 leading-relaxed flex-1">
              <pre>{JSON.stringify(generateFhirRiskAssessment(), null, 2)}</pre>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between">
              <span className="text-[11px] text-slate-400 font-mono truncate">
                Patient ID: {selectedPatient.id} • Station: {selectedPatient.location_name}
              </span>
              <button
                onClick={() => setShowFhirModal(false)}
                className="bg-slate-800 hover:bg-slate-700 text-white px-4 py-1.5 rounded-lg text-xs font-medium shrink-0 ml-2"
              >
                Dismiss
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}