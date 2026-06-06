import { Link, NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import NgoHomeRedirect from './NgoHomeRedirect';

export default function Layout() {
  const { isAuthenticated, user, logout, hasRole } = useAuth();

  return (
    <div className="layout">
      <header className="site-header">
        <div className="container header-inner">
          <Link to="/" className="logo">
            <span className="logo-mark">S</span>
            <span>SewaConnect</span>
          </Link>
          <nav className="nav-main" aria-label="Primary">
            <NavLink to="/" end>
              Home
            </NavLink>
            <NavLink to="/ngos">Verified NGOs</NavLink>
            {isAuthenticated && (
              <>
                {hasRole('NGO') ? (
                  <>
                    <NavLink to="/ngo-dashboard">NGO workspace</NavLink>
                    <NavLink to="/ngo/bundles">Bundles</NavLink>
                    <NavLink to="/profile">Account</NavLink>
                  </>
                ) : (
                  <>
                    <NavLink to="/donate">Donate</NavLink>
                    <NavLink to="/my-donations">My donations</NavLink>
                    <NavLink to="/profile">My profile</NavLink>
                  </>
                )}
                {hasRole('ADMIN') && (
                  <NavLink to="/admin">Admin</NavLink>
                )}
              </>
            )}
          </nav>
          <div className="nav-auth">
            {isAuthenticated ? (
              <>
                <Link
                  className="user-pill"
                  to={hasRole('NGO') ? '/ngo-dashboard' : '/profile'}
                  title={user?.email || 'My profile'}
                >
                  {user?.username}
                </Link>
                <button type="button" className="btn btn-ghost" onClick={logout}>
                  Sign out
                </button>
              </>
            ) : (
              <>
                <Link className="btn btn-ghost" to="/login">
                  Sign in
                </Link>
                <Link className="btn btn-primary" to="/register">
                  Join
                </Link>
              </>
            )}
          </div>
        </div>
      </header>
      <main className="site-main">
        <NgoHomeRedirect />
        <Outlet />
      </main>
      <footer className="site-footer">
        <div className="container footer-grid">
          <div>
            <strong>SewaConnect</strong>
            <p className="muted small">
              Connecting people who want to help with NGOs that have been
              through verification—so every gift has a clearer path from intention
              to impact.
            </p>
          </div>
          <div>
            <strong>Need help?</strong>
            <p className="muted small">
              Sign in to browse verified partners, record a donation, or open
              your organization workspace. New here? Start from Home or create
              an account.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
