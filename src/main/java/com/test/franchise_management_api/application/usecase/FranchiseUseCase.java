package com.test.franchise_management_api.application.usecase;

import com.test.franchise_management_api.application.dto.BranchResponse;
import com.test.franchise_management_api.application.dto.FranchiseResponse;
import com.test.franchise_management_api.application.dto.MaxStockProductResponse;
import com.test.franchise_management_api.application.dto.ProductResponse;
import com.test.franchise_management_api.application.mapper.FranchiseMapper;
import com.test.franchise_management_api.application.usecase.exception.ConflictException;
import com.test.franchise_management_api.application.usecase.exception.NotFoundException;
import com.test.franchise_management_api.domain.model.Branch;
import com.test.franchise_management_api.domain.model.Franchise;
import com.test.franchise_management_api.domain.model.Product;
import com.test.franchise_management_api.domain.repository.BranchRepositoryPort;
import com.test.franchise_management_api.domain.repository.FranchiseRepositoryPort;
import com.test.franchise_management_api.domain.repository.ProductRepositoryPort;
import com.test.franchise_management_api.domain.service.NameNormalizer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FranchiseUseCase {

    private static final String FRANCHISE_ID = "franchiseId";
    private static final String BRANCH_ID = "branchId";
    private static final String PRODUCT_ID = "productId";

    private final FranchiseRepositoryPort franchiseRepository;
    private final BranchRepositoryPort branchRepository;
    private final ProductRepositoryPort productRepository;
    private final FranchiseMapper mapper;
    private final IdValidator idValidator;
    private final NameNormalizer nameNormalizer;

    public FranchiseUseCase(
            FranchiseRepositoryPort franchiseRepository,
            BranchRepositoryPort branchRepository,
            ProductRepositoryPort productRepository,
            FranchiseMapper mapper,
            IdValidator idValidator,
            NameNormalizer nameNormalizer
    ) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.idValidator = idValidator;
        this.nameNormalizer = nameNormalizer;
    }

    public Mono<FranchiseResponse> createFranchise(String name) {
        String sanitizedName = name.trim();
        String normalizedName = nameNormalizer.normalize(sanitizedName);

        return franchiseRepository.existsByNormalizedName(normalizedName)
                .flatMap(exists -> exists
                        ? Mono.error(new ConflictException("Franchise name already exists"))
                        : franchiseRepository.save(new Franchise(null, sanitizedName), normalizedName))
                .map(franchise -> mapper.toResponse(franchise));
    }

    public Mono<BranchResponse> addBranch(String franchiseId, String name) {
        idValidator.validateMongoId(franchiseId, FRANCHISE_ID);
        String sanitizedName = name.trim();
        String normalizedName = nameNormalizer.normalize(sanitizedName);

        return findFranchise(franchiseId)
                .then(branchRepository.existsByFranchiseIdAndNormalizedName(franchiseId, normalizedName))
                .flatMap(exists -> exists
                        ? Mono.error(new ConflictException("Branch name already exists in franchise"))
                        : branchRepository.save(new Branch(null, franchiseId, sanitizedName), normalizedName))
                .map(branch -> mapper.toResponse(branch));
    }

    public Mono<ProductResponse> addProduct(String franchiseId, String branchId, String name, int stock) {
        validateIds(franchiseId, branchId, null);
        String sanitizedName = name.trim();
        String normalizedName = nameNormalizer.normalize(sanitizedName);

        return ensureBranchBelongsToFranchise(branchId, franchiseId)
                .then(productRepository.existsByBranchIdAndFranchiseIdAndNormalizedName(branchId, franchiseId, normalizedName))
                .flatMap(exists -> exists
                        ? Mono.error(new ConflictException("Product name already exists in branch"))
                        : productRepository.save(new Product(null, franchiseId, branchId, sanitizedName, stock), normalizedName))
                .map(product -> mapper.toResponse(product));
    }

    public Mono<Void> deleteProduct(String franchiseId, String branchId, String productId) {
        validateIds(franchiseId, branchId, productId);

        return ensureBranchBelongsToFranchise(branchId, franchiseId)
                .then(findProduct(productId, branchId, franchiseId))
                .flatMap(product -> productRepository.deleteById(product.id()));
    }

    public Mono<ProductResponse> updateProductStock(String franchiseId, String branchId, String productId, int stock) {
        validateIds(franchiseId, branchId, productId);

        return ensureBranchBelongsToFranchise(branchId, franchiseId)
                .then(findProduct(productId, branchId, franchiseId))
                .flatMap(product -> productRepository.save(
                        new Product(product.id(), product.franchiseId(), product.branchId(), product.name(), stock),
                        nameNormalizer.normalize(product.name())
                ))
                .map(product -> mapper.toResponse(product));
    }

    public Flux<MaxStockProductResponse> getMaxStockProductsByBranch(String franchiseId) {
        idValidator.validateMongoId(franchiseId, FRANCHISE_ID);

        return findFranchise(franchiseId)
                .flatMapMany(franchise -> branchRepository.findByFranchiseId(franchiseId)
                        .flatMap(branch -> productRepository
                                .findTopByFranchiseIdAndBranchIdOrderByStockDesc(franchiseId, branch.id())
                                .map(product -> mapper.toMaxStockResponse(franchise, branch, product))));
    }

    public Mono<FranchiseResponse> updateFranchiseName(String franchiseId, String name) {
        idValidator.validateMongoId(franchiseId, FRANCHISE_ID);
        String sanitizedName = name.trim();
        String normalizedName = nameNormalizer.normalize(sanitizedName);

        return findFranchise(franchiseId)
                .flatMap(current -> franchiseRepository.existsByNormalizedNameAndIdNot(normalizedName, franchiseId)
                        .flatMap(exists -> exists
                                ? Mono.error(new ConflictException("Franchise name already exists"))
                                : franchiseRepository.save(new Franchise(current.id(), sanitizedName), normalizedName)))
                .map(franchise -> mapper.toResponse(franchise));
    }

    public Mono<BranchResponse> updateBranchName(String franchiseId, String branchId, String name) {
        validateIds(franchiseId, branchId, null);
        String sanitizedName = name.trim();
        String normalizedName = nameNormalizer.normalize(sanitizedName);

        return ensureBranchBelongsToFranchise(branchId, franchiseId)
                .flatMap(current -> branchRepository
                        .existsByFranchiseIdAndNormalizedNameAndIdNot(franchiseId, normalizedName, branchId)
                        .flatMap(exists -> exists
                                ? Mono.error(new ConflictException("Branch name already exists in franchise"))
                                : branchRepository.save(new Branch(current.id(), current.franchiseId(), sanitizedName), normalizedName)))
                .map(branch -> mapper.toResponse(branch));
    }

    public Mono<ProductResponse> updateProductName(String franchiseId, String branchId, String productId, String name) {
        validateIds(franchiseId, branchId, productId);
        String sanitizedName = name.trim();
        String normalizedName = nameNormalizer.normalize(sanitizedName);

        return ensureBranchBelongsToFranchise(branchId, franchiseId)
                .then(findProduct(productId, branchId, franchiseId))
                .flatMap(current -> productRepository
                        .existsByBranchIdAndFranchiseIdAndNormalizedNameAndIdNot(branchId, franchiseId, normalizedName, productId)
                        .flatMap(exists -> exists
                                ? Mono.error(new ConflictException("Product name already exists in branch"))
                                : productRepository.save(new Product(
                                current.id(),
                                current.franchiseId(),
                                current.branchId(),
                                sanitizedName,
                                current.stock()
                        ), normalizedName)))
                .map(product -> mapper.toResponse(product));
    }

    private Mono<Franchise> findFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")));
    }

    private Mono<Branch> ensureBranchBelongsToFranchise(String branchId, String franchiseId) {
        return findFranchise(franchiseId)
                .then(branchRepository.findByIdAndFranchiseId(branchId, franchiseId)
                        .switchIfEmpty(Mono.error(new NotFoundException("Branch not found in franchise"))));
    }

    private Mono<Product> findProduct(String productId, String branchId, String franchiseId) {
        return productRepository.findByIdAndBranchIdAndFranchiseId(productId, branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")));
    }

    private void validateIds(String franchiseId, String branchId, String productId) {
        idValidator.validateMongoId(franchiseId, FRANCHISE_ID);
        idValidator.validateMongoId(branchId, BRANCH_ID);
        if (productId != null) {
            idValidator.validateMongoId(productId, PRODUCT_ID);
        }
    }
}
