import { API, apiFetch } from '../config/api';

export function adminPendingNgos(token) {
  return apiFetch(`${API.admin}/admin/ngos/pending`, { token });
}

export function adminGetNgoDetails(ngoId, token) {
  return apiFetch(`${API.admin}/admin/ngos/${ngoId}`, { token });
}

export function adminApproveNgo(ngoId, token) {
  return apiFetch(`${API.admin}/admin/ngos/${ngoId}/approve`, {
    method: 'POST',
    token,
  });
}

export function adminRejectNgo(ngoId, token) {
  return apiFetch(`${API.admin}/admin/ngos/${ngoId}/reject`, {
    method: 'POST',
    token,
  });
}

export function adminSuspendNgo(ngoId, token) {
  return apiFetch(`${API.admin}/admin/ngos/${ngoId}/suspend`, {
    method: 'POST',
    token,
  });
}

export async function adminDownloadNgoDocument(ngoId, documentId, token) {
  const res = await fetch(
    `${API.admin}/admin/ngos/${ngoId}/documents/${documentId}/download`,
    {
      headers: { Authorization: `Bearer ${token}` },
    }
  );
  if (!res.ok) {
    throw new Error('Could not download document');
  }
  return res.blob();
}
