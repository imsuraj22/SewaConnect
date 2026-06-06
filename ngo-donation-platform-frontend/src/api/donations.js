import { API, apiFetch, getStoredToken } from '../config/api';

/**
 * @param {object} donation - fields for Donation entity (no images)
 * @param {File[]} imageFiles
 */
export async function createDonation(donation, imageFiles) {
  const token = getStoredToken();
  const fd = new FormData();
  fd.append(
    'donation',
    new Blob([JSON.stringify(donation)], { type: 'application/json' })
  );
  if (imageFiles?.length) {
    imageFiles.forEach((f) => fd.append('images', f));
  }

  const res = await fetch(`${API.donation}/donations`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: fd,
  });
  if (!res.ok) {
    let detail = res.statusText;
    try {
      detail = await res.text();
    } catch {
      /* ignore */
    }
    const err = new Error(detail || `HTTP ${res.status}`);
    err.status = res.status;
    throw err;
  }
  return res.json();
}

export function listDonationsByDonor(donorId, token) {
  return apiFetch(`${API.donation}/donations/donor/${donorId}`, { token });
}

export function listDonationsByNgo(ngoId, token) {
  return apiFetch(`${API.donation}/donations/ngo/${ngoId}`, { token });
}
