import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  adminPendingNgos,
  adminApproveNgo,
  adminRejectNgo,
  adminSuspendNgo,
  adminDownloadNgoDocument,
} from '../api/admin';

function docList(ngo) {
  const docs = ngo?.documents;
  if (!docs) return [];
  return Array.isArray(docs) ? docs : [...docs];
}

export default function AdminDashboard() {
  const { token } = useAuth();
  const [pending, setPending] = useState([]);
  const [error, setError] = useState('');
  const [msg, setMsg] = useState('');

  async function refresh() {
    setError('');
    try {
      const data = await adminPendingNgos(token);
      setPending(Array.isArray(data) ? data : []);
    } catch (e) {
      setError(e.message || 'Could not load the review queue.');
    }
  }

  useEffect(() => {
    refresh();
  }, [token]);

  async function act(fn, ngoId, label) {
    setMsg('');
    setError('');
    try {
      await fn(ngoId, token);
      setMsg(`${label} — organization #${ngoId}`);
      await refresh();
    } catch (e) {
      setError(e.message || `${label} could not be completed.`);
    }
  }

  async function downloadDoc(ngoId, docId, fileName) {
    setError('');
    try {
      const blob = await adminDownloadNgoDocument(ngoId, docId, token);
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName || `document-${docId}`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      setError(e.message || 'Could not download document.');
    }
  }

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>Review organizations</h1>
      <p className="muted">
        NGOs appear here after they complete their profile, upload verification
        documents, and submit for review. Open each file before approving.
      </p>
      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      {msg && (
        <p className="alert alert-info" role="status">
          {msg}
        </p>
      )}

      <button type="button" className="btn btn-ghost" onClick={refresh}>
        Refresh list
      </button>

      <h2>Awaiting decision</h2>
      {pending.length === 0 ? (
        <p className="muted">No organizations are waiting in this queue right now.</p>
      ) : (
        <ul className="ngo-list" style={{ padding: 0, marginTop: '1rem' }}>
          {pending.map((n) => {
            const documents = docList(n);
            return (
              <li
                key={n.id}
                className="card"
                style={{ listStyle: 'none', marginBottom: '1.25rem' }}
              >
                <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '0.5rem' }}>
                  <strong style={{ fontSize: '1.15rem' }}>
                    {n.name || `Organization #${n.id}`}
                  </strong>
                  <span className="badge">{n.ngoStatus}</span>
                  {n.profileComplete && (
                    <span className="badge" style={{ opacity: 0.85 }}>
                      Profile {n.profileCompletionPercent ?? 100}% complete
                    </span>
                  )}
                </div>

                <dl
                  className="profile-dl"
                  style={{ margin: '1rem 0', display: 'grid', gap: '0.65rem' }}
                >
                  <div>
                    <dt>Organization ID</dt>
                    <dd>{n.id}</dd>
                  </div>
                  <div>
                    <dt>Registered user ID</dt>
                    <dd>{n.userId}</dd>
                  </div>
                  <div>
                    <dt>Contact email</dt>
                    <dd>{n.contactEmail || '—'}</dd>
                  </div>
                  <div>
                    <dt>Phone</dt>
                    <dd>{n.phoneNumber || '—'}</dd>
                  </div>
                  <div>
                    <dt>Address</dt>
                    <dd style={{ whiteSpace: 'pre-wrap' }}>{n.address || '—'}</dd>
                  </div>
                  <div>
                    <dt>Description</dt>
                    <dd style={{ whiteSpace: 'pre-wrap' }}>{n.description || '—'}</dd>
                  </div>
                </dl>

                <h3 style={{ fontSize: '1rem', margin: '0 0 0.5rem' }}>
                  Verification documents ({documents.length})
                </h3>
                {documents.length === 0 ? (
                  <p className="small muted">No documents uploaded.</p>
                ) : (
                  <ul className="small" style={{ margin: '0 0 1rem', paddingLeft: '1.2rem' }}>
                    {documents.map((d) => (
                      <li key={d.id} style={{ marginBottom: '0.35rem' }}>
                        {d.fileName || `File #${d.id}`}
                        <button
                          type="button"
                          className="btn btn-ghost"
                          style={{ marginLeft: '0.5rem', padding: '0.15rem 0.5rem' }}
                          onClick={() => downloadDoc(n.id, d.id, d.fileName)}
                        >
                          Download
                        </button>
                      </li>
                    ))}
                  </ul>
                )}

                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={() => act(adminApproveNgo, n.id, 'Approved')}
                  >
                    Approve for donors
                  </button>
                  <button
                    type="button"
                    className="btn btn-ghost"
                    onClick={() => act(adminRejectNgo, n.id, 'Rejected')}
                  >
                    Reject
                  </button>
                  <button
                    type="button"
                    className="btn btn-ghost"
                    onClick={() => act(adminSuspendNgo, n.id, 'Suspended')}
                  >
                    Pause listing
                  </button>
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
