const BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * Merkezi GET İstemcisi
 * - Boşlukları RFC 3986 standardında (%20) encode eder.
 * - 404 (Kayıt Yok) durumunu hata fırlatmadan zarifçe (null) karşılar.
 */
export async function apiGet(path, params = {}) {
  const queryParts = Object.entries(params)
    .filter(([_, val]) => val !== undefined && val !== null && val !== '')
    .map(([key, val]) => `${encodeURIComponent(key)}=${encodeURIComponent(val)}`);

  const qs = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
  const url = `${BASE}${path}${qs}`;

  const res = await fetch(url, {
    headers: {
      'Accept': 'application/json',
    },
  });

  // Eğer konumda risk kaydı yoksa (404), hata fırlatma; null dön
  if (res.status === 404) {
    return null;
  }

  if (!res.ok) {
    throw new Error(`API Hatası [${res.status}]: ${path}`);
  }

  return res.json();
}