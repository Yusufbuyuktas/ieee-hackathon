import React, { useState } from 'react';
import { FileJson, Database, CheckCircle, Copy, Check } from 'lucide-react';

export default function FhirExplorer({ measurements }) {
  const [copied, setCopied] = useState(false);

  // Örnek bir Ergene Observation JSON'ı
  const sampleObservation = {
    resourceType: "Observation",
    id: "erg-obs-corlu-2025",
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
          code: "29263-1",
          display: "Arsenic [Mass/volume] in Water"
        }
      ],
      text: "Ergene Nehri Su Numunesi Arsenik Konsantrasyonu"
    },
    effectiveDateTime: "2025-08-18T10:00:00+03:00",
    valueQuantity: {
      value: 0.0152,
      unit: "mg/L",
      system: "http://unitsofmeasure.org",
      code: "mg/L"
    },
    subject: {
      reference: "Location/ergene-corlu",
      display: "Ergene Nehri Çorlu Deresi Mevkii"
    },
    interpretation: [
      {
        coding: [
          {
            system: "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation",
            code: "H",
            display: "High"
          }
        ]
      }
    ]
  };

  const copyToClipboard = () => {
    navigator.clipboard.writeText(JSON.stringify(sampleObservation, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="space-y-6">
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 flex items-center justify-between">
        <div>
          <div className="flex items-center space-x-2">
            <FileJson className="w-5 h-5 text-indigo-400" />
            <h2 className="text-base font-bold text-white">HL7 FHIR Standart Uyumluluk Gezgini</h2>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Toplanan çevresel verilerin HAPI FHIR R4 standardında saklanma şeması
          </p>
        </div>
        <div className="flex items-center space-x-2 bg-indigo-500/10 border border-indigo-500/30 text-indigo-300 text-xs px-3 py-1.5 rounded-lg">
          <Database className="w-4 h-4" />
          <span>HAPI FHIR JPA Server (v7.4)</span>
        </div>
      </div>

      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5">
        <div className="flex items-center justify-between mb-3">
          <span className="text-xs font-semibold text-slate-300 font-mono">Resource: Observation / Arsenic</span>
          <button
            onClick={copyToClipboard}
            className="flex items-center space-x-1 text-xs text-slate-400 hover:text-white bg-slate-800 px-2.5 py-1 rounded-md border border-slate-700 transition-colors"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span>{copied ? 'Kopyalandı' : 'JSON Kopyala'}</span>
          </button>
        </div>

        <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 font-mono text-xs text-indigo-300 leading-relaxed overflow-x-auto">
          <pre>{JSON.stringify(sampleObservation, null, 2)}</pre>
        </div>
      </div>
    </div>
  );
}