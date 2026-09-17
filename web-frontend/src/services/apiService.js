import { THRESHOLDS, evaluateRisk } from '../constants/apiContract';
import { mockCitizenReports } from '../mock/citizenReports';

// 2013 ve 2025 verilerini güvenli import et (dosya yoksa boş diziye düşer)
let raw2013 = [];
let raw2025 = [];

try {
  raw2013 = (await import('../mock/ergene-2013-measurements.json')).default;
} catch (e) {
  console.warn("2013 veri seti src/mock/ altında bulunamadı, boş dizi kullanılıyor.");
}

try {
  raw2025 = (await import('../mock/ergene-2025-measurements.json')).default;
} catch (e) {
  console.warn("2025 veri seti src/mock/ altında bulunamadı, boş dizi kullanılıyor.");
}

// Bütün ölçümleri tekilleştir ve normalize et
export const getAllMeasurements = () => {
  const combined = [
    ...(Array.isArray(raw2013) ? raw2013 : []),
    ...(Array.isArray(raw2025) ? raw2025 : [])
  ];

  return combined.map((item, index) => {
    const isBDL = item.below_detection_limit === true || item.value === null || item.value === undefined;
    const risk = evaluateRisk(item.parameter, item.value, isBDL);

    return {
      id: item.measurement_id || `MEAS-${index + 1}`,
      year: item.year || (item.timestamp ? new Date(item.timestamp).getFullYear() : 2025),
      timestamp: item.timestamp || `${item.year || 2025}-01-01T00:00:00Z`,
      location_name: item.location_name || "Ergene Havzası Ölçüm Noktası",
      coordinates: item.coordinates || null,
      parameter: item.parameter || 'arsenic',
      sample_type: item.sample_type || 'surface_water',
      unit: item.unit || 'mg/L',
      value: isBDL ? null : Number(item.value),
      below_detection_limit: isBDL,
      method: item.method || 'ICP-MS',
      riskStatus: risk.status,
      riskLabel: risk.label,
      isExceeded: risk.isExceeded,
      threshold: risk.threshold,
    };
  });
};

// Dashboard KPI Metrikleri
export const getDashboardMetrics = (parameter = 'arsenic') => {
  const measurements = getAllMeasurements().filter(m => m.parameter === parameter);
  
  // Sadece sayısal değeri olan (BDL olmayan) kayıtlar üzerinden istatistik
  const validMeasurements = measurements.filter(m => !m.below_detection_limit && m.value !== null);
  const bdlCount = measurements.filter(m => m.below_detection_limit).length;
  const exceededCount = validMeasurements.filter(m => m.isExceeded).length;

  const values = validMeasurements.map(m => m.value);
  const maxValue = values.length > 0 ? Math.max(...values) : 0;
  const avgValue = values.length > 0 ? (values.reduce((a, b) => a + b, 0) / values.length) : 0;

  const currentThreshold = THRESHOLDS[parameter]?.who || 0.01;

  return {
    totalMeasurements: measurements.length,
    validMeasurementsCount: validMeasurements.length,
    bdlCount,
    exceededCount,
    maxValue: maxValue.toFixed(4),
    avgValue: avgValue.toFixed(4),
    threshold: currentThreshold,
    unit: 'mg/L',
    isCritical: exceededCount > 0,
    citizenReportsCount: mockCitizenReports.length
  };
};

// Trend Grafiği İçin Zaman Serisi Verisi (Yıllara / Lokasyonlara Göre)
export const getTrendData = (parameter = 'arsenic') => {
  const measurements = getAllMeasurements()
    .filter(m => m.parameter === parameter)
    .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

  return measurements.map(m => ({
    label: `${m.location_name.substring(0, 15)}... (${m.year})`,
    location: m.location_name,
    year: m.year,
    date: m.timestamp.split('T')[0],
    value: m.below_detection_limit ? null : m.value,
    isBDL: m.below_detection_limit,
    threshold: THRESHOLDS[parameter]?.who || 0.01
  }));
};

// Vatandaş Bildirimleri Servisi
export const getCitizenReports = () => mockCitizenReports;