package com.test.franchise_management_api.infrastructure.persistence.repository;

import com.test.franchise_management_api.infrastructure.persistence.document.BranchDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpringDataBranchRepository extends ReactiveMongoRepository<BranchDocument, String> {

    Mono<BranchDocument> findByIdAndFranchiseId(String id, String franchiseId);

    Flux<BranchDocument> findByFranchiseId(String franchiseId);

    Mono<Boolean> existsByFranchiseIdAndNormalizedName(String franchiseId, String normalizedName);

    Mono<Boolean> existsByFranchiseIdAndNormalizedNameAndIdNot(String franchiseId, String normalizedName, String excludedId);
}

