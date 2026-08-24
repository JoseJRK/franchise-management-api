package com.test.franchise_management_api.infrastructure.persistence.adapter;

import com.test.franchise_management_api.domain.model.Product;
import com.test.franchise_management_api.domain.repository.ProductRepositoryPort;
import com.test.franchise_management_api.infrastructure.persistence.document.ProductDocument;
import com.test.franchise_management_api.infrastructure.persistence.repository.SpringDataProductRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository repository;

    public ProductRepositoryAdapter(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Product> save(Product product, String normalizedName) {
        ProductDocument document = new ProductDocument(
                product.id(),
                product.franchiseId(),
                product.branchId(),
                product.name(),
                normalizedName,
                product.stock()
        );
        return repository.save(document)
                .map(saved -> new Product(
                        saved.getId(),
                        saved.getFranchiseId(),
                        saved.getBranchId(),
                        saved.getName(),
                        saved.getStock()
                ));
    }

    @Override
    public Mono<Product> findByIdAndBranchIdAndFranchiseId(String id, String branchId, String franchiseId) {
        return repository.findByIdAndBranchIdAndFranchiseId(id, branchId, franchiseId)
                .map(found -> new Product(
                        found.getId(),
                        found.getFranchiseId(),
                        found.getBranchId(),
                        found.getName(),
                        found.getStock()
                ));
    }

    @Override
    public Mono<Boolean> existsByBranchIdAndFranchiseIdAndNormalizedName(
            String branchId,
            String franchiseId,
            String normalizedName
    ) {
        return repository.existsByBranchIdAndFranchiseIdAndNormalizedName(branchId, franchiseId, normalizedName);
    }

    @Override
    public Mono<Boolean> existsByBranchIdAndFranchiseIdAndNormalizedNameAndIdNot(
            String branchId,
            String franchiseId,
            String normalizedName,
            String excludedId
    ) {
        return repository.existsByBranchIdAndFranchiseIdAndNormalizedNameAndIdNot(
                branchId,
                franchiseId,
                normalizedName,
                excludedId
        );
    }

    @Override
    public Mono<Product> findTopByFranchiseIdAndBranchIdOrderByStockDesc(String franchiseId, String branchId) {
        return repository.findTopByFranchiseIdAndBranchIdOrderByStockDesc(franchiseId, branchId)
                .map(found -> new Product(
                        found.getId(),
                        found.getFranchiseId(),
                        found.getBranchId(),
                        found.getName(),
                        found.getStock()
                ));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}

