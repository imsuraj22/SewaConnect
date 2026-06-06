import { useMemo, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { createDonation } from '../api/donations';

const TYPES = [
  { value: 'DIRECT_MONETARY', label: 'Money' },
  { value: 'PACKAGE', label: 'Sponsor a support bundle' },
  { value: 'ITEM', label: 'In-kind item (goods)' },
];

export default function Donate() {
  const { user } = useAuth();
  const [params] = useSearchParams();
  const initialNgo = params.get('ngoId') || '';
  const initialPkg = params.get('packageId') || '';

  const [donationType, setDonationType] = useState(
    initialPkg ? 'PACKAGE' : 'DIRECT_MONETARY'
  );
  const [ngoId, setNgoId] = useState(initialNgo);
  const [packageId, setPackageId] = useState(initialPkg);
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('INR');
  const [itemName, setItemName] = useState('');
  const [itemDescription, setItemDescription] = useState('');
  const [files, setFiles] = useState([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const donorId = user?.id;

  const payloadPreview = useMemo(() => {
    const base = {
      donorId,
      donationType,
      ngoId: ngoId ? Number(ngoId) : null,
    };
    if (donationType === 'DIRECT_MONETARY') {
      return {
        ...base,
        amount: amount ? Number(amount) : null,
        currency,
      };
    }
    if (donationType === 'PACKAGE') {
      return {
        ...base,
        packageId: packageId ? Number(packageId) : null,
        amount: amount ? Number(amount) : null,
        currency,
      };
    }
    return {
      ...base,
      itemName: itemName || null,
      itemDescription: itemDescription || null,
    };
  }, [
    donorId,
    donationType,
    ngoId,
    packageId,
    amount,
    currency,
    itemName,
    itemDescription,
  ]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setMessage('');
    if (!donorId) {
      setError('Please sign in again so we know who this donation is from.');
      return;
    }
    if (donationType === 'PACKAGE' && !packageId) {
      setError(
        'Open an NGO’s page and use “Sponsor this bundle” so the bundle is selected for you—or enter the bundle details if your coordinator shared them.'
      );
      return;
    }
    if (donationType === 'ITEM' && !itemName.trim()) {
      setError('Please add a short name for the item you are offering.');
      return;
    }
    try {
      const donation = { ...payloadPreview };
      if (donation.ngoId == null || Number.isNaN(donation.ngoId)) {
        delete donation.ngoId;
      }
      if (donationType !== 'PACKAGE') {
        delete donation.packageId;
      } else {
        donation.packageId = Number(packageId);
      }
      await createDonation(donation, [...files]);
      setMessage('Thank you—your donation has been recorded.');
      setFiles([]);
    } catch (err) {
      setError(err.message || 'Something went wrong. Please try again.');
    }
  }

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>Make a donation</h1>
      <p className="muted">
        Tell us what you are offering and, when it applies, which organization
        or bundle it belongs to. Photos help staff recognize in-kind gifts.
      </p>
      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      {message && (
        <p className="alert alert-info" role="status">
          {message}{' '}
          <Link to="/my-donations">View my donations</Link>
        </p>
      )}

      <form className="form-stack" style={{ maxWidth: 520 }} onSubmit={handleSubmit}>
        <label>
          Type of support
          <select
            value={donationType}
            onChange={(e) => setDonationType(e.target.value)}
          >
            {TYPES.map((t) => (
              <option key={t.value} value={t.value}>
                {t.label}
              </option>
            ))}
          </select>
        </label>

        <label>
          Organization (reference number, optional for some gifts)
          <input
            value={ngoId}
            onChange={(e) => setNgoId(e.target.value)}
            placeholder="Filled automatically when you donate from an NGO page"
          />
          <span className="small muted">
            If you opened this form from a profile, this is usually filled in for you.
          </span>
        </label>

        {(donationType === 'DIRECT_MONETARY' || donationType === 'PACKAGE') && (
          <>
            <label>
              Amount
              <input
                type="number"
                min="0"
                step="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
              />
            </label>
            <label>
              Currency
              <input value={currency} onChange={(e) => setCurrency(e.target.value)} />
            </label>
          </>
        )}

        {donationType === 'PACKAGE' && (
          <label>
            Support bundle reference
            <input
              value={packageId}
              onChange={(e) => setPackageId(e.target.value)}
              required
            />
            <span className="small muted">
              Set automatically when you use “Sponsor this bundle” on an NGO page.
            </span>
          </label>
        )}

        {donationType === 'ITEM' && (
          <>
            <label>
              What are you donating?
              <input value={itemName} onChange={(e) => setItemName(e.target.value)} />
            </label>
            <label>
              Details (condition, size, quantity, etc.)
              <textarea value={itemDescription} onChange={(e) => setItemDescription(e.target.value)} />
            </label>
          </>
        )}

        <label>
          Photos (optional)
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={(e) => setFiles(Array.from(e.target.files || []))}
          />
        </label>

        <button type="submit" className="btn btn-primary">
          Submit donation
        </button>
      </form>
    </div>
  );
}
