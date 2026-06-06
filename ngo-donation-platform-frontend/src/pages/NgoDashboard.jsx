import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  findNgoForUser,
  updateNgo,
  uploadNgoDocuments,
  uploadOrganizationLogo,
  ngoLogoUrl,
  submitNgoForReview,
} from '../api/ngos';
import { listDonationsByNgo } from '../api/donations';

const DONATION_TYPE_LABELS = {
  DIRECT_MONETARY: 'Money',
  PACKAGE: 'Support bundle',
  ITEM: 'In-kind item',
};

const DONATION_STATUS_LABELS = {
  PENDING: 'Pending',
  ACCEPTED: 'Accepted',
  WITHDRAWN: 'Withdrawn',
  NOT_AVAILABLE: 'Not available',
};

function dLabel(map, key) {
  if (key == null) return '—';
  return map[key] || String(key);
}

function statusLabel(status) {
  if (!status) return '';
  const s = String(status).toUpperCase();
  if (s === 'APPROVED') return 'Active — visible to donors';
  if (s === 'PENDING') return 'Inactive — complete your profile';
  if (s === 'UNDER_REVIEW') return 'Inactive — awaiting admin approval';
  if (s === 'REJECTED') return 'Not approved';
  if (s === 'SUSPENDED') return 'Paused';
  if (s === 'DEACTIVATED') return 'Inactive';
  return status;
}

