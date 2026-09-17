import React, { useState } from 'react';
import { mockPatients } from '../../mock/mockPatients';
import { THRESHOLDS } from '../../constants/apiContract';
import {
  Stethoscope,
  User,
  MapPin,
  AlertTriangle,
  ShieldCheck,
  FileCode,
  CheckCircle2,
  Activity,
  X,
  ExternalLink
} from 'lucide-react';

export default function ClinicalDecisionSupport({ measurements }) {
  const [selectedPatient, setSelectedPatient] = useState(mockPatients[0]);
  const [showFhirModal, setShowFhirModal] = useState(false);

  // Hastanın yaşadığı ilçeye ait çevre ölçümlerini bul
  const regionalMeasurements = measurements.filter(m =>
    m.location_name.toLowerCase().includes(selectedPatient.district.toLowerCase())
  );

  // Arsenik ve şüpheli parametre maruziyetini analiz et
  const arsenicData = regionalMeasurements.find(m => m.parameter === 'arsenic' && !m.below_detection_limit);
  const isHighRisk = arsenicData && arsenicData.isExceeded;

  // Hackathon Raporundaki HL7 FHIR RiskAssessment Formatında JSON Üretici
  const generateFhirRiskAssessment = () => {
    return {
      resourceType: "RiskAssessment",
      id: `risk-${selectedPatient.id.toLowerCase()}-env`,
      status: "final",
      subject: {
        reference: `Patient/${selectedPatient.id}`,
        display: `${selectedPatient.name} (${selectedPatient.district})`
      },
      occurrenceDateTime: new Date().toISOString(),
      basis: [
        {
          reference: `Observation/${arsenicData ? arsenicData.id : 'obs-erg-corlu'}`,
          display: `Ergene Nehri ${selectedPatient.district} Su Kalite Ölçümü`
        }
      ],
      prediction: [
        {
          outcome: {
            text: "Kronik Arsenik ve Ağır Metal Maruziyeti Riski"
          },
          qualitativeRisk: {
            coding: [
              {
                system: "http://ourproject.org/fhir/risk",
                code: isHighRisk ? "high" : "low",
                display: isHighRisk ? "Yüksek Risk" : "Düşük / Güvenli"
              }
            ]
          },
          rationale: isHighRisk
            ? `Hastanın ikamet ettiği ${selectedPatient.district} bölgesindeki arsenik seviyesi (${arsenicData?.value} mg/L), DSÖ içme suyu sınırını (0.01 mg/L) aşmaktadır. Klinisyenin malignite ve toksisite açısından ileri tetkik yapması önerilir.`
            : "Bölgedeki ölçümler DSÖ ve ulusal sağlık standartları dahilindedir."
        }
      ]
    };
  };

  return (
    <div className="space-y-6">
      
      {/* Üst Bilgilendirme Banner'ı */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2">
            <Stethoscope className="w-5 h-5 text-rose-400" />
            <h2 className="text-base font-bold text-white tracking-wide">
              Klinik Karar Destek & Çevresel Maruziyet Paneli
            </h2>
            <span className="bg-rose-500/20 text-rose-300 border border-rose-500/40 text-[10px] px-2 py-0.5 rounded font-mono">
              One Health PoC
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Hastane klinik verileri ile Ergene Havzası HL7 FHIR çevresel gözlemleri entegre edilmiştir.
          </p>
        </div>

        {/* Hasta Seçim Butonları */}
        <div className="flex items-center space-x-2">
          <span className="text-xs text-slate-400 font-medium">Hasta:</span>
          {mockPatients.map((p) => (
            <button
              key={p.id}
              onClick={() => setSelectedPatient(p)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                selectedPatient.id === p.id
                  ? 'bg-cyan-500 text-slate-950 font-bold shadow-lg shadow-cyan-500/20'
                  : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-slate-700'
              }`}
            >
              {p.name} ({p.district})
            </button>
          ))}
        </div>
      </div>

      {/* İki Kolonlu PoC Sahnesi */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* SOL KOLON: Elektronik Sağlık Kaydı (EHR) */}
        <div className="lg:col-span-5 bg-slate-900/80 border border-slate-800 rounded-2xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-4">
              <div className="flex items-center space-x-3">
                <div className="w-10 h-10 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-300">
                  <User className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-white">{selectedPatient.name}</h3>
                  <p className="text-[11px] font-mono text-slate-400">{selectedPatient.id} • {selectedPatient.gender}, {selectedPatient.age} Yaş</p>
                </div>
              </div>
              <span className="text-[11px] bg-slate-800 text-slate-300 border border-slate-700 px-2 py-1 rounded">
                Aktif Poliklinik
              </span>
            </div>

            {/* İkamet ve Demografik Detaylar */}
            <div className="space-y-3 text-xs">
              <div className="flex items-center space-x-2 text-slate-300 bg-slate-800/50 p-2.5 rounded-xl border border-slate-800">
                <MapPin className="w-4 h-4 text-cyan-400 shrink-0" />
                <span>İkamet: <strong className="text-white">{selectedPatient.district}</strong> / {selectedPatient.neighborhood}</span>
              </div>

              <div>
                <label className="text-slate-400 font-medium block mb-1">Başvuru Şikayeti ve Anamnez:</label>
                <div className="bg-slate-950/60 p-3 rounded-xl border border-slate-800 text-slate-200 text-xs leading-relaxed">
                  "{selectedPatient.chief_complaint}"
                </div>
              </div>

              <div>
                <label className="text-slate-400 font-medium block mb-1">Klinik Ön Değerlendirme:</label>
                <div className="text-white font-medium bg-slate-800/40 p-2.5 rounded-xl border border-slate-700/50">
                  {selectedPatient.primary_concern}
                </div>
              </div>

              {/* Vital Bulgular */}
              <div className="grid grid-cols-3 gap-2 pt-2">
                <div className="bg-slate-950/40 p-2 rounded-lg border border-slate-800/80 text-center">
                  <div className="text-[10px] text-slate-400">Tansiyon</div>
                  <div className="font-mono text-white text-xs font-bold">{selectedPatient.vitals.blood_pressure}</div>
                </div>
                <div className="bg-slate-950/40 p-2 rounded-lg border border-slate-800/80 text-center">
                  <div className="text-[10px] text-slate-400">Nabız</div>
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
            <span>Kaynak: Trakya Balkan Onkoloji / Hastane EHR</span>
            <span className="text-cyan-400 font-mono">ID: {selectedPatient.id}</span>
          </div>
        </div>

        {/* SAĞ KOLON: Çevresel Maruziyet & Otomatik Risk Önerisi (One Health) */}
        <div className="lg:col-span-7 bg-slate-900/80 border border-slate-800 rounded-2xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-4">
              <div className="flex items-center space-x-2">
                <Activity className="w-5 h-5 text-cyan-400" />
                <h3 className="text-sm font-bold text-white">Bölgesel Çevresel Maruziyet Analizi</h3>
              </div>
              <span className="text-xs text-slate-400">
                Konum: <strong className="text-white">{selectedPatient.district} Havzası</strong>
              </span>
            </div>

            {/* Otomatik Risk Uyarı Kutusu */}
            {isHighRisk ? (
              <div className="bg-rose-950/40 border border-rose-500/50 rounded-xl p-4 mb-4 text-rose-200">
                <div className="flex items-start space-x-3">
                  <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-xs font-bold text-white uppercase tracking-wider">
                      Otomatik Hekim Uyarısı: Kronik Arsenik Maruziyeti
                    </h4>
                    <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                      Hastanın yaşadığı <strong className="text-white">{selectedPatient.district}</strong> bölgesinde ölçülen arsenik değeri (<span className="font-mono text-rose-300 font-bold">{arsenicData?.value} mg/L</span>), DSÖ içme suyu güvenli sınırının (<span className="font-mono">{THRESHOLDS.arsenic.who} mg/L</span>) üzerindedir. Bildirilen hiperkeratoz ve solunum şikayetleri kronik arsenik toksisitesiyle uyumludur.
                    </p>
                    <div className="mt-3 flex flex-wrap gap-2 text-[11px]">
                      <span className="bg-rose-500/20 text-rose-300 px-2 py-0.5 rounded border border-rose-500/40 font-mono">
                        Ölçüm: {arsenicData?.value} mg/L
                      </span>
                      <span className="bg-slate-800 text-slate-300 px-2 py-0.5 rounded border border-slate-700">
                        Metot: ICP-MS
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-emerald-950/30 border border-emerald-500/30 rounded-xl p-4 mb-4 text-emerald-200">
                <div className="flex items-start space-x-3">
                  <ShieldCheck className="w-5 h-5 text-emerald-400 shrink-0" />
                  <div>
                    <h4 className="text-xs font-bold text-white">Çevresel Ağır Metal Riski Saptanmadı</h4>
                    <p className="text-xs text-slate-300 mt-1">
                      {selectedPatient.district} bölgesindeki mevcut ölçümler DSÖ ve ulusal sınırların altındadır.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Bölgedeki Ölçüm Parametreleri Özeti */}
            <div className="bg-slate-950/50 p-3.5 rounded-xl border border-slate-800/80">
              <h4 className="text-xs font-semibold text-white mb-2">Bölgedeki Su Kalitesi Özet Tablosu</h4>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs">
                {regionalMeasurements.slice(0, 4).map((m) => (
                  <div key={m.id} className="bg-slate-900 p-2 rounded-lg border border-slate-800">
                    <div className="text-[10px] text-slate-400 uppercase">{m.parameter}</div>
                    <div className="font-mono text-white font-semibold">
                      {m.below_detection_limit ? 'BDL' : `${m.value} ${m.unit}`}
                    </div>
                    <div className="text-[10px] mt-1">
                      {m.isExceeded ? (
                        <span className="text-rose-400">Limit Üstü</span>
                      ) : (
                        <span className="text-emerald-400">Normal</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Aksiyon Barı & FHIR Butonu */}
          <div className="mt-5 pt-3 border-t border-slate-800 flex items-center justify-between">
            <span className="text-xs text-slate-400">HL7 FHIR R4 Standardı Karar Destek Çıktısı</span>
            <button
              onClick={() => setShowFhirModal(true)}
              className="flex items-center space-x-1.5 bg-indigo-600 hover:bg-indigo-500 text-white px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-colors shadow-lg shadow-indigo-600/20"
            >
              <FileCode className="w-4 h-4" />
              <span>FHIR RiskAssessment JSON İncele</span>
            </button>
          </div>
        </div>

      </div>

      {/* FHIR JSON Görüntüleme Modalı */}
      {showFhirModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-2xl w-full p-5 shadow-2xl flex flex-col max-h-[85vh]">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-3">
              <div className="flex items-center space-x-2">
                <FileCode className="w-5 h-5 text-indigo-400" />
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
              Aşağıdaki JSON, hastane sistemlerinin (EHR) doğrudan okuyabileceği uluslararası sağlık bilişimi standardında oluşturulmuştur:
            </p>

            <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 overflow-y-auto font-mono text-[11px] text-cyan-300 leading-relaxed flex-1">
              <pre>{JSON.stringify(generateFhirRiskAssessment(), null, 2)}</pre>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between">
              <span className="text-[11px] text-slate-400">Endpoint: /RiskAssessment?patient={selectedPatient.id}</span>
              <button
                onClick={() => setShowFhirModal(false)}
                className="bg-slate-800 hover:bg-slate-700 text-white px-4 py-1.5 rounded-lg text-xs font-medium"
              >
                Kapat
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}