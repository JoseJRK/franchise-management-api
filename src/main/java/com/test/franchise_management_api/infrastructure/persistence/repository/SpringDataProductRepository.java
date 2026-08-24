package com.test.franchise_management_api.infrastructure.persistence.repository;

import com.test.franchise_management_api.infrastructure.persistence.document.ProductDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SpringDataProductRepository extends ReactiveMongoRepository<ProductDocument, String> {

    Mono<ProductDocument> findByIdAndBranchIdAndFranchiseId(String id, String branchId, String franchiseId);

    Mono<Boolean> existsByBranchIdAndFranchiseIdAndNormalizedName(String branchId, String franchiseId, String normalizedName);

    Mono<Boolean> existsByBranchIdAndFranchiseIdAndNormalizedNameAndIdNot(
            String branchId,
            String franchiseId,
            String normalizedName,
            String excludedId
    );

    Mono<ProductDocument> findTopByFranchiseIdAndBranchIdOrderByStockDesc(String franchiseId, String branchId);
}

