import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import toast from 'react-hot-toast';
import '../styles/DashboardPage.css';

function DashboardPage({ userRole }) {
  const [documents, setDocuments] = useState([]);
  const [analyses, setAnalyses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalDocuments: 0,
    completedAnalyses: 0,
    highRiskDocuments: 0,
    pendingReviews: 0
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const userId = parseInt(localStorage.getItem('userId') || '1', 10);

      const [docsRes, pendingRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/documents/user/${userId}`),
        axios.get(`http://localhost:8080/api/risk-analysis/pending-user/${userId}`)
      ]);

      setDocuments(docsRes.data);
      setAnalyses(pendingRes.data);

      setStats({
        totalDocuments: docsRes.data.length,
        completedAnalyses: docsRes.data.filter(d => d.processingStatus === 'COMPLETED').length,
        highRiskDocuments: docsRes.data.filter(d => {
          const riskAnalysis = getRiskAnalysisForDocument(d.id, docsRes.data, pendingRes.data);
          return riskAnalysis && (riskAnalysis.overallRiskLevel === 'HIGH' || riskAnalysis.overallRiskLevel === 'CRITICAL');
        }).length,
        pendingReviews: pendingRes.data.length
      });
    } catch (error) {
      toast.error('Failed to load dashboard: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const getRiskAnalysisForDocument = (docId, allDocs, pendingAnalyses) => {
    return pendingAnalyses.find(a => a.document?.id === docId) || null;
  };

  const getReviewStatus = (docId) => {
    const pendingAnalysis = analyses.find(a => a.document?.id === docId);
    if (!pendingAnalysis) {
      return { status: 'Not Analyzed', color: '#9ca3af' };
    }
    if (pendingAnalysis.reviewed) {
      return { status: 'Reviewed', color: '#16a34a' };
    }
    return { status: 'Pending Review', color: '#ea580c' };
  };

  const getStatusColor = (status) => {
    const colors = {
      COMPLETED: '#16a34a',
      PROCESSING: '#eab308',
      PENDING: '#0ea5e9',
      FAILED: '#dc2626'
    };
    return colors[status] || '#666';
  };

  const deleteDocument = async (id) => {
    if (window.confirm('Are you sure you want to delete this document?')) {
      try {
        await axios.delete(`http://localhost:8080/api/documents/${id}`);
        toast.success('Document deleted successfully');
        fetchDashboardData();
      } catch (error) {
        toast.error('Failed to delete document: ' + error.message);
      }
    }
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Welcome to Document Analyzer</h1>
        <p>Manage and analyze your documents for risk assessment</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <h3>📄 Total Documents</h3>
          <p className="stat-value">{stats.totalDocuments}</p>
        </div>
        <div className="stat-card">
          <h3>✅ Completed Analyses</h3>
          <p className="stat-value">{stats.completedAnalyses}</p>
        </div>
        <div className="stat-card">
          <h3>⚠️ High Risk Documents</h3>
          <p className="stat-value high-risk">{stats.highRiskDocuments}</p>
        </div>
        <div className="stat-card">
          <h3>📋 Pending Reviews</h3>
          <p className="stat-value">{stats.pendingReviews}</p>
        </div>
      </div>

      <div className="dashboard-actions">
        <Link to="/upload" className="action-button primary">
          + Upload New Document
        </Link>
      </div>

      <div className="dashboard-tables">
        <div className="table-section">
          <h2>Recent Documents</h2>
          <div className="table-container">
            <table className="documents-table">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Review Status</th>
                  <th>Uploaded</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {documents.slice(0, 10).map((doc) => {
                  const reviewStatus = getReviewStatus(doc.id);
                  return (
                    <tr key={doc.id}>
                      <td>{doc.fileName}</td>
                      <td>{doc.documentType}</td>
                      <td>
                        <span
                          className="status-badge"
                          style={{ backgroundColor: getStatusColor(doc.processingStatus) }}
                        >
                          {doc.processingStatus}
                        </span>
                      </td>
                      <td>
                        <span
                          className="status-badge"
                          style={{ backgroundColor: reviewStatus.color }}
                        >
                          {reviewStatus.status}
                        </span>
                      </td>
                      <td>{new Date(doc.createdAt).toLocaleDateString()}</td>
                      <td>
                        <Link to={`/results/${doc.id}`} className="view-link">
                          View
                        </Link>
                        <button 
                          onClick={() => deleteDocument(doc.id)} 
                          className="delete-link"
                          style={{ marginLeft: '10px', color: '#dc2626', cursor: 'pointer', border: 'none', background: 'none', textDecoration: 'underline' }}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        {analyses.length > 0 && (
          <div className="table-section">
            <h2>My Pending Reviews</h2>
            <div className="table-container">
              <table className="reviews-table">
                <thead>
                  <tr>
                    <th>Document</th>
                    <th>Risk Level</th>
                    <th>Score</th>
                    <th>Framework</th>
                    {userRole === 'ADMIN' && <th>Action</th>}
                  </tr>
                </thead>
                <tbody>
                  {analyses.slice(0, 10).map((analysis) => (
                    <tr key={analysis.id}>
                      <td>{analysis.document?.fileName}</td>
                      <td>
                        <span className={`risk-badge ${analysis.overallRiskLevel.toLowerCase()}`}>
                          {analysis.overallRiskLevel}
                        </span>
                      </td>
                      <td>{(analysis.riskScore * 100).toFixed(0)}%</td>
                      <td>{analysis.framework}</td>
                      {userRole === 'ADMIN' && (
                        <td>
                          <button onClick={() => TODO_APPROVE_REVIEW(analysis.id)} className="approve-btn">
                            Approve
                          </button>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function TODO_APPROVE_REVIEW(analysisId) {
}

export default DashboardPage;
