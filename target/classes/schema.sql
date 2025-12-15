-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Documents Table
CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    extracted_text TEXT,
    user_id BIGINT NOT NULL,
    processing_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Classifications Table
CREATE TABLE IF NOT EXISTS classifications (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    category VARCHAR(100) NOT NULL,
    confidence FLOAT NOT NULL,
    classification_reason TEXT,
    raw_classification_result TEXT,
    version INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);

-- Risk Analyses Table
CREATE TABLE IF NOT EXISTS risk_analyses (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    overall_risk_level VARCHAR(50) NOT NULL,
    risk_score FLOAT NOT NULL,
    identified_risks TEXT,
    mitigation_recommendations TEXT,
    raw_analysis_result TEXT,
    framework VARCHAR(50) NOT NULL,
    reviewed BOOLEAN NOT NULL DEFAULT false,
    review_notes VARCHAR(500),
    reviewed_by_user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create Indexes for Performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);

CREATE INDEX idx_documents_user_id ON documents(user_id);
CREATE INDEX idx_documents_processing_status ON documents(processing_status);
CREATE INDEX idx_documents_document_type ON documents(document_type);

CREATE INDEX idx_classifications_document_id ON classifications(document_id);
CREATE INDEX idx_classifications_category ON classifications(category);
CREATE INDEX idx_classifications_confidence ON classifications(confidence);

CREATE INDEX idx_risk_analyses_document_id ON risk_analyses(document_id);
CREATE INDEX idx_risk_analyses_overall_risk_level ON risk_analyses(overall_risk_level);
CREATE INDEX idx_risk_analyses_risk_score ON risk_analyses(risk_score);
CREATE INDEX idx_risk_analyses_reviewed ON risk_analyses(reviewed);
CREATE INDEX idx_risk_analyses_framework ON risk_analyses(framework);
CREATE INDEX idx_risk_analyses_reviewed_by ON risk_analyses(reviewed_by_user_id);

-- Insert Sample Users for Testing
INSERT INTO users (email, first_name, last_name, password_hash, role, active)
VALUES 
  ('admin@example.com', 'Admin', 'User', 'hashed_password', 'ADMIN', true),
  ('analyst@example.com', 'Analyst', 'User', 'hashed_password', 'ANALYST', true),
  ('demo@example.com', 'Demo', 'User', 'hashed_password', 'ANALYST', true)
ON CONFLICT (email) DO NOTHING;
