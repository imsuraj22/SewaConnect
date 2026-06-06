import { API, apiFetch } from '../config/api';

export function listNgosByStatus(status, token) {
  return apiFetch(`${API.ngo}/api/ngos/status/${status}`, { token });
}

export function getNgo(id, token) {
  return apiFetch(`${API.ngo}/api/ngos/${id}`, { token });
}

export function getNgoByUser(userId, token) {
  return apiFetch(`${API.ngo}/api/ngos/by-user/${userId}`, { token });
}

export function getMyNgoWorkspace(token) {
  return apiFetch(`${API.ngo}/api/ngos/workspace/me`, { token });
}

export function submitNgoForReview(ngoId, token) {
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}/submit-for-review`, {
    method: 'POST',
    token,
  });
}

export function getPackages(ngoId, token) {
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}/packages`, { token });
}

export function updateNgo(ngoId, payload, token) {
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}`, {
    method: 'PUT',
    token,
    json: payload,
  });
}

export function uploadNgoDocuments(ngoId, files, token) {
  const fd = new FormData();
  files.forEach((f) => fd.append('files', f));
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}/documents`, {
    method: 'POST',
    token,
    body: fd,
  });
}

export function createPackage(ngoId, pkg, token) {
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}/packages`, {
    method: 'POST',
    token,
    json: pkg,
  });
}

/** Publish a support bundle with optional photos (multipart). */
export function createPackageWithImages(ngoId, { title, amount, items, images }, token) {
  const fd = new FormData();
  fd.append('title', title);
  fd.append('amount', String(amount));
  if (items) fd.append('items', items);
  (images || []).forEach((file) => fd.append('images', file));
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}/packages`, {
    method: 'POST',
    token,
    body: fd,
  });
}

export function uploadOrganizationLogo(ngoId, file, token) {
  const fd = new FormData();
  fd.append('file', file);
  return apiFetch(`${API.ngo}/api/ngos/${ngoId}/logo`, {
    method: 'POST',
    token,
    body: fd,
  });
}

export function ngoLogoUrl(ngoId) {
  return `${API.ngo}/api/ngos/${ngoId}/logo`;
}

export function packageImageUrl(ngoId, packageId, imageId) {
  return `${API.ngo}/api/ngos/${ngoId}/packages/${packageId}/images/${imageId}`;
}

const STATUSES = [
  'PENDING',
  'APPROVED',
  'UNDER_REVIEW',
  'REJECTED',
  'SUSPENDED',
  'DEACTIVATED',
];

/** Resolve the current user's NGO profile (creates stub on server if missing). */
export async function findNgoForUser(userId, token) {
  try {
    return await getMyNgoWorkspace(token);
  } catch (e) {
    if (!userId) return null;
    try {
      return await getNgoByUser(userId, token);
    } catch (inner) {
      if (inner.status === 404 || inner.status === 403) {
        return null;
      }
      throw inner;
    }
  }
}
