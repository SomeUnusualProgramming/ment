import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import toast from 'react-hot-toast';
import '../styles/UploadPage.css';

function UploadPage() {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const navigate = useNavigate();

  const onDrop = useCallback(acceptedFiles => {
    setFiles(acceptedFiles);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  const handleUpload = async () => {
    if (files.length === 0) {
      toast.error('Please select a file');
      return;
    }

    setUploading(true);
    const formData = new FormData();
    formData.append('file', files[0]);
    formData.append('userId', localStorage.getItem('userId') || '1');

    try {
      const response = await axios.post(
        'http://localhost:8080/api/documents/upload',
        formData,
        {
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            setUploadProgress(percentCompleted);
          }
        }
      );

      toast.success('Document uploaded successfully!');
      setFiles([]);
      setUploadProgress(0);

      navigate(`/results/${response.data.id}`);
    } catch (error) {
      toast.error('Upload failed: ' + error.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="upload-container">
      <div className="upload-header">
        <h1>Upload Document</h1>
        <p>Upload a document for analysis and risk classification</p>
      </div>

      <div {...getRootProps()} className={`dropzone ${isDragActive ? 'active' : ''}`}>
        <input {...getInputProps()} />
        {isDragActive ? (
          <p>Drop the file here...</p>
        ) : (
          <div>
            <p>📁 Drag and drop your document here</p>
            <p>or click to select a file</p>
            <small>Supported formats: PDF, DOCX, TXT, JSON, CSV, IMAGE</small>
          </div>
        )}
      </div>

      {files.length > 0 && (
        <div className="file-list">
          <h3>Selected Files:</h3>
          {files.map((file) => (
            <div key={file.name} className="file-item">
              <span>{file.name}</span>
              <small>{(file.size / 1024).toFixed(2)} KB</small>
            </div>
          ))}
        </div>
      )}

      {uploading && (
        <div className="progress-container">
          <div className="progress-bar">
            <div className="progress-fill" style={{ width: `${uploadProgress}%` }}></div>
          </div>
          <p>{uploadProgress}% uploaded</p>
        </div>
      )}

      <div className="upload-actions">
        <button
          onClick={handleUpload}
          disabled={uploading || files.length === 0}
          className="upload-btn"
        >
          {uploading ? 'Uploading...' : 'Upload Document'}
        </button>
      </div>

      <div className="upload-info">
        <h3>What happens next:</h3>
        <ul>
          <li>📝 Document text extraction and analysis</li>
          <li>🏷️ Automatic document classification</li>
          <li>⚠️ Risk assessment and analysis</li>
          <li>📊 Detailed results and recommendations</li>
        </ul>
      </div>
    </div>
  );
}

export default UploadPage;
