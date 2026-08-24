package com.test.franchise_management_api.domain.repository;

import com.test.franchise_management_api.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {

    Mono<Branch> save(Branch branch, String normalizedName);

    Mono<Branch> findByIdAndFranchiseId(String id, String franchiseId);

    Flux<Branch> findByFranchiseId(String franchiseId);

    Mono<Boolean> existsByFranchiseIdAndNormalizedName(String franchiseId, String normalizedName);

    Mono<Boolean> existsByFranchiseIdAndNormalizedNameAndIdNot(String franchiseId, String normalizedName, String excludedId);
}

