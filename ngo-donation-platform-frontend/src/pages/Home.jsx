import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <>
      <section className="page-hero">
        <div className="container">
          <p className="pill">Purpose-led giving</p>
          <h1>Give with clarity. Support organizations you can trust.</h1>
          <p className="lead">
            SewaConnect helps you discover verified NGOs, understand what they
            need, and record your support—whether you give money, sponsor a
            published bundle of items, or offer something specific from your
            own hands.
          </p>
          <p style={{ marginTop: '1.25rem', display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
            {isAuthenticated ? (
              <>
                <Link className="btn btn-primary" to="/ngos">
                  Browse verified NGOs
                </Link>
                <Link className="btn btn-ghost" to="/donate">
                  Make a donation
                </Link>
              </>
            ) : (
              <>
                <Link className="btn btn-primary" to="/register">
                  Create a free account
                </Link>
                <Link className="btn btn-ghost" to="/login">
                  I already have an account
                </Link>
              </>
            )}
          </p>
        </div>
      </section>

      <section className="section">
        <div className="container">
          <div className="stat-strip" aria-label="What you can expect">
            <div>
              <strong>Verified</strong>
              <span className="muted small">Organizations are reviewed before they appear as verified partners.</span>
            </div>
            <div>
              <strong>Clear choices</strong>
              <span className="muted small">See profiles, impact stories, and optional support bundles in plain language.</span>
            </div>
            <div>
              <strong>Your history</strong>
              <span className="muted small">Signed-in donors can track what they offered and how it was recorded.</span>
            </div>
          </div>
        </div>
      </section>

      <section className="section" id="mission">
        <div className="container grid-2">
          <div>
            <h2>Why SewaConnect exists</h2>
            <p className="muted">
              Giving should feel grounded, not vague. We focus on{" "}
              <strong>traceable intent</strong>: you pick who you stand behind,
              you can align with a published bundle (for example a school kit at
              a set amount), and NGOs can publish only what they are ready to
              receive—so expectations stay realistic on both sides.
            </p>
            <p className="muted">
              The goal is simple: fewer mismatched gifts, more dignity for
              communities, and more confidence for everyone who opens their
              wallet or their cupboard.
            </p>
          </div>
          <div className="card">
            <h3 style={{ marginTop: 0 }}>What we stand for</h3>
            <ul className="muted" style={{ paddingLeft: '1.2rem' }}>
              <li>
                <strong>Safety by design:</strong> new organizations go through a
                review before they are shown as verified partners to donors.
              </li>
              <li>
                <strong>Respect for roles:</strong> day-to-day giving and
                organization tools are separated from platform administration, so
                people only see what is relevant to them.
              </li>
              <li>
                <strong>Straightforward access:</strong> if something needs an
                account, we say so—so you are never stuck guessing why a screen
                looks empty.
              </li>
            </ul>
          </div>
        </div>
      </section>

      <section className="section" style={{ background: '#fff', borderBlock: '1px solid var(--color-border)' }}>
        <div className="container">
          <h2>For donors</h2>
          <div className="grid-3">
            <article className="card">
              <h3 style={{ marginTop: 0 }}>1 · Create an account</h3>
              <p className="muted small">
                Sign up in a minute. Your account lets you browse verified
                organizations, save donations, and pick up where you left off.
              </p>
            </article>
            <article className="card">
              <h3 style={{ marginTop: 0 }}>2 · Explore</h3>
              <p className="muted small">
                Open profiles that have completed verification. Read how each
                NGO works and which support bundles they have published, if any.
              </p>
            </article>
            <article className="card">
              <h3 style={{ marginTop: 0 }}>3 · Give</h3>
              <p className="muted small">
                Record a money gift, sponsor a bundle, or describe an in-kind
                item. You can attach photos so teams understand what you are
                offering.
              </p>
            </article>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="container">
          <h2>For NGOs</h2>
          <div className="grid-2">
            <article className="card">
              <h3 style={{ marginTop: 0 }}>Joining the platform</h3>
              <p className="muted small">
                Register with username, email, and password only. Complete your
                organization profile in NGO workspace, submit for review, and stay
                inactive until an administrator approves you.
              </p>
            </article>
            <article className="card">
              <h3 style={{ marginTop: 0 }}>Your workspace</h3>
              <p className="muted small">
                Update how donors find you, upload documents your process
                requires, and publish support bundles (title, amount, and what is
                included) so donors can choose them confidently.
              </p>
            </article>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="container grid-2">
          <div>
            <h2>How oversight fits in</h2>
            <p className="muted">
              Platform administrators review new organizations, handle serious
              concerns, and can pause or remove listings when needed. Donations
              move through clear stages—such as pending, accepted, or withdrawn—so
              both sides know where things stand.
            </p>
            <p className="muted">
              Behind the scenes, requests between donors and NGOs are coordinated
              so information stays consistent without you having to manage
              spreadsheets or long message threads alone.
            </p>
          </div>
          <div className="card">
            <h3 style={{ marginTop: 0 }}>Not sure where to start?</h3>
            <p className="muted small" style={{ marginBottom: 0 }}>
              If you mainly want to give, register as a donor. If you officially
              represent an NGO that should appear on the platform, register with
              that option so we can review your organization. When in doubt, use
              the contact your team already uses for platform or compliance
              questions.
            </p>
          </div>
        </div>
      </section>

      <section className="section" style={{ paddingBottom: '3rem' }}>
        <div className="container card" style={{ textAlign: 'center' }}>
          <h2 style={{ marginTop: 0 }}>Start when you are ready</h2>
          <p className="muted" style={{ maxWidth: '50ch', marginInline: 'auto' }}>
            Whether you want to give once or build an ongoing relationship with
            a cause you care about, the next step is opening an account—or
            signing back in if you already have one.
          </p>
          {!isAuthenticated && (
            <p style={{ marginTop: '1rem' }}>
              <Link className="btn btn-primary" to="/register">
                Get started
              </Link>
            </p>
          )}
        </div>
      </section>
    </>
  );
}
