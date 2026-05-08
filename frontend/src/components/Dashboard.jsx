import React, { useState, useEffect } from 'react';
import FeatureCarousel from './FeatureCarousel';
import AdminReportChart from './AdminReportChart';
import './Dashboard.css';

/**
 * Dashboard Component
 *
 * What is this component?
 * This is the main landing view. It contains the welcome banner,
 * quick stats, and the feature carousel.
 */
const Dashboard = ({ user }) => {
  const [rideCount, setRideCount] = useState(0);
  const [paperCount, setPaperCount] = useState(0);
  const [bookListingCount, setBookListingCount] = useState(0);
  const [userCount, setUserCount] = useState(0);
  const [flaggedCount, setFlaggedCount] = useState(0);
  const [pendingCount, setPendingCount] = useState(0);
  const isAdmin = user?.role === 'ADMIN';

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [ridesRes, papersRes, booksRes] = await Promise.all([
          fetch('http://localhost:8080/api/rides/count/active'),
          fetch('http://localhost:8080/api/past-papers/count/active'),
          fetch('http://localhost:8080/api/books/count/active')
        ]);
        
        const rCount = await ridesRes.json();
        const pCount = await papersRes.json();
        const bCount = await booksRes.json();
        
        setRideCount(rCount);
        // We can add state for other counts if we want to display them individually
        setPaperCount(pCount);
        setBookListingCount(bCount);

        if (isAdmin) {
          const [userCountRes, flaggedRidesRes, flaggedPapersRes, flaggedBooksRes, pendingRidesRes, pendingPapersRes, pendingBooksRes] = await Promise.all([
            fetch('http://localhost:8080/api/users/count'),
            fetch('http://localhost:8080/api/rides/flagged/count'),
            fetch('http://localhost:8080/api/past-papers/flagged/count'),
            fetch('http://localhost:8080/api/books/flagged/count'),
            fetch('http://localhost:8080/api/rides/pending'),
            fetch('http://localhost:8080/api/past-papers/pending'),
            fetch('http://localhost:8080/api/books/pending')
          ]);

          setUserCount(await userCountRes.json());
          
          const fRides = await flaggedRidesRes.json();
          const fPapers = await flaggedPapersRes.json();
          const fBooks = await flaggedBooksRes.json();
          setFlaggedCount(fRides + fPapers + fBooks);

          const [pRides, pPapers, pBooks] = await Promise.all([
            pendingRidesRes.json(),
            pendingPapersRes.json(),
            pendingBooksRes.json()
          ]);
          
          setPendingCount(pRides.length + pPapers.length + pBooks.length);
        }
      } catch (err) {
        console.error("Failed to fetch dashboard stats", err);
      }
    };
    fetchData();
  }, [isAdmin]);

  return (
    <div className="dashboard">
      {/* Welcome Banner */}
      <section className="welcome-banner glass-card">
        <div className="banner-content">
          <h1>Welcome back, {user?.name || 'Student'}!</h1>
          <p>FAST-NUCES Lahore</p>
        </div>
      </section>

      {/* Feature Section with 3-Card Carousel */}
      <section className="dashboard-section">
        <div className="section-header">
          <h3>Services & Tools</h3>
          <span className="swipe-hint">Swipe to see more</span>
        </div>

        <FeatureCarousel />
      </section>

      {/* Admin Visual Report (Replaces Cards) */}
      {isAdmin && (
        <section className="dashboard-section">
          <h3>Admin GUI Reports</h3>
          <AdminReportChart 
            stats={{
              users: userCount,
              rides: rideCount,
              papers: paperCount,
              books: bookListingCount,
              pending: pendingCount,
              flagged: flaggedCount
            }} 
          />
        </section>
      )}

      {!isAdmin && (
        <section className="dashboard-section">
          <h3>Live Updates</h3>
          <div className="stats-grid">
            <div className="stat-card glass-card">
              <p className="stat-label">Active Rides</p>
              <p className="stat-value">{rideCount}</p>
            </div>
            <div className="stat-card glass-card">
              <p className="stat-label">Past Papers</p>
              <p className="stat-value">{paperCount}</p>
            </div>
            <div className="stat-card glass-card">
              <p className="stat-label">Books for Sale</p>
              <p className="stat-value">{bookListingCount}</p>
            </div>
          </div>
        </section>
      )}
    </div>
  );
};

export default Dashboard;
