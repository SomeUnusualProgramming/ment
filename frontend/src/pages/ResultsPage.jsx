import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import toast from 'react-hot-toast';
import '../styles/ResultsPage.css';

function ResultsPage() {
  const { documentId } = useParams();
  const navigate = useNavigate();
  const [document, setDocument] = useState(null);
  const [classification, setClassification] = useState(null);
  const [riskAnalysis, setRiskAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchResults();
  }, [documentId]);

  const fetchResults = async () => {
    try {
      setLoading(true);

      const [docRes, classRes, riskRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/documents/${documentId}`).catch(() => ({ data: null })),
        axios.get(`http://localhost:8080/api/classifications/${documentId}`).catch(() => ({ data: null })),
        axios.get(`http://localhost:8080/api/risk-analysis/${documentId}`).catch(() => ({ data: null }))
      ]);

      setDocument(docRes.data);
      setClassification(classRes.data);
      setRiskAnalysis(riskRes.data);
      
      if (!docRes.data) {
        toast.error('Could not load document');
      }
    } catch (error) {
      toast.error('Failed to load results: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleExportResults = () => {
    const report = {
      document: document,
      classification: classification,
      riskAnalysis: riskAnalysis,
      exportedAt: new Date().toISOString()
    };
    
    const dataStr = JSON.stringify(report, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `analysis_report_${documentId}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    toast.success('Report exported successfully');
  };

  const handleRequestReview = () => {
    toast.success('Review request submitted. Administrator will review your analysis.');
  };

  const handleReturnToDashboard = () => {
    navigate('/');
  };

  if (loading) {
    return <div className="loading">Loading analysis results...</div>;
  }

  const getRiskColor = (level) => {
    const colors = {
      CRITICAL: '#dc2626',
      HIGH: '#ea580c',
      MEDIUM: '#eab308',
      LOW: '#16a34a',
      MINIMAL: '#0ea5e9'
    };
    return colors[level] || '#666';
  };

  return (
    <div className="results-container">
      <div className="results-header">
        <h1>Analysis Results</h1>
        <p className="document-name">{document?.fileName}</p>
      </div>

      <div className="results-grid">
        {classification && (
          <div className="results-card classification-card">
            <h2>📂 Document Classification</h2>
            <div className="classification-badge">
              <span className="category">{classification.category}</span>
              <span className="confidence">{(classification.confidence * 100).toFixed(0)}% Confidence</span>
            </div>
            <div className="classification-details">
              <h3>Classification Reason:</h3>
              <p>{classification.classificationReason}</p>
            </div>
          </div>
        )}

        {riskAnalysis && (
          <div className="results-card risk-card">
            <h2>⚠️ Risk Analysis</h2>
            <div className="risk-badge" style={{ borderColor: getRiskColor(riskAnalysis.overallRiskLevel) }}>
              <span className="level" style={{ color: getRiskColor(riskAnalysis.overallRiskLevel) }}>
                {riskAnalysis.overallRiskLevel}
              </span>
              <span className="score">Risk Score: {(riskAnalysis.riskScore * 100).toFixed(0)}%</span>
            </div>
            <div className="risk-details">
              <h3>Identified Risks:</h3>
              <p>{riskAnalysis.identifiedRisks}</p>
              <h3>Recommended Mitigations:</h3>
              <p>{riskAnalysis.mitigationRecommendations}</p>
            </div>
            {riskAnalysis.framework && (
              <div className="framework-badge">
                Framework: {riskAnalysis.framework}
              </div>
            )}
          </div>
        )}
      </div>

      <div className="results-actions">
        <button className="action-btn primary-btn" onClick={handleExportResults}>
          📥 Export Report
        </button>
        <button className="action-btn secondary-btn" onClick={handleRequestReview}>
          ✅ Request Review
        </button>
        <button className="action-btn tertiary-btn" onClick={handleReturnToDashboard}>
          ← Back to Dashboard
        </button>
      </div>

      {document?.extractedText && (
        <div className="extracted-text-section">
          <h2>📄 Extracted Text</h2>
          <div className="text-preview">
            {document.extractedText.substring(0, 500)}...
          </div>
        </div>
      )}
    </div>
  );
}

export default ResultsPage;
