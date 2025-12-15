package com.document.analyzer.service;

import com.document.analyzer.entity.Document;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

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
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        doNothing().when(mockFile).transferTo(any(java.io.File.class));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        Document document = uploadService.uploadDocument(mockFile, 1L);

        assertNotNull(document);
        assertEquals("test.pdf", document.getFileName());
        assertEquals(Document.DocumentType.PDF, document.getDocumentType());
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
    void testExtractAndStoreText() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        Document document = uploadService.extractAndStoreText(1L);

        assertNotNull(document);
        assertEquals(Document.ProcessingStatus.COMPLETED, document.getProcessingStatus());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void testDocumentTypeInference() {
        testDocument.setFileName("invoice.docx");
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        uploadService.extractAndStoreText(1L);

        verify(documentRepository, times(1)).findById(1L);
    }
}
