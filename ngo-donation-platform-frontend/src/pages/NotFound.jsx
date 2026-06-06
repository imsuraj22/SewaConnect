import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="container" style={{ padding: '3rem 0', textAlign: 'center' }}>
      <h1>404</h1>
      <p className="muted">This page does not exist.</p>
      <Link className="btn btn-primary" to="/">
        Home
      </Link>
    </div>
  );
}
