/**
 * NOTICE: The risk evaluation functions in this contract serve primarily as fallbacks
 * for MOCK telemetry mode.
 * 
 * Production telemetry from live backend services (GET /api/observations) natively carries
 * 'risk_flagged' and 'exceeded_standards' attributes computed against regulatory databases.
 * 
 * The THRESHOLDS matrix serves as the single source of truth for UI labeling,
 * chart reference guidelines, and WHO baseline comparisons.
 */

// Primary 9 heavy metal trace elements monitored across the Ergene Basin dataset (2013-2025)
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

// Regulatory Benchmarks & Thresholds for Aquatic Samples (mg/L)
// Source: Arkoç (2014) Table 2 — TS 2005 (Turkish Standards), WHO (2006/2011), US EPA (2013)
export const THRESHOLDS = {
  arsenic: { who: 0.01, ts: 0.01, epa: 0.01, label: 'Arsenic (As)' },
  cadmium: { who: 0.003, ts: 0.005, epa: 0.005, label: 'Cadmium (Cd)' },
  chromium: { who: 0.05, ts: 0.05, epa: 0.1, label: 'Chromium (Cr)' },
  copper: { who: 2.0, ts: 2.0, epa: 1.3, label: 'Copper (Cu)' },
  iron: { who: 0.3, ts: 0.2, epa: 0.3, label: 'Iron (Fe)' },
  lead: { who: 0.01, ts: 0.01, epa: 0.015, label: 'Lead (Pb)' },
  manganese: { who: 0.08, ts: 0.05, epa: 0.05, label: 'Manganese (Mn)' },
  nickel: { who: 0.07, ts: 0.02, epa: 0.1, label: 'Nickel (Ni)' },
  zinc: { who: 3.0, ts: null, epa: 5.0, label: 'Zinc (Zn)' },
};

/**
 * Fallback Risk Assessment Evaluator (Mock Data Pipeline Only)
 */
export const evaluateRisk = (parameter, value, belowDetectionLimit = false, sampleType = 'surface_water') => {
  if (belowDetectionLimit || value === null || value === undefined) {
    return {
      status: 'BDL',
      label: 'Below Detection Limit (BDL)',
      color: 'text-slate-400',
      isExceeded: false,
      unit: sampleType === 'sediment' ? 'mg/kg' : 'mg/L'
    };
  }

  if (sampleType === 'sediment') {
    return {
      status: 'SEDIMENT_REF',
      label: 'Sediment Bed Core',
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
    label: isExceeded ? 'Exceeds WHO Guideline' : 'Within Safe Baseline',
    color: isExceeded ? 'text-red-400' : 'text-emerald-400',
    isExceeded,
    threshold,
    unit: 'mg/L'
  };
};