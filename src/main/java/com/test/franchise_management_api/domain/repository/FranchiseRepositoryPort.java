package com.test.franchise_management_api.domain.repository;

import com.test.franchise_management_api.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {

    Mono<Franchise> save(Franchise franchise, String normalizedName);

    Mono<Franchise> findById(String id);

    Mono<Boolean> existsByNormalizedName(String normalizedName);

    Mono<Boolean> existsByNormalizedNameAndIdNot(String normalizedName, String excludedId);
}

