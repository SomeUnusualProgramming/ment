# Document Risk Analyzer

A full-stack web application for analyzing documents and assessing risk using LLM integration. The system classifies documents and provides comprehensive risk analysis based on multiple frameworks (OWASP, NIST, ISO27001, GDPR).

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Docker Deployment](#docker-deployment)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Key Features](#key-features)
- [Development](#development)

## Overview

**Document Risk Analyzer** is a document processing platform that:
- Allows users to upload various document formats (PDF, DOCX, TXT, JSON, CSV, IMAGE)
- Automatically classifies documents into categories (CONTRACT, INVOICE, REPORT, POLICY, AGREEMENT, FORM, OTHER)
- Performs risk analysis on classified documents
- Supports multiple risk assessment frameworks
- Provides role-based access control (ADMIN, ANALYST, VIEWER)

**Version**: 1.0.0  
**Status**: Active development (authentication and LLM integration in progress)

## Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Core language |
| Spring Boot | 3.2.0 | Web framework |
| Spring Data JPA | Latest | ORM and database access |
| PostgreSQL | 13+ | Primary database |
| Lombok | Latest | Boilerplate reduction |
| JUnit 5 | Latest | Testing framework |
| Mockito | Latest | Test mocking |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.2.0 | UI framework |
| React Router | 6.8.0 | Client-side routing |
| Axios | 1.3.0 | HTTP client |
| Vite | 4.1.0 | Build tool |
| Tailwind CSS | 3.2.4 | Styling |
| React Dropzone | 14.2.3 | File upload component |
| React Hot Toast | 2.4.0 | Notifications |
| Date-fns | 2.29.0 | Date utilities |

### DevOps
| Tool | Purpose |
|------|---------|
| Docker | Containerization |
| Docker Compose | Multi-container orchestration |
| Maven | Java build management |
| npm | JavaScript dependency management |

## Project Structure

### Backend
```
src/main/java/com/document/analyzer/
├── controller/              # REST API endpoints
│   ├── UserController.java
│   ├── UploadController.java
│   ├── ClassificationController.java
│   └── RiskAnalysisController.java
├── service/                 # Business logic
│   ├── UserService.java
│   ├── UploadService.java
│   ├── ClassificationService.java
│   ├── RiskAnalysisService.java
│   ├── DocumentService.java
│   ├── IntentAnalyzer.java
│   ├── DocumentClassifier.java
│   ├── RiskFrameworkSelector.java
│   ├── SanityChecker.java
│   └── JsonFormatter.java
├── repository/              # Data access layer
│   ├── UserRepository.java
│   ├── DocumentRepository.java
│   ├── ClassificationRepository.java
│   └── RiskAnalysisRepository.java
├── entity/                  # JPA entities
│   ├── User.java
│   ├── Document.java
│   ├── Classification.java
│   └── RiskAnalysis.java
├── domain/                  # DTOs and domain models
│   ├── DocumentType.java
│   ├── RiskCategory.java
│   ├── AnalysisRequest.java
│   ├── AnalysisResponse.java
│   └── LLMAnalysisResult.java
├── util/                    # Utility classes
│   └── DocumentProcessor.java
└── DocumentAnalyzerApplication.java  # Main entry point

src/main/resources/
├── application.properties   # Spring Boot config
└── schema.sql              # Database schema

src/test/java/com/document/analyzer/service/
├── UserServiceTest.java
├── UploadServiceTest.java
├── ClassificationServiceTest.java
└── RiskAnalysisServiceTest.java
```

### Frontend
```
frontend/src/
├── components/
│   ├── Navigation.jsx
│   └── Navigation.css
├── pages/
│   ├── DashboardPage.jsx
│   ├── DashboardPage.css
│   ├── UploadPage.jsx
│   ├── UploadPage.css
│   ├── ResultsPage.jsx
│   └── ResultsPage.css
├── utils/
│   └── api.js              # Axios API configuration
├── styles/
│   └── (component styles)
├── App.jsx                 # Main App component
├── App.css
├── main.jsx                # React entry point
├── vite.config.js
├── index.html
├── package.json
└── package-lock.json
```

## Setup & Installation

### Prerequisites
- **Java 21** (for backend development)
- **Node.js 18+** (for frontend)
- **PostgreSQL 13+** (or Docker)
- **Maven 3.8+**
- **Git**

### Backend Setup
```bash
# Clone the repository
git clone <repository-url>
cd ment

# Install Java dependencies
mvn clean install

# Create .env or configure application.properties
# Database connection details
# LLM API configuration
```

### Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Create .env.local for API configuration
echo "VITE_API_URL=http://localhost:8080/api" > .env.local
```

## Running the Application

### Local Development (Without Docker)

**Terminal 1 - Start Backend**
```bash
cd /path/to/project
mvn spring-boot:run
```
Backend runs on: `http://localhost:8080`

**Terminal 2 - Start Frontend**
```bash
cd /path/to/project/frontend
npm run dev
```
Frontend runs on: `http://localhost:5173` (Vite default)

### Access Points
- **Frontend**: http://localhost:5173 or http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Database**: localhost:5432

## Docker Deployment

### Start All Services with Docker Compose
```bash
# Build and start containers
docker-compose up --build

# Access services
# Frontend: http://localhost:3000
# Backend: http://localhost:8080/api
# PostgreSQL: localhost:5432
```

### Individual Docker Commands
```bash
# Build backend
docker build -f Dockerfile.backend -t document-analyzer-backend .

# Build frontend
docker build -f Dockerfile.frontend -t document-analyzer-frontend .

# Run backend
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/document_analyzer \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  document-analyzer-backend

# Run frontend
docker run -p 3000:3000 document-analyzer-frontend
```

### Docker Compose Environment Variables
```
# Backend Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/document_analyzer
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update
APP_DOCUMENT_UPLOAD_DIR=/app/uploads
APP_LLM_API_KEY=your-api-key
APP_LLM_ENDPOINT=https://api.openai.com/v1/chat/completions

# Frontend Configuration
VITE_API_URL=http://localhost:8080/api
```

## Database Schema

### Users Table
Stores user accounts with role-based access control.
```
id (BIGSERIAL PRIMARY KEY)
email (VARCHAR 100, UNIQUE)
first_name, last_name (VARCHAR 100)
password_hash (VARCHAR 255)
role (VARCHAR 50) - ADMIN, ANALYST, VIEWER
active (BOOLEAN)
created_at, updated_at (TIMESTAMP)
```

### Documents Table
Stores uploaded document metadata and processing status.
```
id (BIGSERIAL PRIMARY KEY)
file_name, file_path (VARCHAR)
file_size (BIGINT)
document_type (VARCHAR 50)
extracted_text (TEXT)
user_id (BIGINT, FOREIGN KEY -> users)
processing_status (VARCHAR 50)
created_at, updated_at (TIMESTAMP)
```

### Classifications Table
Stores document classification results.
```
id (BIGSERIAL PRIMARY KEY)
document_id (BIGINT, FOREIGN KEY -> documents)
category (VARCHAR 100)
confidence (FLOAT)
classification_reason (TEXT)
raw_classification_result (TEXT)
version (INTEGER)
created_at, updated_at (TIMESTAMP)
```

### Risk Analyses Table
Stores risk assessment results.
```
id (BIGSERIAL PRIMARY KEY)
document_id (BIGINT, FOREIGN KEY -> documents)
overall_risk_level (VARCHAR 50)
risk_score (FLOAT)
identified_risks (TEXT)
mitigation_recommendations (TEXT)
raw_analysis_result (TEXT)
framework (VARCHAR 50) - OWASP, NIST, ISO27001, GDPR, CUSTOM
reviewed (BOOLEAN)
review_notes (VARCHAR 500)
reviewed_by_user_id (BIGINT, FOREIGN KEY -> users)
created_at, updated_at (TIMESTAMP)
```

### Enums

**User Roles**
- ADMIN - Full system access
- ANALYST - Can classify and analyze documents
- VIEWER - Read-only access

**Document Types**
- PDF, DOCX, TXT, JSON, CSV, IMAGE

**Processing Status**
- PENDING, PROCESSING, COMPLETED, FAILED

**Document Categories**
- CONTRACT, INVOICE, REPORT, POLICY, AGREEMENT, FORM, OTHER

**Risk Levels**
- CRITICAL, HIGH, MEDIUM, LOW, MINIMAL

**Analysis Frameworks**
- OWASP, NIST, ISO27001, GDPR, CUSTOM

## API Endpoints

### User Management
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| GET | `/api/users` | List all users | Yes |
| GET | `/api/users/{id}` | Get user by ID | Yes |
| POST | `/api/users` | Create new user | Yes |
| PUT | `/api/users/{id}` | Update user | Yes |
| DELETE | `/api/users/{id}` | Delete user | Yes |

### Document Upload
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| POST | `/api/upload` | Upload document | Yes |
| GET | `/api/documents` | List documents | Yes |
| GET | `/api/documents/{id}` | Get document details | Yes |
| DELETE | `/api/documents/{id}` | Delete document | Yes |

### Classification
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| POST | `/api/classify` | Classify document | Yes |
| GET | `/api/classifications/{docId}` | Get classifications | Yes |
| GET | `/api/classifications/{id}` | Get classification details | Yes |

### Risk Analysis
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---|
| POST | `/api/analyze` | Perform risk analysis | Yes |
| GET | `/api/risk-analysis/{docId}` | Get risk analysis | Yes |
| GET | `/api/risk-analysis/{id}` | Get analysis details | Yes |
| PUT | `/api/risk-analysis/{id}/review` | Review analysis | Yes |

## Key Features

### Current Implementation
- ✅ User management and role-based access control structure
- ✅ Document upload infrastructure
- ✅ Database schema and JPA entities
- ✅ Service layer with business logic scaffolding
- ✅ REST API endpoints
- ✅ Frontend with React and routing
- ✅ Docker containerization

### In Progress / TODO
- 🔄 Password hashing with BCrypt
- 🔄 JWT authentication and authorization
- 🔄 LLM API integration (OpenAI/similar)
- 🔄 Document text extraction
- 🔄 Risk analysis calculations
- 🔄 API key and configuration management
- 🔄 Comprehensive error handling
- 🔄 Request/response logging
- 🔄 API documentation (Swagger/OpenAPI)

### Future Enhancements
- 📋 Caching layer for frequently accessed data
- 📋 Pagination for list endpoints
- 📋 Search and filter functionality
- 📋 Email notifications
- 📋 Audit logging
- 📋 Dashboard analytics
- 📋 Batch processing for multiple documents
- 📋 Export functionality (PDF, Excel)
- 📋 User activity tracking

## Development

### Running Tests
```bash
# Run backend tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run frontend tests
cd frontend
npm test
```

### Code Quality
```bash
# Frontend linting
cd frontend
npm run lint

# Fix linting issues
npm run lint -- --fix
```

### Build Production
```bash
# Backend
mvn clean package

# Frontend
cd frontend
npm run build
```

### Directory for Uploads
Documents uploaded through the API are stored in:
- **Local**: `./uploads/`
- **Docker**: `/app/uploads/` (mapped to `./uploads/`)

### Key Configuration Files
- **Backend Config**: `src/main/resources/application.properties`
- **Database Schema**: `src/main/resources/schema.sql`
- **Frontend Config**: `frontend/vite.config.js`
- **Frontend Environment**: `frontend/.env.local`
- **Docker**: `docker-compose.yml`, `Dockerfile.backend`, `Dockerfile.frontend`

---

**Last Updated**: December 2025  
**Maintainers**: Development Team  
**License**: [Add your license here]
