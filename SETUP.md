# Document Risk Analyzer - Project Setup Guide

## Project Structure

```
ment/
├── src/
│   ├── main/
│   │   ├── java/com/document/analyzer/
│   │   │   ├── controller/          # REST API Controllers
│   │   │   ├── service/             # Business Logic Services
│   │   │   ├── repository/          # JPA Repositories
│   │   │   ├── entity/              # JPA Entities
│   │   │   └── DocumentAnalyzerApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql           # Database Schema
│   └── test/
│       └── java/com/document/analyzer/service/  # Unit Tests
├── frontend/                        # React Application
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── styles/
│   │   ├── utils/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
├── pom.xml                         # Maven Configuration
└── SETUP.md                        # This file

```

## Backend Setup (Java/Spring Boot)

### Prerequisites
- Java 17 or 21
- Maven 3.8+
- PostgreSQL 13+

### Step 1: Database Setup

1. Create a PostgreSQL database:
```bash
createdb document_analyzer
```

2. Execute the schema file to create tables:
```bash
psql -U postgres -d document_analyzer -f src/main/resources/schema.sql
```

### Step 2: Update Configuration

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/document_analyzer
spring.datasource.username=your_postgres_user
spring.datasource.password=your_postgres_password
app.llm.api-key=your_openai_api_key
```

### Step 3: Build and Run Backend

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# The backend will start on http://localhost:8080
```

### Step 4: Run Tests

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -Dtest=UserServiceTest
```

## Frontend Setup (React/Vite)

### Prerequisites
- Node.js 16+
- npm or yarn

### Step 1: Install Dependencies

```bash
cd frontend
npm install
```

### Step 2: Start Development Server

```bash
npm run dev

# The frontend will start on http://localhost:3000
```

### Step 3: Build for Production

```bash
npm run build
npm run preview
```

## API Endpoints

### User Management
- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/authenticate` - Authenticate user

### Document Upload
- `POST /api/documents/upload` - Upload a document
- `GET /api/documents/{id}` - Get document details
- `GET /api/documents/user/{userId}` - Get user's documents
- `PUT /api/documents/{id}/status` - Update processing status
- `DELETE /api/documents/{id}` - Delete document
- `POST /api/documents/{id}/extract-text` - Extract text from document

### Classification
- `POST /api/classifications/classify/{documentId}` - Classify document
- `GET /api/classifications/{documentId}` - Get classification
- `GET /api/classifications/category/{category}` - Filter by category
- `GET /api/classifications/high-confidence/{minConfidence}` - Get high confidence classifications
- `PUT /api/classifications/{id}` - Update classification
- `GET /api/classifications/{id}/validate` - Validate classification accuracy

### Risk Analysis
- `POST /api/risk-analysis/analyze/{documentId}` - Analyze document risk
- `GET /api/risk-analysis/{documentId}` - Get risk analysis
- `GET /api/risk-analysis/level/{riskLevel}` - Filter by risk level
- `GET /api/risk-analysis/high-risk/{minScore}` - Get high risk documents
- `GET /api/risk-analysis/unreviewed` - Get unreviewed analyses
- `POST /api/risk-analysis/{id}/review` - Review and approve analysis
- `PUT /api/risk-analysis/{id}/risk-level` - Update risk level

## TODO: Implementation Areas

### Backend - High Priority
1. **Authentication & Authorization**
   - Implement JWT token generation and validation
   - Add Spring Security configuration
   - Add password hashing (BCrypt)

2. **LLM Integration** (`ClassificationService`, `RiskAnalysisService`)
   - Integrate with OpenAI API or other LLM provider
   - Implement prompt engineering for document classification
   - Parse and extract risk data from LLM responses

3. **Document Processing**
   - Implement text extraction (PDF, DOCX, Images with OCR)
   - Add file upload and storage logic
   - Implement async processing for large documents

4. **Configuration**
   - Add properties for LLM API keys and endpoints
   - Configure file upload directory and limits
   - Set up environment-specific configurations

### Frontend - High Priority
1. **Authentication**
   - Implement login form with backend integration
   - Store JWT tokens in localStorage
   - Add authentication interceptor to axios

2. **API Integration**
   - Complete all TODO placeholders in `api.js`
   - Add error handling and toast notifications
   - Implement proper loading states

3. **State Management**
   - Consider adding Context API or Redux for state management
   - Manage user session state globally
   - Cache API responses appropriately

4. **UI/UX Enhancements**
   - Add pagination to document and results tables
   - Implement search and filter functionality
   - Add export functionality for reports

## Database Schema Overview

### Users Table
- Stores user accounts with roles (ADMIN, ANALYST, VIEWER)
- Contains authentication data (password hash)
- Tracks user activity timestamps

### Documents Table
- Stores uploaded documents with metadata
- Links documents to users
- Tracks processing status

### Classifications Table
- Stores document classification results
- Contains confidence scores and reasoning
- Supports versioning for corrections

### Risk Analyses Table
- Stores risk assessment results
- Contains identified risks and recommendations
- Tracks review status and reviewer information

## Security Considerations

1. **API Security**
   - Add CORS configuration for frontend URL
   - Implement rate limiting
   - Add request validation and sanitization

2. **Database Security**
   - Use parameterized queries (JPA handles this)
   - Implement row-level security if needed
   - Enable database encryption at rest

3. **File Upload Security**
   - Validate file types and sizes
   - Scan uploaded files for malware
   - Store files outside webroot

## Deployment Guide

### Backend Deployment
```bash
# Create production build
mvn clean package -DskipTests

# Docker deployment
docker build -t document-analyzer .
docker run -p 8080:8080 document-analyzer
```

### Frontend Deployment
```bash
# Build for production
npm run build

# Deploy dist folder to web server or CDN
```

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in application.properties
- Ensure database exists and is accessible

### Build Failures
- Clear Maven cache: `mvn clean`
- Check Java version: `java -version`
- Verify all dependencies are available

### Frontend Issues
- Clear node_modules: `rm -rf node_modules && npm install`
- Check port 3000 is available
- Verify backend is running and accessible

## Support & Documentation

For more information, refer to:
- Spring Boot: https://spring.io/projects/spring-boot
- React: https://react.dev
- PostgreSQL: https://www.postgresql.org/docs
- Vite: https://vitejs.dev

## Next Steps

1. Set up the database with schema.sql
2. Configure LLM API keys in application.properties
3. Implement authentication with JWT
4. Integrate document processing libraries
5. Deploy to your hosting platform

Happy coding! 🚀
