import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getNgo, getPackages, ngoLogoUrl, packageImageUrl } from '../api/ngos';

function statusLabel(status) {
  if (!status) return '';
  const s = String(status).toUpperCase();
  if (s === 'APPROVED') return 'Verified';
  if (s === 'PENDING') return 'In review';
  if (s === 'REJECTED') return 'Not approved';
  if (s === 'SUSPENDED') return 'Paused';
  if (s === 'DEACTIVATED') return 'Inactive';
  return status;
}

export default function NgoDetail() {
  const { id } = useParams();
  const { token } = useAuth();
  const [ngo, setNgo] = useState(null);
  const [packages, setPackages] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const [n, pkgs] = await Promise.all([
          getNgo(id, token),
          getPackages(id, token),
        ]);
        if (!cancelled) {
          setNgo(n);
          setPackages(Array.isArray(pkgs) ? pkgs : []);
        }
      } catch (e) {
        if (!cancelled) setError(e.message || 'Failed to load NGO');
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [id, token]);

  if (error) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <p className="alert alert-error">{error}</p>
        <Link to="/ngos">Back to verified NGOs</Link>
      </div>
    );
  }

  if (!ngo) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <p className="muted">Loading…</p>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <p className="small">
        <Link to="/ngos">← Verified NGOs</Link>
      </p>
      <div className="ngo-profile-header">
        {ngo.hasOrganizationImage && (
          <img
            src={ngoLogoUrl(ngo.id)}
            alt=""
            className="ngo-logo-preview"
          />
        )}
        <div>
          <h1 style={{ marginTop: 0 }}>{ngo.name || `Organization #${ngo.id}`}</h1>
        </div>
      </div>
      <p>
        <span className="badge">{statusLabel(ngo.ngoStatus)}</span>
      </p>
      {ngo.address && (
        <p className="muted">
          <strong>Address:</strong> {ngo.address}
        </p>
      )}
      {ngo.phoneNumber && (
        <p className="muted">
          <strong>Phone:</strong>{' '}
          <a href={`tel:${ngo.phoneNumber}`}>{ngo.phoneNumber}</a>
        </p>
      )}
      {ngo.contactEmail && (
        <p className="muted">
          <strong>Email:</strong>{' '}
          <a href={`mailto:${ngo.contactEmail}`}>{ngo.contactEmail}</a>
        </p>
      )}
      {ngo.description && <p>{ngo.description}</p>}

      <h2>Support bundles</h2>
      <p className="muted small">
        When you sponsor a bundle below, we attach it to your donation so the
        organization knows exactly what you intended to cover.
      </p>
      {packages.length === 0 ? (
        <p className="muted">This organization has not published any bundles yet.</p>
      ) : (
        <ul className="ngo-list">
          {packages.map((p) => (
            <li key={p.id} className="card" style={{ listStyle: 'none' }}>
              {p.imageIds?.length > 0 && (
                <div className="bundle-gallery" style={{ marginBottom: '0.75rem' }}>
                  {p.imageIds.map((imgId) => (
                    <img
                      key={imgId}
                      src={packageImageUrl(ngo.id, p.id, imgId)}
                      alt=""
                      className="bundle-thumb"
                    />
                  ))}
                </div>
              )}
              <strong>{p.title}</strong>
              <span className="muted" style={{ marginLeft: '0.5rem' }}>
                ₹{p.amount}
              </span>
              {p.items?.length > 0 && (
                <ul className="small muted" style={{ margin: '0.5rem 0 0' }}>
                  {p.items.map((it, i) => (
                    <li key={i}>{it.name}</li>
                  ))}
                </ul>
              )}
              <p className="small" style={{ marginTop: '0.5rem' }}>
                <Link className="btn btn-primary" to={`/donate?ngoId=${ngo.id}&packageId=${p.id}`}>
                  Sponsor this bundle
                </Link>
              </p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
