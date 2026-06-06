import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import NgoDirectory from './pages/NgoDirectory';
import NgoDetail from './pages/NgoDetail';
import Donate from './pages/Donate';
import MyDonations from './pages/MyDonations';
import Profile from './pages/Profile';
import NgoDashboard from './pages/NgoDashboard';
import NgoBundles from './pages/NgoBundles';
import AdminDashboard from './pages/AdminDashboard';
import NotFound from './pages/NotFound';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />
            <Route
              path="ngos"
              element={
                <ProtectedRoute donorOnly>
                  <NgoDirectory />
                </ProtectedRoute>
              }
            />
            <Route
              path="ngos/:id"
              element={
                <ProtectedRoute donorOnly>
                  <NgoDetail />
                </ProtectedRoute>
              }
            />
            <Route
              path="donate"
              element={
                <ProtectedRoute donorOnly>
                  <Donate />
                </ProtectedRoute>
              }
            />
            <Route
              path="my-donations"
              element={
                <ProtectedRoute donorOnly>
                  <MyDonations />
                </ProtectedRoute>
              }
            />
            <Route
              path="profile"
              element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              }
            />
            <Route
              path="ngo-dashboard"
              element={
                <ProtectedRoute>
                  <NgoDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="ngo/bundles"
              element={
                <ProtectedRoute>
                  <NgoBundles />
                </ProtectedRoute>
              }
            />
            <Route
              path="admin"
              element={
                <ProtectedRoute roles={['ADMIN']}>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
