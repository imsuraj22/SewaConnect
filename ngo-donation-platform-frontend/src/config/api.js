export const API = {
  user: import.meta.env.VITE_USER_API || 'http://localhost:8081',
  ngo: import.meta.env.VITE_NGO_API || 'http://localhost:8082',
  donation: import.meta.env.VITE_DONATION_API || 'http://localhost:8083',
  admin: import.meta.env.VITE_ADMIN_API || 'http://localhost:8084',
};

const TOKEN_KEY = 'sc_access_token';
const USER_KEY = 'sc_user';

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function persistAuth(accessToken, user) {
  localStorage.setItem(TOKEN_KEY, accessToken);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

/**
 * @param {string} url
 * @param {RequestInit & { token?: string | null, json?: unknown }} [options]
 */
export async function apiFetch(url, options = {}) {
  const { token, json, headers: initHeaders, ...rest } = options;
  const headers = new Headers(initHeaders);
  if (json !== undefined) {
    headers.set('Content-Type', 'application/json');
  }
  const auth = token ?? getStoredToken();
  if (auth) {
    headers.set('Authorization', `Bearer ${auth}`);
  }
  const body = json !== undefined ? JSON.stringify(json) : rest.body;
  const res = await fetch(url, { ...rest, headers, body });
  if (!res.ok) {
    let detail = res.statusText;
    try {
      const errBody = await res.json();
      detail = errBody.message || errBody.error || JSON.stringify(errBody);
    } catch {
      try {
        detail = await res.text();
      } catch {
        /* ignore */
      }
    }
    const err = new Error(detail || `HTTP ${res.status}`);
    err.status = res.status;
    throw err;
  }
  if (res.status === 204) return null;
  const text = await res.text();
  if (!text) return null;
  const ct = res.headers.get('content-type');
  if (ct && ct.includes('application/json')) {
    try {
      return JSON.parse(text);
    } catch {
      return text;
    }
  }
  return text;
}
