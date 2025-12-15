# Document Risk Analyzer - Complete Project Structure

## Backend Structure

### Controllers (REST API Endpoints)
```
src/main/java/com/document/analyzer/controller/
├── UserController.java              (User management endpoints)
├── UploadController.java            (Document upload endpoints)
├── ClassificationController.java    (Classification endpoints)
└── RiskAnalysisController.java      (Risk analysis endpoints)
```

### Services (Business Logic)
```
src/main/java/com/document/analyzer/service/
├── UserService.java                 (User operations)
├── UploadService.java               (Document upload and processing)
├── ClassificationService.java       (LLM-based classification)
├── RiskAnalysisService.java         (Risk assessment logic)
├── DocumentService.java             (Document operations)
├── IntentAnalyzer.java              (Document intent analysis)
├── DocumentClassifier.java          (Classification logic)
├── RiskFrameworkSelector.java       (Framework selection)
├── SanityChecker.java               (Data validation)
└── JsonFormatter.java               (Response formatting)
```

### Repositories (Data Access)
```
src/main/java/com/document/analyzer/repository/
├── UserRepository.java              (User database queries)
├── DocumentRepository.java          (Document database queries)
├── ClassificationRepository.java    (Classification database queries)
└── RiskAnalysisRepository.java      (Risk analysis database queries)
```

### Entities (JPA Models)
```
src/main/java/com/document/analyzer/entity/
├── User.java                        (User entity with roles)
├── Document.java                    (Document entity)
├── Classification.java              (Classification result entity)
└── RiskAnalysis.java                (Risk analysis entity)
```

### Utilities
```
src/main/java/com/document/analyzer/util/
└── DocumentProcessor.java           (Document processing utilities)
```

### Domain Models (DTOs)
```
src/main/java/com/document/analyzer/domain/
├── DocumentType.java                (Enum for document types)
├── RiskCategory.java                (Enum for risk categories)
├── AnalysisRequest.java             (Analysis request DTO)
├── AnalysisResponse.java            (Analysis response DTO)
└── LLMAnalysisResult.java           (LLM result DTO)
```

### Configuration
```
src/main/resources/
├── application.properties           (Spring Boot configuration)
└── schema.sql                       (PostgreSQL database schema)
```

### Unit Tests
```
src/test/java/com/document/analyzer/service/
├── UserServiceTest.java             (User service tests)
├── UploadServiceTest.java           (Upload service tests)
├── ClassificationServiceTest.java   (Classification service tests)
└── RiskAnalysisServiceTest.java     (Risk analysis service tests)
```

### Root Configuration
```
pom.xml                             (Maven dependencies and build config)
DocumentAnalyzerApplication.java    (Spring Boot main class)
```

## Frontend Structure

### React Components
```
frontend/src/components/
├── Navigation.jsx                   (Navigation bar component)
└── Navigation.css                   (Navigation styles)
```

### Pages
```
frontend/src/pages/
├── UploadPage.jsx                   (Document upload page)
├── ResultsPage.jsx                  (Analysis results page)
└── DashboardPage.jsx                (Dashboard/home page)
```

### Styles
```
frontend/src/styles/
├── UploadPage.css                   (Upload page styles)
├── ResultsPage.css                  (Results page styles)
└── DashboardPage.css                (Dashboard page styles)
```

### Utilities
```
frontend/src/utils/
└── api.js                           (API service and axios configuration)
```

### Entry Points
```
frontend/src/
├── App.jsx                          (Main App component)
├── App.css                          (App global styles)
├── main.jsx                         (React DOM render entry)
├── vite.config.js                   (Vite configuration)
├── package.json                     (NPM dependencies)
└── index.html                       (HTML entry point)
```

## Database Schema

### Tables
1. **users** - User accounts and authentication
   - id (BIGSERIAL PRIMARY KEY)
   - email (VARCHAR 100, UNIQUE)
   - first_name, last_name (VARCHAR 100)
   - password_hash (VARCHAR 255)
   - role (VARCHAR 50) - ADMIN, ANALYST, VIEWER
   - active (BOOLEAN)
   - created_at, updated_at (TIMESTAMP)

2. **documents** - Uploaded documents
   - id (BIGSERIAL PRIMARY KEY)
   - file_name, file_path (VARCHAR)
   - file_size (BIGINT)
   - document_type (VARCHAR 50)
   - extracted_text (TEXT)
   - user_id (BIGINT, FOREIGN KEY)
   - processing_status (VARCHAR 50)
   - created_at, updated_at (TIMESTAMP)

