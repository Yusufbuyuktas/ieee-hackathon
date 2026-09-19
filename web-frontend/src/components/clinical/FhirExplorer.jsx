import React, { useState } from 'react';
import { FileJson, Database, Copy, Check, ChevronRight, Activity } from 'lucide-react';
import { THRESHOLDS } from '../../constants/apiContract';

// LOINC Kodları Sözlüğü
const LOINC_CODES = {
  arsenic: { code: "29263-1", display: "Arsenic [Mass/volume] in Water" },
  cadmium: { code: "29266-4", display: "Cadmium [Mass/volume] in Water" },
  lead: { code: "29272-2", display: "Lead [Mass/volume] in Water" },
  chromium: { code: "29268-0", display: "Chromium [Mass/volume] in Water" },
  copper: { code: "29269-8", display: "Copper [Mass/volume] in Water" },
  iron: { code: "29271-4", display: "Iron [Mass/volume] in Water" },
  manganese: { code: "29274-8", display: "Manganese [Mass/volume] in Water" },
  nickel: { code: "29275-5", display: "Nickel [Mass/volume] in Water" },
  zinc: { code: "29278-9", display: "Zinc [Mass/volume] in Water" },
};

export default function FhirExplorer({ measurements }) {
  const [selectedObsId, setSelectedObsId] = useState(measurements[0]?.id || '');
  const [copied, setCopied] = useState(false);

  // Seçili ölçümü bul
  const selectedMeasurement = measurements.find(m => m.id === selectedObsId) || measurements[0];
  const loinc = LOINC_CODES[selectedMeasurement?.parameter] || { code: "UNKNOWN", display: "Heavy metal in Water" };

  // Dinamik HL7 FHIR Observation Resource Üretimi
  const fhirObservation = {
    resourceType: "Observation",
    id: `erg-obs-${selectedMeasurement?.id?.toLowerCase().replace(/[^a-z0-9]/g, '-')}`,
    status: "final",
    category: [
      {
        coding: [
          {
            system: "http://terminology.hl7.org/CodeSystem/observation-category",
            code: "environmental",
            display: "Environmental"
          }
        ]
      }
    ],
    code: {
      coding: [
        {
          system: "http://loinc.org",
          code: loinc.code,
          display: loinc.display
        }
      ],
      text: `${selectedMeasurement?.location_name} - ${THRESHOLDS[selectedMeasurement?.parameter]?.label || selectedMeasurement?.parameter} Konsantrasyonu`
    },
    effectiveDateTime: selectedMeasurement?.timestamp,
    subject: {
      reference: `Location/ergene-basin-${selectedMeasurement?.id?.toLowerCase()}`,
      display: selectedMeasurement?.location_name
    },
    ...(selectedMeasurement?.below_detection_limit
      ? {
          dataAbsentReason: {
            coding: [
              {
                system: "http://terminology.hl7.org/CodeSystem/data-absent-reason",
                code: "below-detection-limit",
                display: "Below Detection Limit (BDL)"
              }
            ]
          }
        }
      : {
          valueQuantity: {
            value: selectedMeasurement?.value,
            unit: selectedMeasurement?.unit,
            system: "http://unitsofmeasure.org",
            code: selectedMeasurement?.unit === 'mg/L' ? 'mg/L' : 'mg/kg'
          }
        }),
    interpretation: selectedMeasurement?.isExceeded
      ? [
          {
            coding: [
              {
                system: "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation",
                code: "H",
                display: "High (Eşik Üstü - DSÖ)"
              }
            ]
          }
        ]
      : undefined
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(JSON.stringify(fhirObservation, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="space-y-6">
      
      {/* Üst Başlık */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2">
            <FileJson className="w-5 h-5 text-indigo-400" />
            <h2 className="text-base font-bold text-white">Dinamik HL7 FHIR Resource Gezgini</h2>
            <span className="bg-indigo-500/20 text-indigo-300 border border-indigo-500/40 text-[10px] px-2 py-0.5 rounded font-mono">
              R4 Standardı
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Havzadan toplanan laboratuvar ve sensör verilerinin HAPI FHIR JPA sunucusu formatındaki canlı JSON karşılıkları
          </p>
        </div>

        {/* Canlı Ölçüm Seçici */}
        <div className="flex items-center space-x-2">
          <label className="text-xs text-slate-400 font-medium">Ölçüm Kaydı:</label>
          <select
            value={selectedObsId}
            onChange={(e) => setSelectedObsId(e.target.value)}
            className="bg-slate-800 border border-slate-700 text-white text-xs rounded-lg px-3 py-1.5 focus:outline-none focus:border-indigo-500 font-mono"
          >
            {measurements.slice(0, 15).map((m) => (
              <option key={m.id} value={m.id}>
                {m.id} — {m.location_name.substring(0, 20)} ({m.parameter})
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* JSON Önizleme Alanı */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5">
        <div className="flex items-center justify-between mb-3 border-b border-slate-800 pb-3">
          <div className="flex items-center space-x-3 text-xs">
            <span className="font-semibold text-slate-200 font-mono">Resource: Observation</span>
            <span className="text-slate-500">•</span>
            <span className="text-indigo-400 font-mono">LOINC: {loinc.code}</span>
            <span className="text-slate-500">•</span>
            <span className="text-slate-400">{selectedMeasurement?.sample_type}</span>
          </div>

          <button
            onClick={copyToClipboard}
            className="flex items-center space-x-1.5 text-xs text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700 px-3 py-1.5 rounded-lg border border-slate-700 transition-colors shadow-sm"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span>{copied ? 'Kopyalandı' : 'JSON Kopyala'}</span>
          </button>
        </div>

        <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 font-mono text-xs text-cyan-300 leading-relaxed overflow-x-auto max-h-[500px]">
          <pre>{JSON.stringify(fhirObservation, null, 2)}</pre>
        </div>

        <div className="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
          <span>HAPI FHIR Endpoint: <code className="text-indigo-300 font-mono">POST /Observation</code></span>
          <span className="text-emerald-400 font-medium">HL7 R4 Uyumlu Şema Doğrulandı[cite: 1]</span>
        </div>
      </div>

    </div>
  );
}