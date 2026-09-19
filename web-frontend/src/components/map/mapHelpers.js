export const LOCATION_COORDINATES = {
  "Çorlu": { lat: 41.1592, lon: 27.8033 },
  "Çorlu Deresi": { lat: 41.1610, lon: 27.7985 },
  "Çerkezköy": { lat: 41.2861, lon: 28.0016 },
  "Muratlı": { lat: 41.1712, lon: 27.5024 },
  "Lüleburgaz": { lat: 41.4055, lon: 27.3512 },
  "Babaeski": { lat: 41.4328, lon: 27.0944 },
  "Uzunköprü": { lat: 41.2685, lon: 26.6853 },
  "Meriç": { lat: 41.2100, lon: 26.6000 },
  "Ergene": { lat: 41.2500, lon: 27.4000 }
};

// String tabanlı deterministik hash (Pinlerin her render'da sabit kalmasını sağlar)
const getDeterministicOffset = (str = '', scale = 0.04) => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  const normalized = ((Math.abs(hash) % 1000) / 1000 - 0.5) * 2;
  return normalized * scale;
};

export const resolveCoordinates = (item) => {
  if (item.coordinates && item.coordinates.lat && item.coordinates.lon) {
    return { lat: item.coordinates.lat, lon: item.coordinates.lon, isApproximate: false };
  }

  const name = item.location_name || item.id || "Ergene";
  const idStr = item.id || name;

  for (const [key, coords] of Object.entries(LOCATION_COORDINATES)) {
    if (name.toLowerCase().includes(key.toLowerCase())) {
      return {
        lat: coords.lat + getDeterministicOffset(idStr + '_lat', 0.015),
        lon: coords.lon + getDeterministicOffset(idStr + '_lon', 0.015),
        isApproximate: true
      };
    }
  }

  // Varsayılan havza merkezinde deterministik dağılım
  return {
    lat: 41.25 + getDeterministicOffset(idStr + '_lat', 0.08),
    lon: 27.45 + getDeterministicOffset(idStr + '_lon', 0.12),
    isApproximate: true
  };
};