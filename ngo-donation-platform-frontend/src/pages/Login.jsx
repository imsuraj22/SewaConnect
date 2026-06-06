import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { roleMatches, useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/ngos';

  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    try {
      const data = await login(usernameOrEmail.trim(), password);
      const roles = data?.user?.roles || [];
      const isNgo = roleMatches(roles, 'NGO');
      const isAdmin = roleMatches(roles, 'ADMIN');
      let target = from;
      if (isAdmin) target = '/admin';
      else if (isNgo) target = '/ngo-dashboard';
      navigate(target, { replace: true });
    } catch (err) {
      setError(err.message || 'Sign-in failed');
    }
  }

  return (
    <div className="container" style={{ padding: '2rem 0 3rem' }}>
      <h1>Sign in</h1>
      <p className="muted">
        Welcome back. Use the username or email you registered with and your
        password to continue browsing NGOs, recording donations, or managing
        your organization workspace.
      </p>
      {error && (
        <p className="alert alert-error" role="alert">
          {error}
        </p>
      )}
      <form className="form-stack" onSubmit={handleSubmit}>
        <label>
          Username or email
          <input
            autoComplete="username"
            value={usernameOrEmail}
            onChange={(e) => setUsernameOrEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <button type="submit" className="btn btn-primary">
          Sign in
        </button>
      </form>
      <p className="small muted" style={{ marginTop: '1.5rem' }}>
        No account yet? <Link to="/register">Create one</Link>
      </p>
    </div>
  );
}
