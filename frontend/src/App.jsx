import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import IconRail from './components/IconRail';
import Topbar from './components/Topbar';
import MobileBottomNav from './components/MobileBottomNav';
import Dashboard from './components/Dashboard';
import Login from './pages/Login';
import Carpool from './pages/Carpool';
import AdminPanel from './pages/AdminPanel';
import Stats from './pages/Stats';
import LostAndFound from './pages/LostAndFound';
import FastNotes from './pages/FastNotes';
import ServiceSkeleton from './components/ServiceSkeleton';
import PastPapers from './pages/PastPapers';
import TimetableManager from './pages/TimetableManager';
import BookExchange from './pages/BookExchange';
import CampusEventBoard from './pages/CampusEventBoard';
import PopReminders from './pages/PopReminders';
import ReminderLoginPopup from './components/ReminderLoginPopup';
import ReminderNotifier from './components/ReminderNotifier';
import CampusMap from './pages/CampusMap';
import './App.css';

/**
 * App.jsx
 * 
 * We now use 'react-router-dom' to handle navigation between features.
 */

function RouteTitleUpdater() {
  const location = useLocation();
  React.useEffect(() => {
    const routeTitles = {
      '/': 'Dashboard | FAST Student Facilitator',
      '/carpool': 'Carpool | FAST Student Facilitator',
      '/admin': 'Admin Panel | FAST Student Facilitator',
      '/stats': 'Stats | FAST Student Facilitator',
      '/lost-found': 'Lost & Found | FAST Student Facilitator',
      '/past-papers': 'Past Papers | FAST Student Facilitator',
      '/events': 'Campus Events | FAST Student Facilitator',
      '/reminders': 'Reminders | FAST Student Facilitator',
      '/campus-map': 'Campus Map | FAST Student Facilitator',
      '/timetable': 'Timetable | FAST Student Facilitator',
      '/marketplace': 'Book Exchange | FAST Student Facilitator',
      '/notes': 'Fast Notes | FAST Student Facilitator'
    };
    document.title = routeTitles[location.pathname] || 'FAST Student Facilitator | FAST-NUCES Portal';
  }, [location]);
  return null;
}

/**
 * Default mock student session for testing mode.
 * Allows users to access the Student Portal directly without authenticating.
 */
const DEFAULT_STUDENT_USER = {
  id: 1,
  name: 'FAST Student',
  email: 'student@nu.edu.pk',
  role: 'STUDENT',
  picture: ''
};

function App() {
  /* =========================================================================
   * [ORIGINAL AUTHENTICATION LOGIC - KEPT FOR TESTING & PRODUCTION RESTORE]
   * In production with Google OAuth, unauthenticated visitors are redirected to /login.
   *
   * const [user, setUser] = useState(() => {
   *   const savedUser = localStorage.getItem('fsf-user');
   *   try {
   *     return savedUser ? JSON.parse(savedUser) : null;
   *   } catch (e) {
   *     return null;
   *   }
   * });
   *
   * const ProtectedRoute = ({ children }) => {
   *   if (!user) return <Navigate to="/login" />;
   *   return children;
   * };
   * ========================================================================= */

  // --- TESTING BYPASS STATE ---
  // When a user visits the URL, they bypass login and land directly in the student portal
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('fsf-user');
    try {
      return savedUser ? JSON.parse(savedUser) : DEFAULT_STUDENT_USER;
    } catch (e) {
      return DEFAULT_STUDENT_USER;
    }
  });

  // Effective user ensures components never receive null during testing
  const activeUser = user || DEFAULT_STUDENT_USER;

  // UC-24: tracks whether the user just logged in (vs. a page reload of
  // an existing session). The reminder login pop-up only fires when this
  // is true. It is reset by ReminderLoginPopup once shown / dismissed.
  const [justLoggedIn, setJustLoggedIn] = useState(false);
  const [theme, setTheme] = useState(localStorage.getItem('fsf-theme') || 'dark');

  React.useEffect(() => {
    if (user) {
      localStorage.setItem('fsf-user', JSON.stringify(user));
    } else {
      localStorage.removeItem('fsf-user');
    }
  }, [user]);

  React.useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('fsf-theme', theme);
  }, [theme]);

  const toggleTheme = () => setTheme(prev => prev === 'dark' ? 'light' : 'dark');

  // --- TESTING BYPASS ROUTE: Allow immediate direct access into the student portal ---
  const ProtectedRoute = ({ children }) => {
    return children;
  };

  const AdminRoute = ({ children }) => {
    if (!activeUser || activeUser.role !== 'ADMIN') return <Navigate to="/" />;
    return children;
  };

  return (
    <Router>
      <RouteTitleUpdater />
      <div className="app-shell">
        <Routes>
          {/* Login Page: Kept for manual testing / role switching */}
          <Route path="/login" element={<Login onLogin={(userData) => { setUser(userData); setJustLoggedIn(true); }} />} />

          {/* Main Shell: Direct access for testing (no auth required) */}
          <Route path="/*" element={
            <ProtectedRoute>
              <>
                {/* UC-24 login pop-up & UC-25 alt2 in-app notification scheduler */}
                <ReminderLoginPopup
                  user={activeUser}
                  justLoggedIn={justLoggedIn}
                  onDismiss={() => setJustLoggedIn(false)}
                />
                <ReminderNotifier user={activeUser} />
                <div className="app-canvas">
                  <IconRail user={activeUser} onLogout={() => setUser(null)} />
                  <main className="main-viewport">
                    <Topbar theme={theme} toggleTheme={toggleTheme} user={activeUser} />
                    <div className="content-area">
                      <Routes>
                        <Route path="/" element={<Dashboard user={activeUser} />} />
                        <Route path="/carpool" element={<Carpool user={activeUser} />} />
                        <Route path="/admin" element={<AdminRoute><AdminPanel /></AdminRoute>} />
                        <Route path="/stats" element={<AdminRoute><Stats /></AdminRoute>} />
                        <Route path="/lost-found" element={<LostAndFound user={activeUser} />} />
                        <Route path="/past-papers" element={<PastPapers user={activeUser} />} />
                        <Route path="/events" element={<CampusEventBoard user={activeUser} />} />
                        <Route path="/reminders" element={<PopReminders user={activeUser} />} />
                        <Route path="/campus-map" element={<CampusMap user={activeUser} />} />
                        <Route path="/timetable" element={<TimetableManager user={activeUser} />} />
                        <Route path="/marketplace" element={<BookExchange user={activeUser} />} />
                        <Route path="/notes" element={<FastNotes user={activeUser} />} />
                      </Routes>
                    </div>
                  </main>
                  <MobileBottomNav />
                </div>
              </>
            </ProtectedRoute>
          } />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
