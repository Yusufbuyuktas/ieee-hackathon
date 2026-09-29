// Türkçe karakterleri ve büyük/küçük harf duyarlılığını temizleyen fonksiyon
export const normalizeText = (text = '') => {
  return text
    .toString()
    .toLowerCase()
    .replace(/ğ/g, 'g')
    .replace(/ü/g, 'u')
    .replace(/ş/g, 's')
    .replace(/ı/g, 'i')
    .replace(/ö/g, 'o')
    .replace(/ç/g, 'c')
    .trim();
};

// Hastayı Ergene ölçüm istasyonlarıyla eşleştiren hibrit motor
export const getPatientRegionalMeasurements = (patient, allMeasurements = []) => {
  if (!allMeasurements || allMeasurements.length === 0) return [];

  const pLat = patient.coordinates?.lat;
  const pLon = patient.coordinates?.lon;

  let matched = [];

  // Katman 1: Koordinat Bazlı Yakınlık (Trakya ölçeğinde ~30 km yarıçap)
  if (pLat && pLon) {
    matched = allMeasurements.filter((m) => {
      if (!m.coordinates || !m.coordinates.lat || !m.coordinates.lon) return false;
      const dLat = (m.coordinates.lat - pLat) * 111; // Enlem başına ~111 km
      const dLon = (m.coordinates.lon - pLon) * 85;  // Trakya için boylam başına ~85 km
      const distKm = Math.sqrt(dLat * dLat + dLon * dLon);
      return distKm <= 35;
    });
  }

  // Katman 2: Koordinatsız kayıtlar veya 0 eşleşme durumunda Anahtar Kelime Eşleşmesi
  if (matched.length === 0) {
    const keywords = [
      ...(patient.station_keywords || []),
      patient.district
    ].map(normalizeText);

    matched = allMeasurements.filter((m) => {
      const locNorm = normalizeText(m.location_name || '');
      return keywords.some((kw) => locNorm.includes(kw));
    });
  }

  // Katman 3: Veri setinde lokasyon adı tamamen farklıysa güvenli havuz
  if (matched.length === 0) {
    const targetParam = patient.suspected_exposure || 'arsenic';
    matched = allMeasurements.filter((m) => m.parameter === targetParam);
  }

  return matched;
};