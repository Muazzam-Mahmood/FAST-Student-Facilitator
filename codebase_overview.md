# FSF Project: Comprehensive Codebase Overview

The **FAST Student Facilitator (FSF)** is a full-stack web application designed to help students at FAST-NUCES (specifically Lahore campus) manage academic and campus-life activities.

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.x (Java 17+)
- **Database**: PostgreSQL (Primary), H2 (Local testing)
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven
- **Security**: Spring Security (RBAC and Placeholder for OAuth2)
- **Integrations**: Excel/CSV parsing (Apache POI)

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **Routing**: React Router DOM v6
- **Styling**: Vanilla CSS (Themed)
- **State Management**: React `useState`/`useEffect` + `localStorage` for persistence

---

## 📂 Project Structure

### Backend (`/backend`)
The backend is organized by feature modules, following a DDD (Domain Driven Design) inspired structure combined with explicit Design Pattern folders.

- **`com.fast.fsf`**:
  - `admin`: Moderation and administration logic.
  - `auth`: User authentication and session management.
  - `carpool`: Ride offering and searching (Uses **Factory**, **Template**, **Observer**).
  - `timetable`: Management of campus schedules (Uses **Template Method** for Excel/CSV parsing).
  - `pastpapers`: Repository for academic materials.
  - `books`: Peer-to-peer marketplace for textbooks.
  - `lostfound`: Reporting and searching lost items.
  - `notes`: Student note-sharing platform.
  - `reminders`: Notification and reminder system.
  - `campusmap`: Geospatial data for campus locations.
  - `config`: Global configuration (Security, Database seeding).
  - `shared`: Common utilities and base classes.

### Frontend (`/frontend`)
A standard React application layout.

- **`src/pages`**: Contains the main view components (e.g., `Carpool.jsx`, `TimetableManager.jsx`, `AdminPanel.jsx`).
- **`src/components`**: Reusable UI elements (`IconRail`, `Topbar`, `Dashboard`).
- **`src/styles`**: Global CSS variables and utility styles.
- **`src/utils`**: API wrappers and helper functions.

---

## 🏛 Architectural Patterns

Given this is a **Software Design & Architecture** project, several GoF (Gang of Four) patterns are explicitly implemented:

| Pattern | Usage Location | Purpose |
| :--- | :--- | :--- |
| **Singleton** | Spring Controllers | Managed as single instances by the Spring IOC container. |
| **Factory Method** | `RideOfferFactory`, `RideSearchCriterionFactory` | Encapsulates object creation and validation logic. |
| **Template Method** | `ApproveRideWorkflow`, `ExcelTimetableProcessor` | Defines the skeleton of an algorithm (e.g., moderation steps) while allowing subclasses to override specific steps. |
| **Observer** | `ApplicationEventPublisher` | Decouples main logic from side effects like logging, notifications, or analytics. |
| **State** | Moderation logic | Manages object transitions (Pending -> Approved -> Flagged). |
| **Adapter** | `RideController`, `TimetableEntryController` | Acts as an adapter between the HTTP world and the domain logic. |

---

## 🚀 Key Workflows

1. **User Authentication**: Simple login using `localStorage` for session persistence (UC-24).
2. **Carpool Management**: Students can offer rides; admins must approve or flag listings based on SRS rules (max 5 checkpoints).
3. **Timetable Management**: Admins can upload Excel/CSV files which are parsed and persisted to provide searchable schedules for students.
4. **Admin Moderation**: A central panel for managing all user-generated content (Rides, Notes, Books, etc.) including flagging and deletion with reasons.