export default function NgoDashboard() {
  const { user, token, refreshMe, hasRole } = useAuth();
  const [authChecked, setAuthChecked] = useState(false);
  const [ngo, setNgo] = useState(null);
  const [donations, setDonations] = useState([]);
  const [logoFile, setLogoFile] = useState(null);
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [description, setDescription] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [contactEmail, setContactEmail] = useState('');
  const [docs, setDocs] = useState([]);
  const [status, setStatus] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        await refreshMe();
      } catch {
        /* ignore */
      }
      if (!cancelled) setAuthChecked(true);
    })();
    return () => {
      cancelled = true;
    };
  }, [refreshMe]);

  useEffect(() => {
    if (!authChecked || !user?.id) return;
    let cancelled = false;
    (async () => {
      try {
        const n = await findNgoForUser(user.id, token);
        if (cancelled) return;
        setNgo(n);
        if (n) {
          setName(n.name?.trim() || '');
          setAddress(n.address?.trim() || '');
          setDescription(n.description?.trim() || '');
          setPhoneNumber(n.phoneNumber?.trim() || '');
          setContactEmail(n.contactEmail?.trim() || user.email || '');
          const statusUpper = String(n.ngoStatus || '').toUpperCase();
          if (statusUpper === 'APPROVED') {
            const d = await listDonationsByNgo(n.id, token).catch(() => []);
            if (!cancelled) setDonations(Array.isArray(d) ? d : []);
          }
        }
      } catch (e) {
        if (!cancelled) setError(e.message || 'Could not load your organization');
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [authChecked, user?.id, user?.email, token]);

  async function saveProfile(e) {
    e.preventDefault();
    setError('');
    setStatus('');
    if (!ngo) return;
    try {
      await updateNgo(
        ngo.id,
        {
          name: name.trim() || null,
          address: address.trim() || null,
          description: description.trim() || null,
          phoneNumber: phoneNumber.trim() || null,
          contactEmail: contactEmail.trim() || null,
        },
        token
      );
      const refreshed = await findNgoForUser(user.id, token);
      if (refreshed) setNgo(refreshed);
      setStatus('Profile saved.');
    } catch (e) {
      setError(e.message || 'Could not save. Please try again.');
    }
  }

  async function onUploadDocs(e) {
    e.preventDefault();
    setError('');
    setStatus('');
    if (!ngo || docs.length === 0) return;
    try {
      await uploadNgoDocuments(ngo.id, docs, token);
      const refreshed = await findNgoForUser(user.id, token);
      if (refreshed) setNgo(refreshed);
      setStatus('Documents uploaded.');
      setDocs([]);
    } catch (e) {
      setError(e.message || 'Upload did not complete.');
    }
  }

  async function onSubmitForReview(e) {
    e.preventDefault();
    setError('');
    setStatus('');
    if (!ngo) return;
    try {
      const updated = await submitNgoForReview(ngo.id, token);
      setNgo(updated);
      setStatus(
        'Profile submitted for review. An administrator will verify your organization. You will be notified when your account is active.'
      );
    } catch (err) {
      setError(err.message || 'Could not submit for review.');
    }
  }

  async function onUploadLogo(e) {
    e.preventDefault();
    setError('');
    setStatus('');
    if (!ngo || !logoFile) return;
    try {
      await uploadOrganizationLogo(ngo.id, logoFile, token);
      const refreshed = await findNgoForUser(user.id, token);
      if (refreshed) setNgo(refreshed);
      setStatus('Organization image updated. Donors will see it on your public profile.');
      setLogoFile(null);
    } catch (err) {
      setError(err.message || 'Could not upload image.');
    }
  }

  if (!user) return null;

  if (!authChecked) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <p className="muted">Loading…</p>
      </div>
    );
  }

  if (!hasRole('NGO')) {
    return (
      <div className="container" style={{ padding: '2rem 0 3rem' }}>
        <h1>NGO workspace</h1>
        <p className="alert alert-error" role="alert">
          This account is not registered as an NGO representative.
        </p>
        <p className="muted">
          Your roles:{' '}
          <strong>{user.roles?.length ? user.roles.join(', ') : 'none listed'}</strong>
        </p>
        <p className="muted">
          Create a new account and choose <strong>NGO representative</strong> on the
          registration page, or ask an administrator to assign the NGO role.
        </p>
        <Link className="btn btn-primary" to="/register">
          Register as NGO
        </Link>
      </div>
    );
  }

  if (!ngo && !error) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <p className="muted">Loading your organization…</p>
      </div>
    );
  }

  const statusUpper = String(ngo?.ngoStatus || '').toUpperCase();
  const isApproved = statusUpper === 'APPROVED';
  const canEditProfile = statusUpper === 'PENDING';
  const canSubmit =
    ngo?.profileComplete && statusUpper === 'PENDING';
  const completion = ngo?.profileCompletionPercent ?? 0;

  if (!ngo) {
    return (
      <div className="container" style={{ padding: '2rem 0' }}>
        <h1>NGO workspace</h1>
        <p className="alert alert-error" role="alert">
          {error || 'Could not load your organization profile.'}
        </p>
        <p className="muted">
          Make sure <strong>ngo-service</strong> is running on port 8082 and you
          are signed in as an NGO account. Then refresh this page.
        </p>
        <button
          type="button"
          className="btn btn-primary"
          onClick={() => window.location.reload()}
        >
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>NGO workspace</h1>
      <p className="muted">
        Complete your organization profile here. Until an administrator approves
        you, your account stays inactive and donors cannot see your organization.
      </p>
      <p className="muted">
        <strong>{ngo.name?.trim() || 'Your organization'}</strong>
        <span className="muted small" style={{ marginLeft: '0.5rem' }}>
          (reference {ngo.id})
        </span>
        <br />
        <span className="badge" style={{ marginTop: '0.35rem', display: 'inline-block' }}>
          {statusLabel(ngo.ngoStatus)}
        </span>
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

      <section className="card" style={{ marginBottom: '1.5rem', maxWidth: 640 }}>
        <h2 style={{ marginTop: 0, fontSize: '1.15rem' }}>Activation checklist</h2>
        <p className="small muted" style={{ marginTop: 0 }}>
          Profile completion: <strong>{completion}%</strong>
        </p>
        <div
          className="progress-bar"
          role="progressbar"
          aria-valuenow={completion}
          aria-valuemin={0}
          aria-valuemax={100}
        >
          <span style={{ width: `${completion}%` }} />
        </div>
        {ngo.missingProfileFields?.length > 0 && (
          <ul className="small" style={{ margin: '0.75rem 0 0', paddingLeft: '1.2rem' }}>
            {ngo.missingProfileFields.map((f) => (
              <li key={f}>Still needed: {f}</li>
            ))}
          </ul>
        )}
        {canSubmit && (
          <form onSubmit={onSubmitForReview} style={{ marginTop: '1rem' }}>
            <button type="submit" className="btn btn-primary">
              Submit for admin review
            </button>
          </form>
        )}
        {statusUpper === 'UNDER_REVIEW' && (
          <p className="small muted" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
            Your profile is with our team. You cannot edit it until a decision is made.
            Publishing bundles and receiving donations unlock after approval.
          </p>
        )}
        {isApproved && (
          <p className="small muted" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
            Your organization is active. Donors can discover you and you can publish bundles.
          </p>
        )}
        {!isApproved && statusUpper !== 'UNDER_REVIEW' && (
          <p className="small muted" style={{ marginTop: '0.75rem', marginBottom: 0 }}>
            Fill every field below, upload at least one verification document, then submit
            for review.
          </p>
        )}
      </section>

      <div className="grid-2" style={{ alignItems: 'start' }}>
        <form className="card form-stack" onSubmit={saveProfile}>
          <h2 style={{ marginTop: 0 }}>Organization profile</h2>
          <p className="small muted">
            {canEditProfile
              ? 'This information is shown to donors after approval.'
              : 'Profile editing is locked while your application is under review or after a decision.'}
          </p>
          <fieldset disabled={!canEditProfile} className="form-stack" style={{ border: 0, margin: 0, padding: 0 }}>
            <label>
              Organization name
              <input value={name} onChange={(e) => setName(e.target.value)} required />
            </label>
            <label>
              Address
              <textarea value={address} onChange={(e) => setAddress(e.target.value)} required />
            </label>
            <label>
              Description
              <textarea value={description} onChange={(e) => setDescription(e.target.value)} required />
            </label>
            <label>
              Phone number
              <input
                type="tel"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                placeholder="e.g. +91 98765 43210"
                required
              />
            </label>
            <label>
              Contact email
              <input
                type="email"
                value={contactEmail}
                onChange={(e) => setContactEmail(e.target.value)}
                placeholder="contact@your-ngo.org"
                required
              />
            </label>
            <button type="submit" className="btn btn-primary" disabled={!canEditProfile}>
              Save profile
            </button>
          </fieldset>
        </form>

        <form className="card form-stack" onSubmit={onUploadDocs}>
          <h2 style={{ marginTop: 0 }}>Verification documents</h2>
          <p className="small muted">
            Upload registration proofs or other documents required for verification.
          </p>
          <label>
            Files
            <input
              type="file"
              multiple
              disabled={!canEditProfile}
              onChange={(e) => setDocs(Array.from(e.target.files || []))}
            />
          </label>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={!canEditProfile || !docs.length}
          >
            Upload
          </button>
        </form>
      </div>

      {isApproved && (
        <>
          <section className="card" style={{ marginTop: '1.5rem', maxWidth: 520 }}>
            <h2 style={{ marginTop: 0 }}>Organization image</h2>
            <p className="small muted">
              Upload a logo or photo shown to donors on the verified NGOs list and your
              public profile.
            </p>
            {ngo.hasOrganizationImage && (
              <img
                src={`${ngoLogoUrl(ngo.id)}?t=${ngo.id}`}
                alt=""
                className="ngo-logo-preview"
              />
            )}
            <form className="form-stack" onSubmit={onUploadLogo} style={{ marginTop: '1rem' }}>
              <label>
                Image file
                <input
                  type="file"
                  accept="image/*"
                  onChange={(e) => setLogoFile(e.target.files?.[0] || null)}
                />
              </label>
              <button type="submit" className="btn btn-primary" disabled={!logoFile}>
                {ngo.hasOrganizationImage ? 'Replace image' : 'Upload image'}
              </button>
            </form>
          </section>

          <section className="card" style={{ marginTop: '1.5rem', maxWidth: 520 }}>
            <h2 style={{ marginTop: 0 }}>Support bundles</h2>
            <p className="small muted">
              Publish bundles with photos so donors know what they are sponsoring.
            </p>
            <Link className="btn btn-primary" to="/ngo/bundles">
              Manage bundles
            </Link>
          </section>

          <h2 style={{ marginTop: '2rem' }}>Donations linked to your organization</h2>
          <p className="muted small">
            Gifts donors have recorded toward your reference number.
          </p>
          {donations.length === 0 ? (
            <p className="muted">Nothing logged yet.</p>
          ) : (
            <ul className="small">
              {donations.map((d) => (
                <li key={d.id}>
                  Donation #{d.id} · {dLabel(DONATION_TYPE_LABELS, d.donationType)} ·{' '}
                  {dLabel(DONATION_STATUS_LABELS, d.donationStatus)}
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </div>
  );
}
