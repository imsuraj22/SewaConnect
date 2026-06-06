import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/** Sends logged-in NGO users from Home to their workspace. */
export default function NgoHomeRedirect() {
  const { hasRole, isAuthenticated } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated || !hasRole('NGO')) return;
    if (location.pathname === '/') {
      navigate('/ngo-dashboard', { replace: true });
    }
  }, [isAuthenticated, hasRole, location.pathname, navigate]);

  return null;
}
