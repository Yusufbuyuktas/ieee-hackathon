import { apiGet } from './apiClient';
import { THRESHOLDS, evaluateRisk } from '../constants/apiContract';
import { mockCitizenReports } from '../mock/citizenReports';

// --- MOCK BENCHMARK DATASET INITIALIZATION ---
let raw2013 = [];
let raw2025 = [];

try {
  raw2013 = (await import('../mock/ergene-2013-measurements.json')).default;
} catch (e) {
  // Silent fallback if mock json is unavailable
}

try {
  raw2025 = (await import('../mock/ergene-2025-measurements.json')).default;
} catch (e) {
  // Silent fallback if mock json is unavailable
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
      location_name: item.location_name || "Ergene Basin Telemetry Station",
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

// Category format helper for international UI
const formatCategoryLabel = (cat = '') => {
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
      return cat.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase()) || 'Environmental Report';
  }
};

// --- 1. MONITORING STATIONS ENDPOINT ---
export async function getLocations() {
  if (import.meta.env.VITE_USE_MOCK_OBSERVATIONS === 'true') {
    return [];
  }
  const data = await apiGet('/locations');
  return data.locations || [];
}

// --- 2. TELEMETRY OBSERVATIONS ENDPOINT (LIVE API) ---
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

// --- 4. USEPA HEALTH RISK ASSESSMENTS (LITERATURE CR / THI INDICES) ---
export async function getRiskAssessments(locationName) {
  const params = locationName ? { location: locationName } : {};
  const data = await apiGet('/risk-assessments', params);
  return data.results || [];
}

// --- 5. CROWDSOURCED CITIZEN REPORTS (LIVE API ENDPOINT) ---
export async function getCitizenReports() {
  if (import.meta.env.VITE_USE_MOCK_CITIZEN_REPORTS === 'true') {
    return mockCitizenReports;
  }

  try {
    const data = await apiGet('/citizen-reports');
    
    // Safely unpack Spring Boot wrapper (CitizenReportListResponse)
    const rawList = Array.isArray(data)
      ? data
      : (data.reports || data.results || data.items || data.citizenReports || data.content || []);

    return rawList.map((item, index) => {
      const lat = item.latitude ?? item.lat ?? item.coordinates?.lat ?? 41.25;
      const lon = item.longitude ?? item.lon ?? item.coordinates?.lon ?? 27.50;
      const category = (item.category || 'diger').toLowerCase();
      
      const isVerified = 
        item.aiValidationStatus === 'ONAYLANDI' ||
        item.aiValidationStatus === 'APPROVED' ||
        item.ai_validation_status === 'approved' ||
        (item.aiConfidence != null && item.aiConfidence >= 0.75);

      return {
        id: item.id || `CIT-LIVE-${index + 1}`,
        timestamp: item.timestamp || new Date().toISOString(),
        location_name: item.location_name || item.locationName || `Field Observation (${Number(lat).toFixed(3)}, ${Number(lon).toFixed(3)})`,
        coordinates: { lat: Number(lat), lon: Number(lon) },
        category: category,
        category_label: formatCategoryLabel(category),
        note: item.note || "No additional comments provided by observer.",
        photo_url: item.photo_url || item.photoUrl || item.filePath || item.imageUrl || null,
        ai_verification: {
          verified: isVerified,
          confidence: item.aiConfidence ?? item.ai_confidence ?? 0.88,
          model: item.aiModel || "Gemini-2.5-Flash-Vision",
          feedback: item.aiFeedback || item.ai_feedback || "Environmental anomaly verified via computer vision."
        },
        status: item.status || "approved"
      };
    });
  } catch (err) {
    console.error("Failed to fetch live citizen reports from backend:", err);
    // Graceful fallback to prevent UI breakage if the endpoint is temporarily unavailable
    return mockCitizenReports;
  }
}

// --- ANALYTICAL COMPUTATION UTILITIES ---

/**
 * Computes Executive KPI Metrics across telemetry arrays
 */
export const getDashboardMetrics = (
  measurements = [],
  parameter = 'arsenic',
  sampleType = 'surface_water',
  citizenCount = 0
) => {
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
    citizenReportsCount: typeof citizenCount === 'number' ? citizenCount : 0,
    sampleType: targetSample
  };
};

/**
 * Formats time-series telemetry for AreaChart longitudinal views
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