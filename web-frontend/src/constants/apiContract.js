// docs/api-contract.md şemasına birebir uyumlu parametre ve eşik tanımları

export const PARAMETERS = {
  ARSENIC: 'arsenic',
  CADMIUM: 'cadmium',
  LEAD: 'lead',
  MERCURY: 'mercury',
  COPPER: 'copper',
  CHROMIUM: 'chromium',
  ZINC: 'zinc',
  NICKEL: 'nickel',
  COBALT: 'cobalt',
};

export const SAMPLE_TYPES = {
  SURFACE_WATER: 'surface_water',
  GROUNDWATER: 'groundwater',
  SEDIMENT: 'sediment',
};

export const UNITS = {
  WATER: 'mg/L',
  SEDIMENT: 'mg/kg',
  UG_L: 'µg/L',
};

// TS 2005, WHO 2006 ve EPA 2013 standart eşik değerleri (mg/L - Yüzeysel/İçme Suyu)
export const THRESHOLDS = {
  arsenic: { who: 0.01, ts: 0.01, epa: 0.01, label: 'Arsenik (As)' },
  cadmium: { who: 0.003, ts: 0.005, epa: 0.005, label: 'Kadmiyum (Cd)' },
  lead: { who: 0.01, ts: 0.01, epa: 0.015, label: 'Kurşun (Pb)' },
  mercury: { who: 0.006, ts: 0.001, epa: 0.002, label: 'Cıva (Hg)' },
  copper: { who: 2.0, ts: 2.0, epa: 1.3, label: 'Bakır (Cu)' },
  chromium: { who: 0.05, ts: 0.05, epa: 0.1, label: 'Krom (Cr)' },
  zinc: { who: 3.0, ts: 5.0, epa: 5.0, label: 'Çinko (Zn)' },
  nickel: { who: 0.07, ts: 0.02, epa: 0.1, label: 'Nikel (Ni)' },
  cobalt: { who: 0.05, ts: 0.05, epa: 0.05, label: 'Kobalt (Co)' },
};

// BDL ve Eşik Kontrol Yardımcısı
export const evaluateRisk = (parameter, value, belowDetectionLimit = false) => {
  if (belowDetectionLimit || value === null || value === undefined) {
    return { status: 'BDL', label: 'Tespit Limiti Altı', color: 'text-slate-400', isExceeded: false };
  }
  const threshold = THRESHOLDS[parameter]?.who || 0.01;
  if (value > threshold) {
    return { status: 'HIGH', label: 'Eşik Aşıldı (DSÖ)', color: 'text-red-400', isExceeded: true, threshold };
  }
  return { status: 'SAFE', label: 'Güvenli Aralık', color: 'text-emerald-400', isExceeded: false, threshold };
};