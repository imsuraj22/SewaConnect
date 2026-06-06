import { API, apiFetch } from '../config/api';

export function updateUser(id, payload, token) {
  return apiFetch(`${API.user}/users/${id}`, {
    method: 'PUT',
    token,
    json: payload,
  });
}
