const BASE = import.meta.env.VITE_API_BASE_URL || '/api';

/**
 * Helper: Güvenli JSON ayrıştırıcı (204 No Content veya boş gövdeli yanıtlarda patlamayı önler)
 */
async function parseResponse(res, path) {
  if (res.status === 404) {
    return null;
  }
  if (!res.ok) {
    throw new Error(`API Hatası [${res.status}]: ${path}`);
  }
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

/**
 * Merkezi GET İstemcisi (Session Cookie dahil)
 */
export async function apiGet(path, params = {}) {
  const queryParts = Object.entries(params)
    .filter(([_, val]) => val !== undefined && val !== null && val !== '')
    .map(([key, val]) => `${encodeURIComponent(key)}=${encodeURIComponent(val)}`);

  const qs = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
  const url = `${BASE}${path}${qs}`;

  const res = await fetch(url, {
    credentials: 'include',
    headers: {
      'Accept': 'application/json',
    },
  });

  return parseResponse(res, path);
}

/**
 * Merkezi POST İstemcisi
 */
export async function apiPost(path, body = {}) {
  const url = `${BASE}${path}`;
  const res = await fetch(url, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify(body),
  });

  return parseResponse(res, path);
}

/**
 * Merkezi PATCH İstemcisi
 */
export async function apiPatch(path, body = {}) {
  const url = `${BASE}${path}`;
  const res = await fetch(url, {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    body: JSON.stringify(body),
  });

  return parseResponse(res, path);
}