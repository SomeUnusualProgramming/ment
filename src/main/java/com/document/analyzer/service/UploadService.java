package com.document.analyzer.service;

import com.document.analyzer.entity.Document;
import com.document.analyzer.entity.RiskAnalysis;
import com.document.analyzer.entity.User;
import com.document.analyzer.repository.DocumentRepository;
import com.document.analyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final ClassificationService classificationService;
    private final RiskAnalysisService riskAnalysisService;

    @Value("${app.document.upload-dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String[] ALLOWED_EXTENSIONS = {"pdf", "docx", "doc", "txt", "json", "csv", "png", "jpg", "jpeg", "gif"};

    public Document uploadDocument(MultipartFile file, Long userId) throws IOException {
        validateFileTypeAndSize(file);
        String savedFilePath = storeFileToStorage(file);
        String extractedText = extractTextFromDocument(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Document document = Document.builder()
                .fileName(file.getOriginalFilename())
                .filePath(savedFilePath)
                .fileSize(file.getSize())
                .documentType(inferDocumentType(file.getOriginalFilename()))
                .extractedText(extractedText)
                .uploadedBy(user)
                .processingStatus(Document.ProcessingStatus.PROCESSING)
                .build();

        Document savedDocument = documentRepository.save(document);

        try {
            classificationService.classifyDocument(savedDocument.getId());
            riskAnalysisService.analyzeDocumentRisk(savedDocument.getId(), RiskAnalysis.AnalysisFramework.OWASP);
            savedDocument.setProcessingStatus(Document.ProcessingStatus.COMPLETED);
        } catch (Exception e) {
            savedDocument.setProcessingStatus(Document.ProcessingStatus.FAILED);
        }

        return documentRepository.save(savedDocument);
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public List<Document> getUserDocuments(Long userId) {
        return documentRepository.findByUploadedById(userId);
    }

    public List<Document> getDocumentsByStatus(Document.ProcessingStatus status) {
        return documentRepository.findByProcessingStatus(status);
    }

    public Document updateProcessingStatus(Long documentId, Document.ProcessingStatus status) {
        return documentRepository.findById(documentId)
                .map(doc -> {
                    doc.setProcessingStatus(status);
                    return documentRepository.save(doc);
                })
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public void deleteDocument(Long id) throws IOException {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        deleteFileFromStorage(doc.getFilePath());
        documentRepository.deleteById(id);
    }

    public Document extractAndStoreText(Long documentId) {
        return documentRepository.findById(documentId)
                .map(doc -> {
                    try {
                        String text = extractTextFromDocument(new File(doc.getFilePath()));
                        doc.setExtractedText(text);
                        doc.setProcessingStatus(Document.ProcessingStatus.COMPLETED);
                    } catch (IOException e) {
                        doc.setProcessingStatus(Document.ProcessingStatus.FAILED);
                    }
                    return documentRepository.save(doc);
                })
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    private Document.DocumentType inferDocumentType(String fileName) {
        if (fileName == null) return Document.DocumentType.TXT;
        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".pdf")) return Document.DocumentType.PDF;
        if (lowerName.endsWith(".docx") || lowerName.endsWith(".doc")) return Document.DocumentType.DOCX;
        if (lowerName.endsWith(".txt")) return Document.DocumentType.TXT;
        if (lowerName.endsWith(".json")) return Document.DocumentType.JSON;
        if (lowerName.endsWith(".csv")) return Document.DocumentType.CSV;
        if (lowerName.matches(".*\\.(png|jpg|jpeg|gif)$")) return Document.DocumentType.IMAGE;

        return Document.DocumentType.TXT;
    }

    private void validateFileTypeAndSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of 10MB");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Invalid file name");
        }
        
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        boolean isAllowed = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equals(fileExtension)) {
                isAllowed = true;
                break;
            }
        }
        
        if (!isAllowed) {
            throw new RuntimeException("File type not allowed. Supported types: PDF, DOCX, TXT, JSON, CSV, IMAGE");
        }
    }

    private String storeFileToStorage(MultipartFile file) throws IOException {
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        
        file.transferTo(filePath.toFile());
        return filePath.toString();
    }

    private String extractTextFromDocument(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) return "";
        
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } else if (lowerName.endsWith(".json") || lowerName.endsWith(".csv")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } else if (lowerName.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        }
        return "Document content extraction not yet implemented for this file type.";
    }

    private String extractTextFromDocument(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".txt")) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } else if (fileName.endsWith(".json") || fileName.endsWith(".csv")) {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } else if (fileName.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        }
        return "Document content extraction not yet implemented for this file type.";
    }

    private void deleteFileFromStorage(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
}
