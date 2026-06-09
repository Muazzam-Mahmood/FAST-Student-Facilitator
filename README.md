# FAST Student Facilitator (FSF)

A full-stack campus super-app built for FAST-NUCES students as a Software Design & Architecture (SDA) capstone project. FSF centralizes essential academic and campus-life services into a single, mobile-responsive web application restricted to verified university accounts.

**Live Application:** https://fast-student-facilitator.vercel.app

---

## Overview

FSF was designed to solve real student pain points — finding notes, coordinating rides, tracking timetables, and staying on top of campus events — all from one platform. Access is restricted exclusively to `@nu.edu.pk` Google accounts via OAuth2, ensuring a trusted, university-only environment.

---

## Features

| Module | Description |
|---|---|
| **Carpooling** | Peer-to-peer ride sharing with admin moderation (max 5 checkpoints) |
| **Fast Notes** | Student note-sharing and upload platform |
| **Past Papers** | Repository of past exam papers organized by course |
| **Book Exchange** | Peer-to-peer marketplace for buying and selling textbooks |
| **Timetable Manager** | Admin uploads Excel/CSV schedules; students search by course or teacher |
| **Campus Map** | Interactive map of FAST-NUCES Lahore campus locations |
| **Event Board** | Campus event announcements and listings |
| **Lost & Found** | Report and search lost items on campus |
| **Pop Reminders** | Notification and reminder system for students |
| **Admin Dashboard** | Moderation panel with analytics charts (Recharts) for all user content |

---

## Architecture

The backend follows a **3-Layered Architecture**:

```
Presentation Layer   →   Controllers (REST API endpoints)
Business Logic Layer →   Services & Domain Models
Data Access Layer    →   Spring Data JPA Repositories (PostgreSQL)
```

The project is organized using **Domain-Driven Design (DDD)**, with each feature isolated into its own module (`carpool`, `notes`, `books`, `timetable`, etc.).

### Design Patterns Implemented

This project explicitly implements 6 Gang-of-Four (GoF) design patterns as part of the SDA curriculum:

| Pattern | Location | Purpose |
|---|---|---|
| **Factory Method** | `RideOfferFactory`, `RideSearchCriterionFactory` | Encapsulates object creation and validation logic |
| **Template Method** | `ApproveRideWorkflow`, `ExcelTimetableProcessor` | Defines algorithm skeleton; subclasses override steps |
| **Observer** | `ApplicationEventPublisher` | Decouples main logic from side effects (logging, analytics) |
| **Singleton** | Spring Controllers | Managed as single instances by the Spring IoC container |
| **State** | Moderation workflow | Manages transitions: Pending → Approved → Flagged |
| **Adapter** | `RideController`, `TimetableEntryController` | Bridges HTTP layer with domain logic |

---

## Tech Stack

**Frontend**
- React 19, Vite, React Router DOM v7
- Recharts (data visualization)
- Vanilla CSS (mobile-first, themed)

**Backend**
- Java 17, Spring Boot 3.2
- Spring Security, Spring Data JPA (Hibernate)
- Apache POI (Excel/CSV parsing)
- Maven

**Database**
- PostgreSQL hosted on Neon (serverless)

**Deployment**
- Frontend: Vercel (with API rewrite rules routing to backend)
- Backend: Docker (multi-stage build) hosted on Render
- Database: Neon serverless PostgreSQL
- CI/CD: Continuous delivery on every push

---

## Project Structure

```
FAST-Student-Facilitator/
├── frontend/                  # React + Vite application
│   ├── src/
│   │   ├── pages/             # Feature pages (Carpool, Notes, AdminPanel, etc.)
│   │   ├── components/        # Reusable UI components (Topbar, IconRail, etc.)
│   │   └── utils/             # API wrappers and helper functions
│   └── vercel.json            # Vercel rewrite rules for API routing
│
├── backend/                   # Spring Boot application
│   ├── src/main/java/com/fast/fsf/
│   │   ├── carpool/           # Carpooling module
│   │   ├── notes/             # Notes module
│   │   ├── books/             # Book Exchange module
│   │   ├── timetable/         # Timetable module
│   │   ├── pastpapers/        # Past Papers module
│   │   ├── lostfound/         # Lost & Found module
│   │   ├── events/            # Event Board module
│   │   ├── reminders/         # Reminders module
│   │   ├── campusmap/         # Campus Map module
│   │   ├── admin/             # Admin Panel & moderation
│   │   ├── auth/              # Authentication & session management
│   │   └── config/            # Security, CORS, database config
│   └── Dockerfile             # Multi-stage Docker build
│
└── Resources/                 # SDA deliverables
    ├── FSF_SRS.docx           # Software Requirements Specification
    ├── FSF_SAD.docx           # Software Architecture Document
    ├── Team2_Phase-1_Deliverable.docx
    └── Team2_Phase2_Deliverable.docx
```

---

## Running Locally

### Prerequisites
- Node.js 18+
- Java 17+
- Maven 3.8+
- PostgreSQL (or a Neon connection string)

### Frontend

```bash
cd frontend
cp .env.example .env
# Add your Google OAuth Client ID to .env
npm install
npm run dev
```

### Backend

```bash
cd backend
cp application-local.properties.example application-local.properties
# Add your PostgreSQL password to application-local.properties
./mvnw spring-boot:run
```

The backend runs on `http://localhost:8080` and the frontend on `http://localhost:5173`.

---

## Deployment

**Frontend (Vercel)**

The `vercel.json` rewrites all `/api/*` requests to the Render backend, so the frontend and backend appear on the same domain from the browser's perspective.

**Backend (Docker + Render)**

```bash
docker build -t fsf-backend .
docker run -p 8080:8080 fsf-backend
```

The Dockerfile uses a multi-stage build — Maven compiles the JAR in stage 1, and a slim JRE image runs it in stage 2. JVM flags are tuned for low-memory cloud hosting.

---

## SDA Deliverables

This project was developed as part of the **Software Design & Architecture** course at FAST-NUCES. The following formal documents were produced and reviewed:

- Software Requirements Specification (SRS)
- Software Architecture Document (SAD)
- Phase 1 Deliverable — Domain modeling, use cases, initial design
- Phase 2 Deliverable — Finalized architecture, pattern implementation, deployment

---

## Developers

- Muhammad Muazzam Mahmood
- Muhammad Huzaifa
- Muhammad Sheharyar Waheed
- Muhammad Anas
- Arqam Hafeez

FAST-NUCES Lahore — Spring 2026

Repository: https://github.com/Muazzam-Mahmood/FAST-Student-Facilitator
