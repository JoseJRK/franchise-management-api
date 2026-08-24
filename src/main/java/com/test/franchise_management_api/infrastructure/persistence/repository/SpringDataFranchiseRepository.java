package com.test.franchise_management_api.infrastructure.persistence.repository;

import com.test.franchise_management_api.infrastructure.persistence.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SpringDataFranchiseRepository extends ReactiveMongoRepository<FranchiseDocument, String> {

    Mono<Boolean> existsByNormalizedName(String normalizedName);

    Mono<Boolean> existsByNormalizedNameAndIdNot(String normalizedName, String excludedId);
}

