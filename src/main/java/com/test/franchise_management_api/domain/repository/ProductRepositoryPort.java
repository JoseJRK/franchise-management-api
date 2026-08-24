package com.test.franchise_management_api.domain.repository;

import com.test.franchise_management_api.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {

    Mono<Product> save(Product product, String normalizedName);

    Mono<Product> findByIdAndBranchIdAndFranchiseId(String id, String branchId, String franchiseId);

    Mono<Boolean> existsByBranchIdAndFranchiseIdAndNormalizedName(String branchId, String franchiseId, String normalizedName);

    Mono<Boolean> existsByBranchIdAndFranchiseIdAndNormalizedNameAndIdNot(String branchId, String franchiseId, String normalizedName, String excludedId);

    Mono<Product> findTopByFranchiseIdAndBranchIdOrderByStockDesc(String franchiseId, String branchId);

    Mono<Void> deleteById(String id);
}

