import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { listNgosByStatus, ngoLogoUrl } from '../api/ngos';

function statusLabel(status) {
  if (!status) return '';
  const s = String(status).toUpperCase();
  if (s === 'APPROVED') return 'Verified';
  return status;
}

export default function NgoDirectory() {
  const { token } = useAuth();
  const [ngos, setNgos] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await listNgosByStatus('APPROVED', token);
        if (!cancelled) setNgos(Array.isArray(data) ? data : []);
      } catch (e) {
        if (!cancelled) setError(e.message || 'Could not load NGOs');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [token]);

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>Verified NGOs</h1>
      <p className="muted">
        These organizations have completed our verification step. Open a
        profile to read more and see support bundles you can sponsor, if they
        have published any.
      </p>
      {loading && <p className="muted">Loading…</p>}
      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      {!loading && !error && ngos.length === 0 && (
        <p className="alert alert-info">
          There are no verified NGOs to show yet. If you are helping run the
          platform, complete the review step for pending organizations—then they
          will appear here for donors.
        </p>
      )}
      <ul className="ngo-list">
        {ngos.map((n) => (
          <li key={n.id} className="ngo-directory-item">
            <Link to={`/ngos/${n.id}`} className="ngo-directory-link">
              {n.hasOrganizationImage && (
                <img
                  src={ngoLogoUrl(n.id)}
                  alt=""
                  className="ngo-logo-thumb"
                />
              )}
              <span>
              <strong>{n.name || `Organization #${n.id}`}</strong>
              <span className="badge" style={{ marginLeft: '0.5rem' }}>
                {statusLabel(n.ngoStatus)}
              </span>
              {n.description && (
                <p className="muted small" style={{ margin: '0.35rem 0 0' }}>
                  {n.description.slice(0, 160)}
                  {n.description.length > 160 ? '…' : ''}
                </p>
              )}
              </span>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
