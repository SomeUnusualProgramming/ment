# Document Risk Analyzer

A full-stack web application for analyzing documents, classifying their content, and assessing risk levels using LLM integration.

## 📋 Features

- **Document Upload** - Upload and process documents in multiple formats (PDF, DOCX, TXT, JSON, CSV, IMAGE)
- **Content Classification** - Automatic document classification using LLM (Contract, Invoice, Report, Policy, etc.)
- **Risk Analysis** - Comprehensive risk assessment with configurable frameworks (OWASP, NIST, ISO27001, GDPR)
- **User Management** - Role-based access control (Admin, Analyst, Viewer)
- **Responsive UI** - Modern React-based frontend with Tailwind CSS
- **RESTful API** - Complete REST API for all document operations
- **Database Persistence** - PostgreSQL for reliable data storage

## 🛠 Tech Stack

### Backend
- **Java 21** with Spring Boot 3.2.0
- **PostgreSQL 15** for data storage
- **Spring Data JPA** for database operations
- **Apache PDFBox** for PDF processing
- **JUnit 5 + Mockito** for testing

### Frontend
- **React 18.2** with Vite
- **Tailwind CSS 3.2** for styling
- **React Router 6.8** for navigation
- **Axios** for API communication
- **React Dropzone** for file uploads
- **React Hot Toast** for notifications

### DevOps
- **Docker & Docker Compose** for containerization

## 📦 Prerequisites

- **Java 21** or higher
- **Node.js 16+** and npm
- **Docker & Docker Compose** (optional, for containerized setup)
- **PostgreSQL 15** (if running without Docker)

## ⚙️ Installation

### Option 1: Local Setup

#### Backend Setup
```bash
# Clone the repository
git clone <repository-url>
cd ment

# Configure database connection
# Edit src/main/resources/application.properties
# Set your database credentials and LLM API key

# Build and run backend
mvn clean install
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Configure API endpoint
# The frontend connects to http://localhost:8080/api by default

# Start development server
npm run dev
```

### Option 2: Docker Compose Setup

```bash
# Start all services (PostgreSQL, Backend, Frontend)
docker-compose up -d

# Access the application
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080/api
# PostgreSQL: localhost:5432
```

## 🚀 Running the Application

### Development Mode

**Terminal 1 - Backend:**
```bash
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```
Frontend runs on `http://localhost:3000`

### Production Build

**Backend:**
```bash
mvn clean package
java -jar target/document-analyzer-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
npm run build
npm run preview
```

## 📁 Project Structure

```
ment/
├── src/
│   ├── main/java/com/document/analyzer/
│   │   ├── controller/        # REST API endpoints
│   │   ├── service/           # Business logic
│   │   ├── repository/        # Data access layer
│   │   ├── entity/            # JPA entities
│   │   ├── domain/            # DTOs and enums
│   │   └── util/              # Utility classes
│   ├── main/resources/
│   │   ├── application.properties
│   │   └── schema.sql         # Database initialization
│   └── test/                  # Unit tests
├── frontend/
│   ├── src/
│   │   ├── components/        # React components
│   │   ├── pages/             # Page components
│   │   ├── styles/            # CSS styles
│   │   ├── utils/             # Utility functions
│   │   └── App.jsx            # Main app component
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml
├── Dockerfile.backend
├── Dockerfile.frontend
├── pom.xml                    # Maven configuration
└── README.md
```

## 🔌 API Endpoints

### User Management
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Document Operations
- `POST /api/upload` - Upload document
- `GET /api/documents` - List documents
- `GET /api/documents/{id}` - Get document details
- `DELETE /api/documents/{id}` - Delete document

### Classification
- `POST /api/classify` - Classify document
- `GET /api/classifications/{documentId}` - Get classification results

### Risk Analysis
- `POST /api/analyze` - Perform risk analysis
- `GET /api/risk-analysis/{documentId}` - Get risk analysis results
- `PUT /api/risk-analysis/{id}/review` - Review and update risk analysis

## 🗄️ Database Schema

### Users Table
- id, email, first_name, last_name, password_hash, role, active, created_at, updated_at

### Documents Table
- id, file_name, file_path, file_size, document_type, extracted_text, user_id, processing_status, created_at, updated_at

### Classifications Table
- id, document_id, category, confidence, classification_reason, raw_classification_result, version, created_at, updated_at

### Risk Analyses Table
- id, document_id, overall_risk_level, risk_score, identified_risks, mitigation_recommendations, framework, reviewed, review_notes, reviewed_by_user_id, created_at, updated_at

## 🧪 Running Tests

```bash
# Backend tests
mvn test

# Frontend tests (if configured)
cd frontend
npm test
```

## 📝 Environment Variables

### Backend (src/main/resources/application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/document_analyzer
spring.datasource.username=postgres
spring.datasource.password=postgres
app.llm.api-key=your-openai-api-key
app.llm.endpoint=https://api.openai.com/v1/chat/completions
app.document.upload-dir=/path/to/uploads
```

### Frontend (.env or environment setup)
```
VITE_API_URL=http://localhost:8080/api
```

## 🔐 Security Considerations

- **TODO**: Implement JWT authentication
- **TODO**: Add password hashing (BCrypt)
- **TODO**: Implement HTTPS in production
- **TODO**: Add API rate limiting
- **TODO**: Validate and sanitize all inputs
- **TODO**: Use environment variables for secrets

## 📚 Additional Documentation

- See `PROJECT_STRUCTURE.md` for detailed project organization
- See `SETUP.md` for detailed setup instructions
- See `repo.md` for repository information

## 🤝 Contributing

1. Create a feature branch (`git checkout -b feature/amazing-feature`)
2. Commit your changes (`git commit -m 'Add amazing feature'`)
3. Push to the branch (`git push origin feature/amazing-feature`)
4. Open a Pull Request

## 📄 License

This project is part of an internal system. Contact the development team for licensing information.

## 🆘 Support

For issues, questions, or feature requests, please contact the development team or create an issue in the repository.

## 🎯 Roadmap

- [ ] JWT authentication and authorization
- [ ] Email notifications
- [ ] Document batch processing
- [ ] Export functionality (PDF, Excel)
- [ ] Advanced analytics dashboard
- [ ] Audit logging
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Performance optimization and caching
