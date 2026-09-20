import { apiGet } from './apiClient';
import { THRESHOLDS, evaluateRisk } from '../constants/apiContract';
import { mockCitizenReports } from '../mock/citizenReports';

// --- MOCK FALLBACK HAZIRLIĞI ---
let raw2013 = [];
let raw2025 = [];

try {
  raw2013 = (await import('../mock/ergene-2013-measurements.json')).default;
} catch (e) {
  // Mock dosya yoksa sessiz geç
}

try {
  raw2025 = (await import('../mock/ergene-2025-measurements.json')).default;
} catch (e) {
  // Mock dosya yoksa sessiz geç
}

function getMockMeasurements() {
  const combined = [
    ...(Array.isArray(raw2013) ? raw2013 : []),
    ...(Array.isArray(raw2025) ? raw2025 : [])
  ];

  return combined.map((item, index) => {
    const isBDL = item.below_detection_limit === true || item.value === null || item.value === undefined;
    const sampleType = item.sample_type || (item.unit === 'mg/kg' ? 'sediment' : 'surface_water');
    const risk = evaluateRisk(item.parameter, item.value, isBDL, sampleType);

    return {
      id: item.measurement_id || `MEAS-${index + 1}`,
      year: item.year || (item.timestamp ? new Date(item.timestamp).getFullYear() : 2025),
      timestamp: item.timestamp || `${item.year || 2025}-01-01T00:00:00Z`,
      location_name: item.location_name || "Ergene Havzası Ölçüm Noktası",
      coordinates: item.coordinates || null,
      parameter: item.parameter || 'arsenic',
      sample_type: sampleType,
      unit: item.unit || risk.unit,
      value: isBDL ? null : Number(item.value),
      below_detection_limit: isBDL,
      method: item.method || 'ICP-MS',
      isExceeded: risk.isExceeded,
      exceededStandards: [],
    };
  });
}

// --- 3a. BİLİNEN KONUMLARI LİSTELEME ---
export async function getLocations() {
  if (import.meta.env.VITE_USE_MOCK_OBSERVATIONS === 'true') {
    return [];
  }
  const data = await apiGet('/locations');
  return data.locations || [];
}

// --- 3b. GÖZLEMLERİ LİSTELEME (GERÇEK API / OBSERVATIONS) ---
export async function getAllMeasurements({ parameter, from, to } = {}) {
  if (import.meta.env.VITE_USE_MOCK_OBSERVATIONS === 'true') {
    return getMockMeasurements();
  }

  const data = await apiGet('/observations', { parameter, from, to });
  
  return (data.results || []).map((item) => ({
    id: item.id,
    location_name: item.location_name,
    coordinates: item.coordinates || null,
    timestamp: item.timestamp,
    year: item.timestamp ? new Date(item.timestamp).getFullYear() : 2025,
    parameter: item.parameter,
    value: item.below_detection_limit ? null : Number(item.value),
    unit: item.unit,
    below_detection_limit: item.below_detection_limit,
    sample_type: item.sample_type,
    source_type: item.source_type,
    // Risk değerlendirmesi backend'in risk_flagged alanından doğrudan alınır
    isExceeded: Boolean(item.risk_flagged),
    exceededStandards: item.exceeded_standards || [],
    method: item.method || 'ICP-MS'
  }));
}

// --- 3c. RİSK DURUMU SORGULAMA ---
export async function getRiskStatus(locationName) {
  if (!locationName) return null;
  return apiGet('/risk-status', { location: locationName });
}

// --- 3d. SAĞLIK RİSKİ DEĞERLENDİRMELERİ (LİTERATÜR CR / THI) ---
export async function getRiskAssessments(locationName) {
  const params = locationName ? { location: locationName } : {};
  const data = await apiGet('/risk-assessments', params);
  return data.results || [];
}

// --- 3e. YURTTAŞ BİLDİRİMLERİ (MOCK) ---
export const getCitizenReports = () => mockCitizenReports;

// --- YARDIMCI HESAPLAMA FONKSİYONLARI ---

/**
 * Dashboard KPI Metrikleri
 * Ölçüm listesi üzerinden eşik aşımlarını backend'in 'isExceeded' alanına göre sayar.
 */
export const getDashboardMetrics = (measurements = [], parameter = 'arsenic', sampleType = 'surface_water') => {
  // Geriye dönük uyumluluk: Eğer ilk parametre dizi değilse
  let targetMeasurements = Array.isArray(measurements) ? measurements : [];
  let targetParam = typeof measurements === 'string' ? measurements : parameter;
  let targetSample = typeof parameter === 'string' && typeof measurements === 'string' ? parameter : sampleType;

  const filtered = targetMeasurements.filter((m) =>
    m.parameter === targetParam &&
    (targetSample === 'all' ? true : m.sample_type === targetSample)
  );

  const validMeasurements = filtered.filter((m) => !m.below_detection_limit && m.value !== null);
  const bdlCount = filtered.filter((m) => m.below_detection_limit).length;
  const exceededCount = validMeasurements.filter((m) => m.isExceeded).length;

  const values = validMeasurements.map((m) => m.value);
  const maxValue = values.length > 0 ? Math.max(...values) : 0;
  const avgValue = values.length > 0 ? values.reduce((a, b) => a + b, 0) / values.length : 0;

  const currentThreshold = targetSample === 'sediment' ? null : (THRESHOLDS[targetParam]?.who || null);

  return {
    totalMeasurements: filtered.length,
    validMeasurementsCount: validMeasurements.length,
    bdlCount,
    exceededCount,
    maxValue: maxValue.toFixed(4),
    avgValue: avgValue.toFixed(4),
    threshold: currentThreshold,
    unit: targetSample === 'sediment' ? 'mg/kg' : 'mg/L',
    isCritical: exceededCount > 0,
    citizenReportsCount: mockCitizenReports.length,
    sampleType: targetSample
  };
};

/**
 * Trend Grafiği Veri Formatlayıcı
 */
export const getTrendData = (measurements = [], parameter = 'arsenic', sampleType = 'surface_water') => {
  let targetMeasurements = Array.isArray(measurements) ? measurements : [];
  let targetParam = typeof measurements === 'string' ? measurements : parameter;
  let targetSample = typeof parameter === 'string' && typeof measurements === 'string' ? parameter : sampleType;

  return targetMeasurements
    .filter((m) => m.parameter === targetParam && (targetSample === 'all' ? true : m.sample_type === targetSample))
    .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
    .map((m) => ({
      label: `${m.location_name.substring(0, 14)}.. (${m.year || (m.timestamp ? new Date(m.timestamp).getFullYear() : '')})`,
      location: m.location_name,
      year: m.year,
      date: m.timestamp ? m.timestamp.split('T')[0] : '',
      value: m.below_detection_limit ? null : m.value,
      isBDL: m.below_detection_limit,
      unit: m.unit,
      threshold: targetSample === 'sediment' ? null : (THRESHOLDS[targetParam]?.who || null),
      isExceeded: m.isExceeded,
      exceededStandards: m.exceededStandards || []
    }));
};