import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, roles, donorOnly }) {
  const { isAuthenticated, hasRole } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (roles?.length && !roles.some((r) => hasRole(r))) {
    return <Navigate to="/" replace />;
  }

  if (donorOnly && hasRole('NGO')) {
    return <Navigate to="/ngo-dashboard" replace />;
  }

  return children;
}
