import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import {
  clearAuth,
  getStoredToken,
  getStoredUser,
  persistAuth,
} from '../config/api';
import * as authApi from '../api/auth';

const AuthContext = createContext(null);

export function normalizeRoles(roles) {
  if (!roles) return [];
  if (Array.isArray(roles)) return roles.map(String);
  if (typeof roles === 'object') {
    return Object.values(roles).map(String);
  }
  return [String(roles)];
}

export function roleMatches(userRoles, wanted) {
  const normalized = normalizeRoles(userRoles);
  const role = wanted.startsWith('ROLE_') ? wanted : `ROLE_${wanted}`;
  const short = role.replace(/^ROLE_/, '');
  return normalized.some((r) => {
    const s = String(r);
    return s === role || s === short || s === `ROLE_${short}`;
  });
}

function normalizeUser(u) {
  if (!u) return null;
  const id = u.id != null ? Number(u.id) : u.id;
  const roles = normalizeRoles(u.roles);
  return { ...u, id, roles };
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getStoredToken);
  const [user, setUser] = useState(() => normalizeUser(getStoredUser()));

  const setSession = useCallback((accessToken, nextUser) => {
    persistAuth(accessToken, nextUser);
    setToken(accessToken);
    setUser(normalizeUser(nextUser));
  }, []);

  const login = useCallback(
    async (usernameOrEmail, password) => {
      const data = await authApi.login(usernameOrEmail, password);
      setSession(data.accessToken, data.user);
      try {
        const me = await authApi.fetchMe(data.accessToken);
        if (me) {
          setSession(data.accessToken, me);
          return { ...data, user: normalizeUser(me) };
        }
      } catch {
        /* use login payload */
      }
      return { ...data, user: normalizeUser(data.user) };
    },
    [setSession]
  );

  const register = useCallback(
    async (payload) => {
      const data = await authApi.register(payload);
      setSession(data.accessToken, data.user);
      try {
        const me = await authApi.fetchMe(data.accessToken);
        if (me) {
          setSession(data.accessToken, me);
          return { ...data, user: normalizeUser(me) };
        }
      } catch {
        /* use register payload */
      }
      return { ...data, user: normalizeUser(data.user) };
    },
    [setSession]
  );

  const logout = useCallback(() => {
    clearAuth();
    setToken(null);
    setUser(null);
  }, []);

  const refreshMe = useCallback(async () => {
    const activeToken = token || getStoredToken();
    if (!activeToken) return null;
    const me = await authApi.fetchMe(activeToken);
    const next = normalizeUser(me);
    persistAuth(activeToken, next);
    setToken(activeToken);
    setUser(next);
    return next;
  }, [token]);

  useEffect(() => {
    if (token) {
      refreshMe().catch(() => {});
    }
  }, []); // eslint-disable-line react-hooks/exhaustive-deps -- refresh once on load

  const value = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: Boolean(token && user),
      login,
      register,
      logout,
      refreshMe,
      hasRole: (r) => roleMatches(user?.roles, r),
    }),
    [token, user, login, register, logout, refreshMe]
  );

  return (
    <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return ctx;
}