3. **classifications** - Document classifications
   - id (BIGSERIAL PRIMARY KEY)
   - document_id (BIGINT, FOREIGN KEY)
   - category (VARCHAR 100)
   - confidence (FLOAT)
   - classification_reason (TEXT)
   - raw_classification_result (TEXT)
   - version (INTEGER)
   - created_at, updated_at (TIMESTAMP)

4. **risk_analyses** - Risk assessment results
   - id (BIGSERIAL PRIMARY KEY)
   - document_id (BIGINT, FOREIGN KEY)
   - overall_risk_level (VARCHAR 50)
   - risk_score (FLOAT)
   - identified_risks (TEXT)
   - mitigation_recommendations (TEXT)
   - raw_analysis_result (TEXT)
   - framework (VARCHAR 50)
   - reviewed (BOOLEAN)
   - review_notes (VARCHAR 500)
   - reviewed_by_user_id (BIGINT, FOREIGN KEY)
   - created_at, updated_at (TIMESTAMP)

## Key Enums

### User.UserRole
- ADMIN - Full system access
- ANALYST - Can classify and analyze documents
- VIEWER - Read-only access

### Document.DocumentType
- PDF, DOCX, TXT, JSON, CSV, IMAGE

### Document.ProcessingStatus
- PENDING, PROCESSING, COMPLETED, FAILED

### Classification.DocumentCategory
- CONTRACT, INVOICE, REPORT, POLICY, AGREEMENT, FORM, OTHER

### RiskAnalysis.RiskLevel
- CRITICAL, HIGH, MEDIUM, LOW, MINIMAL

### RiskAnalysis.AnalysisFramework
- OWASP, NIST, ISO27001, GDPR, CUSTOM

## API Response Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Successful deletion |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error |

## Technology Stack

### Backend
- Java 17/21
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL 13+
- JUnit 5
- Mockito
- Lombok

### Frontend
- React 18.2
- React Router 6.8
- Axios 1.3
- Vite 4.1
- Tailwind CSS 3.2
- React Dropzone 14.2
- Date-fns 2.29

## File Dependencies

### Backend Dependencies (pom.xml)
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- postgresql (JDBC driver)
- jackson-databind
- lombok
- junit-jupiter
- mockito-core
- mockito-junit-jupiter

### Frontend Dependencies (package.json)
- react
- react-dom
- react-router-dom
- axios
- tailwindcss
- react-dropzone
- react-hot-toast
- date-fns

## Implementation Checklist

### Critical (TODO Comments in Code)
- [ ] Implement password hashing (BCrypt) in UserService
- [ ] Add JWT authentication and authorization
- [ ] Integrate LLM API (OpenAI/similar) for classification
- [ ] Implement text extraction from documents
- [ ] Add file upload and storage logic
- [ ] Implement risk analysis calculations
- [ ] Add API key and configuration management

### Important
- [ ] Add comprehensive error handling
- [ ] Implement logging throughout services
- [ ] Add request validation annotations
- [ ] Create API documentation (Swagger/OpenAPI)
- [ ] Add caching for frequently accessed data
- [ ] Implement pagination for list endpoints
- [ ] Add search and filter functionality

### Nice to Have
- [ ] Add email notifications
- [ ] Implement audit logging
- [ ] Add dashboard analytics
- [ ] Create batch processing for multiple documents
- [ ] Add export functionality (PDF, Excel)
- [ ] Implement user activity tracking

## Running the Application

### Terminal 1 - Start Backend
```bash
cd /path/to/project
mvn spring-boot:run
```

### Terminal 2 - Start Frontend
```bash
cd /path/to/project/frontend
npm run dev
```

### Access Points
- Backend API: http://localhost:8080/api
- Frontend: http://localhost:3000

## Project Size

- Backend Classes: 4 Controllers + 4 Services + 4 Repositories + 4 Entities = 16 main classes
- Unit Tests: 4 service test classes
- Frontend Components: 3 page components + 1 navigation component
- Total Files: 50+ (including CSS, configs, etc.)
- Database: 4 tables with relationships

## Version History

- v1.0.0 - Initial skeleton project creation
- TODO: v1.1.0 - Authentication and LLM integration
- TODO: v2.0.0 - Advanced features and UI improvements
