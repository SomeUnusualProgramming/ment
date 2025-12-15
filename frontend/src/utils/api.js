import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const userAPI = {
  login: (email, password) => {
    TODO_ADD_AUTHENTICATION_ENDPOINT();
    return api.post('/users/authenticate', { email, password });
  },
  getCurrentUser: () => api.get('/users/profile'),
  updateProfile: (userId, data) => api.put(`/users/${userId}`, data),
  getAllUsers: () => api.get('/users'),
  getUsersByRole: (role) => api.get(`/users/role/${role}`)
};

export const documentAPI = {
  uploadDocument: (file, userId) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);
    TODO_ADD_FILE_UPLOAD_ENDPOINT();
    return api.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },
  getDocument: (id) => api.get(`/documents/${id}`),
  getUserDocuments: (userId) => api.get(`/documents/user/${userId}`),
  getDocumentsByStatus: (status) => api.get(`/documents/status/${status}`),
  updateDocumentStatus: (id, status) => api.put(`/documents/${id}/status`, { status }),
  deleteDocument: (id) => api.delete(`/documents/${id}`),
  extractText: (id) => api.post(`/documents/${id}/extract-text`)
};

export const classificationAPI = {
  classifyDocument: (documentId) => {
    TODO_ADD_CLASSIFICATION_ENDPOINT();
    return api.post(`/classifications/classify/${documentId}`);
  },
  getClassification: (documentId) => api.get(`/classifications/${documentId}`),
  getByCategory: (category) => api.get(`/classifications/category/${category}`),
  getHighConfidence: (minConfidence) => api.get(`/classifications/high-confidence/${minConfidence}`),
  updateClassification: (id, category) => api.put(`/classifications/${id}`, { category }),
  validateClassification: (id) => api.get(`/classifications/${id}/validate`)
};

export const riskAnalysisAPI = {
  analyzeDocument: (documentId, framework = 'OWASP') => {
    TODO_ADD_RISK_ANALYSIS_ENDPOINT();
    return api.post(`/risk-analysis/analyze/${documentId}`, { framework });
  },
  getAnalysis: (documentId) => api.get(`/risk-analysis/${documentId}`),
  getByRiskLevel: (level) => api.get(`/risk-analysis/level/${level}`),
  getHighRisk: (minScore) => api.get(`/risk-analysis/high-risk/${minScore}`),
  getUnreviewed: () => api.get('/risk-analysis/unreviewed'),
  reviewAnalysis: (id, reviewerUserId, notes) => {
    TODO_ADD_REVIEW_ENDPOINT();
    return api.post(`/risk-analysis/${id}/review`, {
      reviewerUserId,
      reviewNotes: notes
    });
  },
  updateRiskLevel: (id, newLevel) => api.put(`/risk-analysis/${id}/risk-level`, { newLevel }),
  getByFramework: (framework) => api.get(`/risk-analysis/framework/${framework}`)
};

export default api;

function TODO_ADD_AUTHENTICATION_ENDPOINT() {
}

function TODO_ADD_FILE_UPLOAD_ENDPOINT() {
}

function TODO_ADD_CLASSIFICATION_ENDPOINT() {
}

function TODO_ADD_RISK_ANALYSIS_ENDPOINT() {
}

function TODO_ADD_REVIEW_ENDPOINT() {
}
