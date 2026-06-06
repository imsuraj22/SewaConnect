import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [accountType, setAccountType] = useState('donor');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const isNgoSignup = accountType === 'ngo';

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    const roles = accountType === 'ngo' ? ['ROLE_NGO'] : [];
    try {
      await register({
        username: username.trim(),
        email: email.trim(),
        password,
        roles: roles.length ? roles : undefined,
      });
      navigate(isNgoSignup ? '/ngo-dashboard' : '/ngos');
    } catch (err) {
      setError(err.message || 'Registration failed');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>Create an account</h1>
      <p className="muted">
        {isNgoSignup
          ? 'Sign up with username, email, and password only. After you sign in, complete your organization profile in NGO workspace and submit it for admin review before you can go live.'
          : 'Join as a donor with username, email, and password to browse verified NGOs and record support.'}
      </p>
      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      <form className="form-stack" onSubmit={handleSubmit}>
        <label>
          I am joining as
          <select
            value={accountType}
            onChange={(e) => setAccountType(e.target.value)}
          >
            <option value="donor">Donor</option>
            <option value="ngo">NGO representative</option>
          </select>
        </label>
        <label>
          Username
          <input
            autoComplete="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            minLength={3}
          />
        </label>
        <label>
          Email
          <input
            type="email"
            autoComplete="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Password (at least 8 characters)
          <input
            type="password"
            autoComplete="new-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
          />
        </label>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
      <p className="small muted" style={{ marginTop: '1.5rem' }}>
        Platform administrator accounts are created separately for staff. Already
        registered? <Link to="/login">Sign in</Link>
      </p>
    </div>
  );
}
