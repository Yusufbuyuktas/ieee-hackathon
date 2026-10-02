import { apiGet, apiPost, apiPatch } from './apiClient';
import { THRESHOLDS } from '../constants/apiContract';

// --- FOTOĞRAF URL ÇÖZÜMLEME YARDIMCISI (A4) ---
export function resolvePhotoUrl(photoUrl) {
  if (!photoUrl) return null;
  if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) return photoUrl;
  const base = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/api\/?$/, '');
  return `${base}${photoUrl.startsWith('/') ? '' : '/'}${photoUrl}`;
}

// --- KATEGORİ ETİKET DÖNÜŞTÜRÜCÜ ---
export const formatCategoryLabel = (cat = '') => {
  switch (cat.toLowerCase()) {
    case 'kirli_renk_degisimi':
      return 'Severe Water Discoloration';
    case 'balik_olumu':
      return 'Fish Mortality Event';
    case 'kotu_koku':
      return 'Noxious Chemical Odor';
    case 'bulanik':
      return 'High Turbidity';
    case 'kopuklenme':
      return 'Industrial Foam Accumulation';
    default:
      return cat.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase()) || 'Environmental Anomaly';
  }
};

// --- AUTH SERVİSLERİ (B3) ---
export async function login(email, password) {
  return apiPost('/auth/login', { email, password });
}

export async function logout() {
  return apiPost('/auth/logout', {});
}

export async function getMe() {
  return apiGet('/auth/me');
}

// --- BELEDİYE STATÜ GÜNCELLEME (B6) ---
export async function updateCitizenReportStatus(id, status) {
  return apiPatch(`/citizen-reports/${id}/status`, { status });
}

// --- 1. MONITORING STATIONS ENDPOINT ---
export async function getLocations() {
  const data = await apiGet('/locations');
  return data?.locations || [];
}

// --- 2. TELEMETRY OBSERVATIONS ENDPOINT (CANLI API) ---
export async function getAllMeasurements({ parameter, from, to } = {}) {
  const data = await apiGet('/observations', { parameter, from, to });
  
  return (data?.results || []).map((item) => ({
    id: item.id,
    location_name: item.location_name,
    coordinates: item.coordinates || null,
    timestamp: item.timestamp,
    year: item.timestamp ? new Date(item.timestamp).getFullYear() : 2025,
    parameter: item.parameter,
    value: item.below_detection_limit ? null : Number(item.value),
    unit: item.unit,
    below_detection_limit: Boolean(item.below_detection_limit),
    sample_type: item.sample_type,
    source_type: item.source_type,
    isExceeded: Boolean(item.risk_flagged),
    exceededStandards: item.exceeded_standards || [],
    method: item.method || 'ICP-MS'
  }));
}

// --- 3. TOXICOLOGICAL RISK STATUS QUERY ---
export async function getRiskStatus(locationName) {
  if (!locationName) return null;
  return apiGet('/risk-status', { location: locationName });
}

// --- 4. USEPA HEALTH RISK ASSESSMENTS ---
export async function getRiskAssessments(locationName) {
  const params = locationName ? { location: locationName } : {};
  const data = await apiGet('/risk-assessments', params);
  return data?.results || [];
}

// --- 5. CROWDSOURCED CITIZEN REPORTS (CANLI API) ---
export async function getCitizenReports() {
  try {
    const data = await apiGet('/citizen-reports');
    const rawList = data?.results || (Array.isArray(data) ? data : []);

    return rawList.map((item, index) => {
      const lat = item.latitude ?? 41.25;
      const lon = item.longitude ?? 27.50;
      const categoryStr = (item.category || 'diger').toString();

      return {
        id: item.id || `CIT-${index + 1}`,
        timestamp: item.timestamp || new Date().toISOString(),
        location_name: `Catchment Sector (${Number(lat).toFixed(3)}, ${Number(lon).toFixed(3)})`,
        coordinates: { lat: Number(lat), lon: Number(lon) },
        category: categoryStr.toLowerCase(),
        category_label: formatCategoryLabel(categoryStr),
        note: item.note || "Açıklama belirtilmedi.",
        photo_url: resolvePhotoUrl(item.photoUrl),
        raw_photo_url: item.photoUrl,
        ai_verification: {
          verified: item.aiValidationStatus === 'ONAYLANDI',
          confidence: item.aiConfidence ?? 0.85,
          model: item.aiModel || null, // Backend model alanını eklediğinde otomatik beslenir
          feedback: item.aiExplanation || "Yapay zeka görsel analizi tamamlandı."
        },
        ai_validation_status: item.aiValidationStatus || 'INCELEMEDE',
        status: item.aiValidationStatus || 'INCELEMEDE',
        ai_explanation: item.aiExplanation,
        fhir_observation_id: item.fhirObservationId
      };
    });
  } catch (err) {
    console.error("Canlı vatandaş bildirimleri alınamadı:", err);
    return [];
  }
}

// --- ANALİTİK HESAPLAMA YARDIMCILARI ---
export const getDashboardMetrics = (
  measurements = [],
  parameter = 'arsenic',
  sampleType = 'surface_water',
  citizenCount = 0
) => {
  const filtered = measurements.filter((m) =>
    m.parameter === parameter &&
    (sampleType === 'all' ? true : m.sample_type === sampleType)
  );

  const validMeasurements = filtered.filter((m) => !m.below_detection_limit && m.value !== null);
  const bdlCount = filtered.filter((m) => m.below_detection_limit).length;
  const exceededCount = validMeasurements.filter((m) => m.isExceeded).length;

  const values = validMeasurements.map((m) => m.value);
  const maxValue = values.length > 0 ? Math.max(...values) : 0;
  const avgValue = values.length > 0 ? values.reduce((a, b) => a + b, 0) / values.length : 0;
  const currentThreshold = sampleType === 'sediment' ? null : (THRESHOLDS[parameter]?.who || null);

  return {
    totalMeasurements: filtered.length,
    validMeasurementsCount: validMeasurements.length,
    bdlCount,
    exceededCount,
    maxValue: maxValue.toFixed(4),
    avgValue: avgValue.toFixed(4),
    threshold: currentThreshold,
    unit: sampleType === 'sediment' ? 'mg/kg' : 'mg/L',
    isCritical: exceededCount > 0,
    citizenReportsCount: citizenCount,
    sampleType
  };
};

export const getTrendData = (measurements = [], parameter = 'arsenic', sampleType = 'surface_water') => {
  return measurements
    .filter((m) => m.parameter === parameter && (sampleType === 'all' ? true : m.sample_type === sampleType))
    .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
    .map((m) => ({
      label: `${m.location_name?.substring(0, 14)}.. (${m.year || ''})`,
      location: m.location_name,
      year: m.year,
      date: m.timestamp ? m.timestamp.split('T')[0] : '',
      value: m.below_detection_limit ? null : m.value,
      isBDL: m.below_detection_limit,
      unit: m.unit,
      threshold: sampleType === 'sediment' ? null : (THRESHOLDS[parameter]?.who || null),
      isExceeded: m.isExceeded,
      exceededStandards: m.exceededStandards || []
    }));
};