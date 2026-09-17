// Ergene Havzası kilit noktalarının yaklaşık koordinatları
export const LOCATION_COORDINATES = {
  "Çorlu": { lat: 41.1592, lon: 27.8033 },
  "Çorlu Deresi": { lat: 41.1610, lon: 27.7985 },
  "Çerkezköy": { lat: 41.2861, lon: 28.0016 },
  "Muratlı": { lat: 41.1712, lon: 27.5024 },
  "Lüleburgaz": { lat: 41.4055, lon: 27.3512 },
  "Babaeski": { lat: 41.4328, lon: 27.0944 },
  "Uzunköprü": { lat: 41.2685, lon: 26.6853 },
  "Meriç Birleşim": { lat: 41.2100, lon: 26.6000 },
  "Ergene Havzası": { lat: 41.2500, lon: 27.4000 }
};

// İstasyon adından koordinat türetme (coordinates: null ise fallback)
export const resolveCoordinates = (item) => {
  if (item.coordinates && item.coordinates.lat && item.coordinates.lon) {
    return { lat: item.coordinates.lat, lon: item.coordinates.lon, isApproximate: false };
  }

  const name = item.location_name || "";
  for (const [key, coords] of Object.entries(LOCATION_COORDINATES)) {
    if (name.toLowerCase().includes(key.toLowerCase())) {
      return { ...coords, isApproximate: true };
    }
  }

  // Varsayılan havza merkezi (rastgele ufak sapmayla çakışmayı önler)
  return {
    lat: 41.25 + (Math.random() - 0.5) * 0.15,
    lon: 27.50 + (Math.random() - 0.5) * 0.25,
    isApproximate: true
  };
};