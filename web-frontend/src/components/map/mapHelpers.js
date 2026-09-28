/**
 * Fallback GIS Coordinate Dictionary (Reserved for locations unmapped in backend endpoints)
 */
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

/**
 * Deterministic Spatial Offset Generator
 * Prevents overlapping pins from jittering or stacking identically when explicit coords are missing.
 */
const getDeterministicOffset = (str = '', scale = 0.03) => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  const normalized = ((Math.abs(hash) % 1000) / 1000 - 0.5) * 2;
  return normalized * scale;
};

/**
 * Coordinate Resolution Engine
 * Resolution Hierarchy:
 * 1. Explicit station `coordinates` payload
 * 2. Exact match from `GET /api/locations` reference table
 * 3. Keyword heuristic match against LOCATION_COORDINATES dictionary
 * 4. Deterministic basin centroid distribution
 */
export const resolveCoordinates = (item, knownLocations = []) => {
  // 1. Direct explicit coordinates present
  if (item.coordinates && item.coordinates.lat && item.coordinates.lon) {
    return {
      lat: item.coordinates.lat,
      lon: item.coordinates.lon,
      isApproximate: Boolean(item.coordinate_source === 'approximated_from_figure')
    };
  }

  const name = item.location_name || item.id || "Ergene";
  const idStr = item.id || name;

  // 2. Lookup in GET /api/locations registry
  if (Array.isArray(knownLocations) && knownLocations.length > 0) {
    const matchedLoc = knownLocations.find(loc => loc.location_name === name);
    if (matchedLoc?.coordinates?.lat && matchedLoc?.coordinates?.lon) {
      return {
        lat: matchedLoc.coordinates.lat,
        lon: matchedLoc.coordinates.lon,
        isApproximate: false
      };
    }
  }

  // 3. Heuristic matching via fallback dictionary
  for (const [key, coords] of Object.entries(LOCATION_COORDINATES)) {
    if (name.toLowerCase().includes(key.toLowerCase())) {
      return {
        lat: coords.lat + getDeterministicOffset(idStr + '_lat', 0.015),
        lon: coords.lon + getDeterministicOffset(idStr + '_lon', 0.015),
        isApproximate: true
      };
    }
  }

  // 4. Fallback basin centroid with deterministic offset
  return {
    lat: 41.25 + getDeterministicOffset(idStr + '_lat', 0.08),
    lon: 27.45 + getDeterministicOffset(idStr + '_lon', 0.12),
    isApproximate: true
  };
};