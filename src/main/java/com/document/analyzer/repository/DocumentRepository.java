package com.document.analyzer.repository;

import com.document.analyzer.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedById(Long userId);

    List<Document> findByProcessingStatus(Document.ProcessingStatus status);

    List<Document> findByDocumentType(Document.DocumentType documentType);

    List<Document> findByUploadedByIdAndProcessingStatus(Long userId, Document.ProcessingStatus status);
}
