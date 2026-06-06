import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { updateUser } from '../api/users';

const ROLE_LABELS = {
  ROLE_DONOR: 'Donor',
  ROLE_NGO: 'NGO partner',
  ROLE_ADMIN: 'Administrator',
};

function formatRole(role) {
  return ROLE_LABELS[role] || role?.replace(/^ROLE_/, '') || role;
}

function formatDate(value) {
  if (!value) return '—';
  try {
    return new Date(value).toLocaleString();
  } catch {
    return String(value);
  }
}

export default function Profile() {
  const { user, token, refreshMe, hasRole } = useAuth();
  const isNgo = hasRole('NGO');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const me = await refreshMe();
        if (cancelled) return;
        const profile = me || user;
        setUsername(profile?.username || '');
        setEmail(profile?.email || '');
      } catch {
        if (!cancelled) {
          setUsername(user?.username || '');
          setEmail(user?.email || '');
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [refreshMe, user]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setMessage('');
    if (!user?.id) {
      setError('Please sign in again.');
      return;
    }
    const payload = {
      username: username.trim(),
      email: email.trim(),
    };
    if (password.trim()) {
      payload.password = password;
    }
    setSaving(true);
    try {
      await updateUser(user.id, payload, token);
      await refreshMe();
      setPassword('');
      setMessage('Your profile has been updated.');
    } catch (err) {
      setError(err.message || 'Could not update profile.');
    } finally {
      setSaving(false);
    }
  }

  const roles = user?.roles || [];

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>My profile</h1>
      <p className="muted">
        Your SewaConnect account details. Email and username are used to sign in
        {isNgo
          ? ' and access your NGO workspace.'
          : ' and identify your donations.'}
      </p>

      {isNgo && (
        <section
          className="card"
          style={{ marginBottom: '1.5rem', maxWidth: 640, borderColor: 'var(--color-primary, #2563eb)' }}
        >
          <h2 style={{ marginTop: 0, fontSize: '1.15rem' }}>Organization profile</h2>
          <p className="small muted" style={{ marginTop: 0 }}>
            Complete your NGO name, address, phone, documents, and submit for admin
            review in your workspace. Your organization stays inactive until approved.
          </p>
          <Link className="btn btn-primary" to="/ngo-dashboard">
            Open NGO workspace
          </Link>
        </section>
      )}

      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      {message && (
        <p className="alert alert-info" role="status">
          {message}
        </p>
      )}

      <section className="card" style={{ marginBottom: '1.5rem', maxWidth: 520 }}>
        <h2 style={{ marginTop: 0, fontSize: '1.15rem' }}>Account overview</h2>
        <dl className="profile-dl">
          <div>
            <dt>Member ID</dt>
            <dd>{user?.id ?? '—'}</dd>
          </div>
          <div>
            <dt>Account status</dt>
            <dd>{user?.active === false ? 'Inactive' : 'Active'}</dd>
          </div>
          <div>
            <dt>Roles</dt>
            <dd>
              {roles.length ? (
                roles.map((r) => (
                  <span key={r} className="badge" style={{ marginRight: '0.35rem' }}>
                    {formatRole(r)}
                  </span>
                ))
              ) : (
                '—'
              )}
            </dd>
          </div>
          <div>
            <dt>Joined</dt>
            <dd>{formatDate(user?.createdAt)}</dd>
          </div>
          <div>
            <dt>Last updated</dt>
            <dd>{formatDate(user?.updatedAt)}</dd>
          </div>
        </dl>
      </section>

      <form className="form-stack card" style={{ maxWidth: 520 }} onSubmit={handleSubmit}>
        <h2 style={{ marginTop: 0, fontSize: '1.15rem' }}>Edit details</h2>
        <label>
          Username
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </label>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            required
          />
        </label>
        <label>
          New password (leave blank to keep current)
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="new-password"
            minLength={6}
          />
        </label>
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Saving…' : 'Save changes'}
        </button>
      </form>
    </div>
  );
}
