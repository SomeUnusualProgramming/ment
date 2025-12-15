package com.document.analyzer.repository;

import com.document.analyzer.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassificationRepository extends JpaRepository<Classification, Long> {

    Optional<Classification> findByDocumentId(Long documentId);

    List<Classification> findByCategory(Classification.DocumentCategory category);

    List<Classification> findByDocumentIdAndVersion(Long documentId, Integer version);

    List<Classification> findByConfidenceGreaterThan(Float confidence);
}
