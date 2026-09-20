/**
 * UYARI: Bu dosyadaki risk fonksiyonları yalnızca MOCK veri modunda kullanılır.
 * Gerçek API'den gelen veriler (GET /api/observations) zaten 'risk_flagged'
 * ve 'exceeded_standards' alanlarını taşır; frontend riski yeniden hesaplamaz.
 * 
 * THRESHOLDS tablosu ise UI etiketleri ve grafik referans çizgileri için referans olarak tutulur.
 */

// Gerçek veri setindeki (2013-2025) 9 metal parametresi
export const PARAMETERS = {
  ARSENIC: 'arsenic',
  CADMIUM: 'cadmium',
  CHROMIUM: 'chromium',
  COPPER: 'copper',
  IRON: 'iron',
  LEAD: 'lead',
  MANGANESE: 'manganese',
  NICKEL: 'nickel',
  ZINC: 'zinc',
};

export const SAMPLE_TYPES = {
  SURFACE_WATER: 'surface_water',
  GROUNDWATER: 'groundwater',
  SEDIMENT: 'sediment',
};

// UI Gösterim ve Standart Referans Değerleri (Su numuneleri için - mg/L)
// Kaynak: Arkoç (2014) Tablo 2 — TS 2005, WHO 2006, EPA 2013
export const THRESHOLDS = {
  arsenic: { who: 0.01, ts: 0.01, epa: 0.01, label: 'Arsenik (As)' },
  cadmium: { who: 0.003, ts: 0.005, epa: 0.005, label: 'Kadmiyum (Cd)' },
  chromium: { who: 0.05, ts: 0.05, epa: 0.1, label: 'Krom (Cr)' },
  copper: { who: 2.0, ts: 2.0, epa: 1.3, label: 'Bakır (Cu)' },
  iron: { who: 0.3, ts: 0.2, epa: 0.3, label: 'Demir (Fe)' },
  lead: { who: 0.01, ts: 0.01, epa: 0.015, label: 'Kurşun (Pb)' },
  manganese: { who: 0.08, ts: 0.05, epa: 0.05, label: 'Mangan (Mn)' },
  nickel: { who: 0.07, ts: 0.02, epa: 0.1, label: 'Nikel (Ni)' },
  zinc: { who: 3.0, ts: null, epa: 5.0, label: 'Çinko (Zn)' },
};

/**
 * Yalnızca Mock Veri Fallback Fonksiyonu
 */
export const evaluateRisk = (parameter, value, belowDetectionLimit = false, sampleType = 'surface_water') => {
  if (belowDetectionLimit || value === null || value === undefined) {
    return {
      status: 'BDL',
      label: 'Tespit Limiti Altı',
      color: 'text-slate-400',
      isExceeded: false,
      unit: sampleType === 'sediment' ? 'mg/kg' : 'mg/L'
    };
  }

  if (sampleType === 'sediment') {
    return {
      status: 'SEDIMENT_REF',
      label: 'Sediment / Dip Çamuru',
      color: 'text-amber-400',
      isExceeded: false,
      unit: 'mg/kg',
      threshold: null
    };
  }

  const threshold = THRESHOLDS[parameter]?.who ?? 0.01;
  const isExceeded = Number(value) > threshold;

  return {
    status: isExceeded ? 'HIGH' : 'SAFE',
    label: isExceeded ? 'Eşik Aşıldı (DSÖ)' : 'Güvenli Aralık',
    color: isExceeded ? 'text-red-400' : 'text-emerald-400',
    isExceeded,
    threshold,
    unit: 'mg/L'
  };
};