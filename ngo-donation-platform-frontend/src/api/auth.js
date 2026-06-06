import { API, apiFetch, clearAuth, persistAuth } from '../config/api';

export async function login(usernameOrEmail, password) {
  const data = await apiFetch(`${API.user}/auth/login`, {
    method: 'POST',
    json: { usernameOrEmail, password },
  });
  persistAuth(data.accessToken, data.user);
  return data;
}

export async function register({ username, email, password, roles }) {
  const body = { username, email, password };
  if (roles?.length) body.roles = roles;
  const data = await apiFetch(`${API.user}/auth/register`, {
    method: 'POST',
    json: body,
  });
  persistAuth(data.accessToken, data.user);
  return data;
}

export async function fetchMe(token) {
  return apiFetch(`${API.user}/auth/me`, { token });
}

export function logout() {
  clearAuth();
}
