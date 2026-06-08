import { NavLink } from 'react-router-dom';
import { Home, Car, Map, BookOpen, FileText, Settings, CalendarDays, ArchiveBox, MessageCircle } from 'lucide-react';
import './MobileBottomNav.css';

const MobileBottomNav = () => {
  // Define all the navigation items we want in the slideable bottom bar
  const navItems = [
    { name: 'Home', path: '/', icon: Home },
    { name: 'Carpool', path: '/carpool', icon: Car },
    { name: 'Map', path: '/campus-map', icon: Map },
    { name: 'Timetable', path: '/timetable', icon: CalendarDays },
    { name: 'Books', path: '/marketplace', icon: BookOpen },
    { name: 'Notes', path: '/notes', icon: FileText },
    { name: 'Papers', path: '/past-papers', icon: ArchiveBox }, // Using ArchiveBox as placeholder for past papers
    { name: 'Lost/Found', path: '/lost-found', icon: MessageCircle },
    { name: 'Admin', path: '/admin', icon: Settings },
  ];

  return (
    <nav className="mobile-bottom-nav">
      <div className="mobile-nav-track">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => 
                `mobile-nav-item ${isActive ? 'active' : ''}`
              }
            >
              <div className="mobile-nav-icon">
                <Icon size={20} strokeWidth={2.5} />
              </div>
              <span className="mobile-nav-label">{item.name}</span>
            </NavLink>
          );
        })}
      </div>
    </nav>
  );
};

export default MobileBottomNav;
