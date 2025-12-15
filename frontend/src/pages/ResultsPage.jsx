import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import toast from 'react-hot-toast';
import { riskAnalysisAPI } from '../utils/api';
import '../styles/ResultsPage.css';

function ResultsPage() {
  const { documentId } = useParams();
  const navigate = useNavigate();
  const [documentData, setDocumentData] = useState(null);
  const [classification, setClassification] = useState(null);
  const [riskAnalysis, setRiskAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [reviewLoading, setReviewLoading] = useState(false);

  useEffect(() => {
    fetchResults();
  }, [documentId]);

  const formatTextList = (text) => {
    if (!text) return [];
    return text
      .split('\n')
      .map(item => {
        let cleaned = item.trim();
        if (!cleaned) return null;
        
        if (cleaned.includes('Identified risks for') || cleaned.includes('recommended mitigations for')) {
          return null;
        }
        
        cleaned = cleaned.replace(/^[-–—*+•]\s*/, '');
        cleaned = cleaned.replace(/^\d+\.\s*/, '');
        cleaned = cleaned.replace(/^\s+/, '');
        
        return cleaned.length > 0 ? cleaned : null;
      })
      .filter(item => item !== null);
  };

  const fetchResults = async () => {
    try {
      setLoading(true);

      const docRes = await axios.get(`http://localhost:8080/api/documents/${documentId}`).catch(() => ({ data: null }));
      
      if (!docRes.data) {
        toast.error('Could not load document');
        setLoading(false);
        return;
      }

      setDocumentData(docRes.data);

      let classification = null;
      let riskAnalysis = null;

      try {
        const classRes = await axios.get(`http://localhost:8080/api/classifications/${documentId}`);
        classification = classRes.data;
      } catch (err) {
        try {
          const classRes = await axios.post(`http://localhost:8080/api/classifications/classify/${documentId}`);
          classification = classRes.data;
        } catch (error) {
          console.warn('Could not create classification');
        }
      }

      try {
        const riskRes = await axios.get(`http://localhost:8080/api/risk-analysis/${documentId}`);
        riskAnalysis = riskRes.data;
      } catch (err) {
        try {
          const riskRes = await axios.post(`http://localhost:8080/api/risk-analysis/analyze/${documentId}`);
          riskAnalysis = riskRes.data;
        } catch (error) {
          console.warn('Could not create risk analysis');
        }
      }

      setClassification(classification);
      setRiskAnalysis(riskAnalysis);
    } catch (error) {
      console.error('Fetch error:', error);
      toast.error('Failed to load results: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleExportResults = () => {
    try {
      if (!documentData && !classification && !riskAnalysis) {
        toast.error('No analysis data to export');
        return;
      }

      let reportText = '═══════════════════════════════════════════════════════════════\n';
      reportText += '                    ANALYSIS REPORT\n';
      reportText += '═══════════════════════════════════════════════════════════════\n\n';
      
      reportText += `Generated: ${new Date().toLocaleString('pl-PL')}\n`;
      reportText += `Document ID: ${documentId}\n\n`;
      
      reportText += '───────────────────────────────────────────────────────────────\n';
      reportText += 'DOCUMENT INFORMATION\n';
      reportText += '───────────────────────────────────────────────────────────────\n';
      reportText += `File Name: ${documentData?.fileName || 'N/A'}\n`;
      reportText += `Upload Date: ${documentData?.uploadedAt || 'N/A'}\n`;
      reportText += `Status: ${documentData?.processingStatus || 'N/A'}\n\n`;
      
      if (classification) {
        reportText += '───────────────────────────────────────────────────────────────\n';
        reportText += 'DOCUMENT CLASSIFICATION\n';
        reportText += '───────────────────────────────────────────────────────────────\n';
        reportText += `Category: ${classification.category || 'N/A'}\n`;
        reportText += `Confidence Level: ${((classification.confidence || 0) * 100).toFixed(0)}%\n`;
        reportText += `Reason: ${classification.classificationReason || 'N/A'}\n\n`;
      }
      
      if (riskAnalysis) {
        reportText += '───────────────────────────────────────────────────────────────\n';
        reportText += 'RISK ANALYSIS\n';
        reportText += '───────────────────────────────────────────────────────────────\n';
        reportText += `Overall Risk Level: ${riskAnalysis.overallRiskLevel || 'N/A'}\n`;
        reportText += `Risk Score: ${((riskAnalysis.riskScore || 0) * 100).toFixed(0)}%\n`;
        reportText += `Analysis Framework: ${riskAnalysis.framework || 'N/A'}\n`;
        reportText += `Status: ${riskAnalysis.reviewed ? 'Reviewed' : 'Not Reviewed'}\n\n`;
        
        reportText += '📋 IDENTIFIED RISKS:\n';
        const risks = formatTextList(riskAnalysis.identifiedRisks);
        if (risks.length > 0) {
          risks.forEach(risk => {
            reportText += `  • ${risk}\n`;
          });
        } else {
          reportText += '  • No specific risks identified\n';
        }
        reportText += '\n';
        
        reportText += '✅ RECOMMENDED MITIGATIONS:\n';
        const mitigations = formatTextList(riskAnalysis.mitigationRecommendations);
        if (mitigations.length > 0) {
          mitigations.forEach(mitigation => {
            reportText += `  • ${mitigation}\n`;
          });
        }
        reportText += '\n';
      }
      
      reportText += '═══════════════════════════════════════════════════════════════\n';
      reportText += 'End of Report\n';
      reportText += '═══════════════════════════════════════════════════════════════\n';
      
      const dataBlob = new Blob([reportText], { type: 'text/plain; charset=utf-8' });
      const url = URL.createObjectURL(dataBlob);
      const link = window.document.createElement('a');
      link.href = url;
      link.download = `analysis_report_${documentId}_${new Date().getTime()}.txt`;
      window.document.body.appendChild(link);
      link.click();
      window.document.body.removeChild(link);
      URL.revokeObjectURL(url);
      toast.success('Report exported as TXT file');
    } catch (error) {
      console.error('Export error:', error);
      toast.error('Failed to export report: ' + error.message);
    }
  };

  const handleRequestReview = async () => {
    if (!riskAnalysis?.id) {
      toast.error('No risk analysis found to review');
      return;
    }
    
    if (riskAnalysis.reviewed) {
      toast.success('This analysis has already been reviewed.');
      return;
    }
    
    try {
      setReviewLoading(true);
      const userIdString = localStorage.getItem('userId') || '1';
      const userId = parseInt(userIdString, 10);
      
      if (isNaN(userId)) {
        throw new Error('Invalid user ID');
      }
      
      await riskAnalysisAPI.reviewAnalysis(riskAnalysis.id, userId, 'Review requested by user');
      toast.success('Review request submitted. Administrator will review your analysis.');
      await fetchResults();
    } catch (error) {
      console.error('Review error:', error);
      toast.error('Failed to submit review request: ' + (error.response?.data?.message || error.message));
    } finally {
      setReviewLoading(false);
    }
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
        <p className="document-name">{documentData?.fileName}</p>
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
              {formatTextList(riskAnalysis.identifiedRisks).length > 0 && (
                <>
                  <h3>Identified Risks:</h3>
                  <ul className="risk-list">
                    {formatTextList(riskAnalysis.identifiedRisks).map((risk, idx) => (
                      <li key={idx}>{risk}</li>
                    ))}
                  </ul>
                </>
              )}
              <h3>Recommended Mitigations:</h3>
              {formatTextList(riskAnalysis.mitigationRecommendations).length > 0 ? (
                <ul className="mitigation-list">
                  {formatTextList(riskAnalysis.mitigationRecommendations).map((mitigation, idx) => (
                    <li key={idx}>{mitigation}</li>
                  ))}
                </ul>
              ) : (
                <p>{riskAnalysis.mitigationRecommendations}</p>
              )}
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
        <button 
          className="action-btn secondary-btn" 
          onClick={handleRequestReview}
          disabled={reviewLoading || riskAnalysis?.reviewed}
        >
          {reviewLoading ? 'Submitting...' : riskAnalysis?.reviewed ? '✓ Already Reviewed' : '✅ Request Review'}
        </button>
        <button className="action-btn tertiary-btn" onClick={handleReturnToDashboard}>
          ← Back to Dashboard
        </button>
      </div>

      {documentData?.extractedText && (
        <div className="extracted-text-section">
          <h2>📄 Extracted Text</h2>
          <div className="text-preview">
            {documentData.extractedText.substring(0, 500)}...
          </div>
        </div>
      )}
    </div>
  );
}

export default ResultsPage;
