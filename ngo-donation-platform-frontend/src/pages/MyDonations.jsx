import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { listDonationsByDonor } from '../api/donations';

const TYPE_LABELS = {
  DIRECT_MONETARY: 'Money',
  PACKAGE: 'Support bundle',
  ITEM: 'In-kind item',
};

const STATUS_LABELS = {
  PENDING: 'Pending',
  ACCEPTED: 'Accepted',
  WITHDRAWN: 'Withdrawn',
  NOT_AVAILABLE: 'Not available',
};

function label(map, key) {
  if (key == null) return '—';
  return map[key] || String(key);
}

export default function MyDonations() {
  const { user, token } = useAuth();
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user?.id) return;
    let cancelled = false;
    (async () => {
      try {
        const data = await listDonationsByDonor(user.id, token);
        if (!cancelled) setRows(Array.isArray(data) ? data : []);
      } catch (e) {
        if (!cancelled) setError(e.message || 'Could not load donations');
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [user?.id, token]);

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>My donations</h1>
      <p className="muted">
        A personal log of what you have offered through SewaConnect—amounts,
        bundles, items, and the latest status we have on file.
      </p>
      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      {rows.length === 0 && !error ? (
        <p className="muted">You have not recorded any donations yet.</p>
      ) : (
        <ul className="ngo-list">
          {rows.map((d) => (
            <li key={d.id} className="card" style={{ listStyle: 'none' }}>
              <strong>Donation #{d.id}</strong>{' '}
              <span className="badge">{label(TYPE_LABELS, d.donationType)}</span>{' '}
              <span className="badge">{label(STATUS_LABELS, d.donationStatus)}</span>
              <p className="small muted" style={{ margin: '0.35rem 0 0' }}>
                Organization ref: {d.ngoId ?? '—'} · Bundle ref: {d.packageId ?? '—'} ·{' '}
                {d.amount != null ? `${d.amount} ${d.currency || ''}`.trim() : '—'}
              </p>
              {d.itemName && (
                <p className="small" style={{ margin: '0.25rem 0 0' }}>
                  Item: {d.itemName}
                </p>
              )}
              {d.itemDescription && (
                <p className="small muted" style={{ margin: '0.25rem 0 0' }}>
                  {d.itemDescription}
                </p>
              )}
              {d.createdAt && (
                <p className="small muted" style={{ margin: '0.25rem 0 0' }}>
                  Recorded: {d.createdAt}
                </p>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
