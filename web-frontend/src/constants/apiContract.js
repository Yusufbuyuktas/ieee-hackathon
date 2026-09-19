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

// Standart Eşik Değerleri (Yalnızca SU numuneleri için - mg/L)
// Not: TS 2005, WHO ve EPA standartları su kalitesi limitleridir.
export const THRESHOLDS = {
  arsenic: { who: 0.01, ts: 0.01, epa: 0.01, label: 'Arsenik (As)' },
  cadmium: { who: 0.003, ts: 0.005, epa: 0.005, label: 'Kadmiyum (Cd)' },
  chromium: { who: 0.05, ts: 0.05, epa: 0.1, label: 'Krom (Cr)' },
  copper: { who: 2.0, ts: 2.0, epa: 1.3, label: 'Bakır (Cu)' },
  iron: { who: 0.3, ts: 0.2, epa: 0.3, label: 'Demir (Fe)' },
  lead: { who: 0.01, ts: 0.01, epa: 0.015, label: 'Kurşun (Pb)' },
  manganese: { who: 0.4, ts: 0.05, epa: 0.05, label: 'Mangan (Mn)' },
  nickel: { who: 0.07, ts: 0.02, epa: 0.1, label: 'Nikel (Ni)' },
  zinc: { who: 3.0, ts: null, epa: 5.0, label: 'Çinko (Zn)' }, // TS tanımlı değil
};

/**
 * Numune türüne (su vs sediment) ve tespit limitine göre bilimsel risk değerlendirmesi
 */
export const evaluateRisk = (parameter, value, belowDetectionLimit = false, sampleType = 'surface_water') => {
  // 1. Tespit Limiti Altı (BDL)
  if (belowDetectionLimit || value === null || value === undefined) {
    return {
      status: 'BDL',
      label: 'Tespit Limiti Altı',
      color: 'text-slate-400',
      isExceeded: false,
      unit: sampleType === 'sediment' ? 'mg/kg' : 'mg/L'
    };
  }

  // 2. Sediment (Dip Çamuru): DSÖ içme suyu sınırları doğrudan katı maddeye uygulanamaz
  if (sampleType === 'sediment') {
    return {
      status: 'SEDIMENT_REF',
      label: 'Sediment / Dip Çamuru',
      color: 'text-amber-400',
      isExceeded: false, // Su eşiğiyle karıştırılmaz
      unit: 'mg/kg',
      threshold: null
    };
  }

  // 3. Su Numuneleri (surface_water, groundwater) - mg/L
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