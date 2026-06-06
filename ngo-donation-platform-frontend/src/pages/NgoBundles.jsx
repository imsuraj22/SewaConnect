import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  findNgoForUser,
  getPackages,
  createPackageWithImages,
  packageImageUrl,
} from '../api/ngos';

export default function NgoBundles() {
  const { user, token, hasRole } = useAuth();
  const [ngo, setNgo] = useState(null);
  const [packages, setPackages] = useState([]);
  const [title, setTitle] = useState('');
  const [amount, setAmount] = useState('');
  const [items, setItems] = useState('');
  const [images, setImages] = useState([]);
  const [error, setError] = useState('');
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user?.id) return;
    let cancelled = false;
    (async () => {
      setLoading(true);
      try {
        const n = await findNgoForUser(user.id, token);
        if (cancelled) return;
        setNgo(n);
        if (n && String(n.ngoStatus || '').toUpperCase() === 'APPROVED') {
          const pkgs = await getPackages(n.id, token);
          if (!cancelled) setPackages(Array.isArray(pkgs) ? pkgs : []);
        }
      } catch (e) {
        if (!cancelled) setError(e.message || 'Could not load bundles');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [user?.id, token]);

  async function onPublish(e) {
    e.preventDefault();
    setError('');
    setStatus('');
    if (!ngo) return;
    try {
      await createPackageWithImages(
        ngo.id,
        {
          title: title.trim(),
          amount: Number(amount),
          items: items.trim(),
          images,
        },
        token
      );
      setStatus('Support bundle published. Donors can now sponsor it on your profile.');
      setTitle('');
      setAmount('');
      setItems('');
      setImages([]);
      const pkgs = await getPackages(ngo.id, token);
      setPackages(Array.isArray(pkgs) ? pkgs : []);
    } catch (err) {
      setError(err.message || 'Could not publish bundle.');
    }
  }

  if (!user) return null;

  if (!hasRole('NGO')) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <p className="alert alert-error">This page is for NGO representatives only.</p>
        <Link to="/">Home</Link>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <p className="muted">Loading…</p>
      </div>
    );
  }

  const isApproved = String(ngo?.ngoStatus || '').toUpperCase() === 'APPROVED';

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <p className="small">
        <Link to="/ngo-dashboard">← NGO workspace</Link>
      </p>
      <h1>Support bundles</h1>
      <p className="muted">
        Publish bundles donors can sponsor. Each bundle needs a title, target amount,
        what is included, and at least one photo helps donors understand the need.
      </p>

      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      {status && (
        <p className="alert alert-info" role="status">
          {status}
        </p>
      )}

      {!isApproved && (
        <p className="alert alert-info">
          Your organization must be approved before you can publish bundles. Complete
          your profile in the{' '}
          <Link to="/ngo-dashboard">NGO workspace</Link> and submit for admin review.
        </p>
      )}

      {isApproved && (
        <>
          <form
            className="card form-stack"
            style={{ maxWidth: 560, marginBottom: '2rem' }}
            onSubmit={onPublish}
          >
            <h2 style={{ marginTop: 0, fontSize: '1.15rem' }}>Publish a new bundle</h2>
            <label>
              Title
              <input
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. School kit for 50 children"
                required
              />
            </label>
            <label>
              Target amount (₹)
              <input
                type="number"
                min="1"
                step="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                required
              />
            </label>
            <label>
              Items included (comma-separated)
              <input
                value={items}
                onChange={(e) => setItems(e.target.value)}
                placeholder="Notebook, Pencils, Eraser"
              />
            </label>
            <label>
              Photos (optional, multiple)
              <input
                type="file"
                accept="image/*"
                multiple
                onChange={(e) => setImages(Array.from(e.target.files || []))}
              />
            </label>
            <button type="submit" className="btn btn-primary">
              Publish bundle
            </button>
          </form>

          <h2>Published bundles</h2>
          {packages.length === 0 ? (
            <p className="muted">You have not published any bundles yet.</p>
          ) : (
            <ul className="ngo-list">
              {packages.map((p) => (
                <li key={p.id} className="card" style={{ listStyle: 'none' }}>
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
                  {p.imageIds?.length > 0 && (
                    <div className="bundle-gallery" style={{ marginTop: '0.75rem' }}>
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
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </div>
  );
}
