package com.document.analyzer.service;

import com.document.analyzer.entity.Document;
import com.document.analyzer.entity.RiskAnalysis;
import com.document.analyzer.entity.User;
import com.document.analyzer.repository.DocumentRepository;
import com.document.analyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassificationService classificationService;

    @Mock
    private RiskAnalysisService riskAnalysisService;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private UploadService uploadService;

    private User testUser;
    private Document testDocument;

    @BeforeEach
    void setUp() throws IOException {
        testUser = User.builder()
                .id(1L)
                .email("uploader@example.com")
                .firstName("Upload")
                .lastName("User")
                .role(User.UserRole.ANALYST)
                .active(true)
                .build();

        testDocument = Document.builder()
                .id(1L)
                .fileName("test.pdf")
                .filePath("/uploads/test.pdf")
                .fileSize(1024L)
                .documentType(Document.DocumentType.PDF)
                .uploadedBy(testUser)
                .processingStatus(Document.ProcessingStatus.PENDING)
                .build();

        Path tempDir = Files.createTempDirectory("upload-test");
        ReflectionTestUtils.setField(uploadService, "uploadDir", tempDir.toString());
    }

    @Test
    void testUploadDocument() throws IOException {
        byte[] textContent = "Test document content".getBytes();
        
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(textContent);
        doNothing().when(mockFile).transferTo(any(java.io.File.class));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            if (doc.getId() == null) {
                doc.setId(1L);
            }
            return doc;
        });

        Document document = uploadService.uploadDocument(mockFile, 1L);

        assertNotNull(document);
        assertEquals("test.txt", document.getFileName());
        assertEquals(Document.DocumentType.TXT, document.getDocumentType());
        verify(userRepository, times(1)).findById(1L);
        verify(documentRepository, times(2)).save(any(Document.class));
    }

    @Test
    void testGetDocumentById() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        Optional<Document> document = uploadService.getDocumentById(1L);

        assertTrue(document.isPresent());
        assertEquals("test.pdf", document.get().getFileName());
    }

    @Test
    void testGetUserDocuments() {
        uploadService.getUserDocuments(1L);

        verify(documentRepository, times(1)).findByUploadedById(1L);
    }

    @Test
    void testUpdateProcessingStatus() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        Document updated = uploadService.updateProcessingStatus(1L, Document.ProcessingStatus.PROCESSING);

        assertNotNull(updated);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void testDeleteDocument() throws IOException {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        uploadService.deleteDocument(1L);

        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testExtractAndStoreText() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, "Test document content".getBytes());
        testDocument.setFilePath(tempFile.toString());
        
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(1L);
            return doc;
        });

        Document document = uploadService.extractAndStoreText(1L);

        assertNotNull(document);
        assertEquals(Document.ProcessingStatus.COMPLETED, document.getProcessingStatus());
        verify(documentRepository, times(1)).save(any(Document.class));
        
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testDocumentTypeInference() {
        testDocument.setFileName("invoice.docx");
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        uploadService.extractAndStoreText(1L);

        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void testUploadPdfDocument() throws IOException {
        byte[] pdfContent = "%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj 2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj 3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]>>endobj xref 0 4 0000000000 65535 f 0000000010 00000 n 0000000053 00000 n 0000000102 00000 n trailer<</Size 4/Root 1 0 R>>startxref 149 %%EOF".getBytes();
        
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(pdfContent);
        doNothing().when(mockFile).transferTo(any(java.io.File.class));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            if (doc.getId() == null) {
                doc.setId(1L);
            }
            return doc;
        });

        Document document = uploadService.uploadDocument(mockFile, 1L);

        assertNotNull(document);
        assertEquals("test.pdf", document.getFileName());
        assertEquals(Document.DocumentType.PDF, document.getDocumentType());
        verify(userRepository, times(1)).findById(1L);
    }
}
