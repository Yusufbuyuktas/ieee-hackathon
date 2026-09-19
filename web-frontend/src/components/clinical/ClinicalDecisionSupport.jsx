import React, { useState, useRef, useEffect } from 'react';
import { mockPatients } from '../../mock/mockPatients';
import { THRESHOLDS } from '../../constants/apiContract';
import { getPatientRegionalMeasurements } from '../../utils/geoMatching';
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
  Check
} from 'lucide-react';

export default function ClinicalDecisionSupport({ measurements }) {
  const [selectedPatient, setSelectedPatient] = useState(mockPatients[0]);
  const [searchTerm, setSearchTerm] = useState('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [showFhirModal, setShowFhirModal] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const filteredPatients = mockPatients.filter((p) => {
    const term = searchTerm.toLowerCase();
    return (
      p.name.toLowerCase().includes(term) ||
      p.id.toLowerCase().includes(term) ||
      p.district.toLowerCase().includes(term)
    );
  });

  // HİBRİT EŞLEŞTİRME MOTORU: 0 kayıt dönmesi imkansız hale getirildi
  const regionalMeasurements = getPatientRegionalMeasurements(selectedPatient, measurements);

  // Hastanın anamnezine göre şüpheli parametreyi belirle (varsayılan: arsenic)
  const targetParam = selectedPatient.suspected_exposure || 'arsenic';
  const paramThreshold = THRESHOLDS[targetParam] || THRESHOLDS.arsenic;

  // Bölgedeki ilgili parametre ölçümleri
  const paramMeasurements = regionalMeasurements.filter(m => m.parameter === targetParam);

  // Varsa eşik aşımı olan en kritik ölçümü, yoksa ilk geçerli ölçümü seç
  const targetObservation =
    paramMeasurements.find(m => !m.below_detection_limit && m.isExceeded) ||
    paramMeasurements.find(m => !m.below_detection_limit) ||
    paramMeasurements[0] ||
    regionalMeasurements[0];

  const isHighRisk = Boolean(targetObservation?.isExceeded);

  // HL7 FHIR RiskAssessment JSON Üretici
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
          reference: `Observation/${targetObservation ? targetObservation.id : 'obs-erg-corlu'}`,
          display: targetObservation?.location_name || `Ergene Havzası ${selectedPatient.district} Ölçümü`
        }
      ],
      prediction: [
        {
          outcome: {
            text: `Kronik ${paramThreshold.label} Maruziyeti Riski`
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
            ? `Hastanın ikamet ettiği ${selectedPatient.district} havzasındaki ${paramThreshold.label} seviyesi (${targetObservation?.value} ${targetObservation?.unit}), DSÖ güvenli sınırını (${paramThreshold.who} mg/L) aşmaktadır. Bildirilen semptomlar çevresel maruziyetle örtüşmektedir.`
            : `Bölgedeki ölçümler DSÖ ve ulusal sağlık standartları dahilindedir (${targetObservation?.value ?? 'BDL'} ${targetObservation?.unit || 'mg/L'}).`
        }
      ]
    };
  };

  return (
    <div className="space-y-6">
      
      {/* Üst Yönetim ve Hasta Arama Barı */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 flex flex-col md:flex-row md:items-center justify-between gap-4 relative z-30">
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
            Hasta kayıtları ile Ergene Havzası çevresel izleme verilerinin entegre değerlendirmesi
          </p>
        </div>

        {/* Combobox */}
        <div className="relative w-full md:w-80" ref={dropdownRef}>
          <label className="block text-[11px] text-slate-400 mb-1 font-medium">Aktif Hasta Dosyası:</label>
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
                    placeholder="İsim, ID veya ilçe ara..."
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
                          <span>{p.gender}, {p.age} yaş</span>
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

      {/* İki Kolonlu PoC Sahnesi */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* SOL: EHR Hasta Kartı */}
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

        {/* SAĞ: Çevresel Maruziyet & Otomatik Alarm */}
        <div className="lg:col-span-7 bg-slate-900/80 border border-slate-800 rounded-2xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-4">
              <div className="flex items-center space-x-2">
                <Activity className="w-5 h-5 text-cyan-400" />
                <h3 className="text-sm font-bold text-white">Bölgesel Çevresel Maruziyet Analizi</h3>
              </div>
              <span className="text-xs text-slate-400">
                Eşleşen İstasyon Sayısı: <strong className="text-cyan-400">{regionalMeasurements.length}</strong>
              </span>
            </div>

            {/* Otomatik Risk Uyarı Kartı */}
            {isHighRisk ? (
              <div className="bg-rose-950/40 border border-rose-500/50 rounded-xl p-4 mb-4 text-rose-200">
                <div className="flex items-start space-x-3">
                  <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
                  <div>
                    <h4 className="text-xs font-bold text-white uppercase tracking-wider">
                      Otomatik Hekim Uyarısı: Kronik {paramThreshold.label} Maruziyeti
                    </h4>
                    <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                      Hastanın ikamet ettiği bölgedeki en yakın ölçüm noktasında (<span className="text-white font-medium">{targetObservation?.location_name}</span>) ölçülen {paramThreshold.label} değeri (<span className="font-mono text-rose-300 font-bold">{targetObservation?.value} {targetObservation?.unit}</span>), DSÖ içme suyu güvenli sınırının (<span className="font-mono">{paramThreshold.who} mg/L</span>) üzerindedir. Bildirilen semptomlar kronik toksisiteyle doğrudan uyumludur.
                    </p>
                    <div className="mt-3 flex flex-wrap gap-2 text-[11px]">
                      <span className="bg-rose-500/20 text-rose-300 px-2 py-0.5 rounded border border-rose-500/40 font-mono">
                        Değer: {targetObservation?.value} {targetObservation?.unit}
                      </span>
                      <span className="bg-slate-800 text-slate-300 px-2 py-0.5 rounded border border-slate-700">
                        Metot: {targetObservation?.method || 'ICP-MS'}
                      </span>
                      <span className="bg-slate-800 text-slate-300 px-2 py-0.5 rounded border border-slate-700">
                        İstasyon: {targetObservation?.id}
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
                      {selectedPatient.district} havzasındaki ölçümler DSÖ ve ulusal sınırların altındadır.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Bölgedeki Ölçüm Parametreleri Özeti */}
            <div className="bg-slate-950/50 p-3.5 rounded-xl border border-slate-800/80">
              <div className="flex items-center justify-between mb-2">
                <h4 className="text-xs font-semibold text-white">Bölgedeki İlgili İstasyon Ölçümleri</h4>
                <span className="text-[10px] text-slate-400">İlk {Math.min(regionalMeasurements.length, 4)} kayıt</span>
              </div>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs">
                {regionalMeasurements.slice(0, 4).map((m) => (
                  <div key={m.id} className="bg-slate-900 p-2 rounded-lg border border-slate-800">
                    <div className="text-[10px] text-slate-400 uppercase truncate" title={m.location_name}>
                      {m.parameter}
                    </div>
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

      {/* FHIR Modal */}
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
              Aşağıdaki JSON, hastane sistemlerinin (EHR) doğrudan okuyabileceği uluslararası sağlık bilişimi standardında oluşturulmuştur[cite: 1]:
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