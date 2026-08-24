package com.test.franchise_management_api.application.usecase;

import com.test.franchise_management_api.application.mapper.FranchiseMapper;
import com.test.franchise_management_api.application.usecase.exception.BadRequestException;
import com.test.franchise_management_api.application.usecase.exception.ConflictException;
import com.test.franchise_management_api.application.usecase.exception.NotFoundException;
import com.test.franchise_management_api.domain.model.Branch;
import com.test.franchise_management_api.domain.model.Franchise;
import com.test.franchise_management_api.domain.model.Product;
import com.test.franchise_management_api.domain.repository.BranchRepositoryPort;
import com.test.franchise_management_api.domain.repository.FranchiseRepositoryPort;
import com.test.franchise_management_api.domain.repository.ProductRepositoryPort;
import com.test.franchise_management_api.domain.service.NameNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    private static final String FRANCHISE_ID = "65f1a9e6a0c1f4d3b8a77a11";
    private static final String BRANCH_ID = "65f1a9e6a0c1f4d3b8a77a12";
    private static final String PRODUCT_ID = "65f1a9e6a0c1f4d3b8a77a13";

    @Mock
    private FranchiseRepositoryPort franchiseRepository;
    @Mock
    private BranchRepositoryPort branchRepository;
    @Mock
    private ProductRepositoryPort productRepository;

    private FranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FranchiseUseCase(
                franchiseRepository,
                branchRepository,
                productRepository,
                new FranchiseMapper(),
                new IdValidator(),
                new NameNormalizer()
        );
    }

    @Test
    void shouldCreateFranchise() {
        when(franchiseRepository.existsByNormalizedName("acme")).thenReturn(Mono.just(false));
        when(franchiseRepository.save(any(), anyString())).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));

        StepVerifier.create(useCase.createFranchise("Acme"))
                .expectNextMatches(response -> response.id().equals(FRANCHISE_ID) && response.name().equals("Acme"))
                .verifyComplete();
    }

    @Test
    void shouldRejectDuplicatedFranchiseName() {
        when(franchiseRepository.existsByNormalizedName("acme")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createFranchise("Acme"))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldCreateBranch() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.existsByFranchiseIdAndNormalizedName(FRANCHISE_ID, "central")).thenReturn(Mono.just(false));
        when(branchRepository.save(any(), anyString())).thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));

        StepVerifier.create(useCase.addBranch(FRANCHISE_ID, "Central"))
                .expectNextMatches(response -> response.id().equals(BRANCH_ID))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenFranchiseDoesNotExist() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addBranch(FRANCHISE_ID, "Central"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldCreateProduct() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(productRepository.existsByBranchIdAndFranchiseIdAndNormalizedName(BRANCH_ID, FRANCHISE_ID, "widget"))
                .thenReturn(Mono.just(false));
        when(productRepository.save(any(), anyString()))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget", 20)));

        StepVerifier.create(useCase.addProduct(FRANCHISE_ID, BRANCH_ID, "Widget", 20))
                .expectNextMatches(response -> response.id().equals(PRODUCT_ID) && response.stock() == 20)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBranchDoesNotExist() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addProduct(FRANCHISE_ID, BRANCH_ID, "Widget", 20))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldDeleteProduct() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(productRepository.findByIdAndBranchIdAndFranchiseId(PRODUCT_ID, BRANCH_ID, FRANCHISE_ID))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget", 20)));
        when(productRepository.deleteById(PRODUCT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(FRANCHISE_ID, BRANCH_ID, PRODUCT_ID))
                .verifyComplete();
    }

    @Test
    void shouldUpdateStock() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(productRepository.findByIdAndBranchIdAndFranchiseId(PRODUCT_ID, BRANCH_ID, FRANCHISE_ID))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget", 20)));
        when(productRepository.save(any(), anyString()))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget", 99)));

        StepVerifier.create(useCase.updateProductStock(FRANCHISE_ID, BRANCH_ID, PRODUCT_ID, 99))
                .expectNextMatches(response -> response.stock() == 99)
                .verifyComplete();
    }

    @Test
    void shouldFailUpdateStockWhenProductDoesNotExist() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(productRepository.findByIdAndBranchIdAndFranchiseId(PRODUCT_ID, BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProductStock(FRANCHISE_ID, BRANCH_ID, PRODUCT_ID, 9))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldGetMaxStockByBranch() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByFranchiseId(FRANCHISE_ID)).thenReturn(Flux.just(
                new Branch(BRANCH_ID, FRANCHISE_ID, "Central"),
                new Branch("65f1a9e6a0c1f4d3b8a77a22", FRANCHISE_ID, "North")
        ));
        when(productRepository.findTopByFranchiseIdAndBranchIdOrderByStockDesc(FRANCHISE_ID, BRANCH_ID))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget", 20)));
        when(productRepository.findTopByFranchiseIdAndBranchIdOrderByStockDesc(FRANCHISE_ID, "65f1a9e6a0c1f4d3b8a77a22"))
                .thenReturn(Mono.just(new Product("65f1a9e6a0c1f4d3b8a77a33", FRANCHISE_ID, "65f1a9e6a0c1f4d3b8a77a22", "Cable", 12)));

        StepVerifier.create(useCase.getMaxStockProductsByBranch(FRANCHISE_ID))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldUpdateFranchiseName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(franchiseRepository.existsByNormalizedNameAndIdNot("globex", FRANCHISE_ID)).thenReturn(Mono.just(false));
        when(franchiseRepository.save(any(), anyString())).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Globex")));

        StepVerifier.create(useCase.updateFranchiseName(FRANCHISE_ID, "Globex"))
                .expectNextMatches(response -> response.name().equals("Globex"))
                .verifyComplete();
    }

    @Test
    void shouldUpdateBranchName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID))
                .thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(branchRepository.existsByFranchiseIdAndNormalizedNameAndIdNot(FRANCHISE_ID, "uptown", BRANCH_ID))
                .thenReturn(Mono.just(false));
        when(branchRepository.save(any(), anyString()))
                .thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Uptown")));

        StepVerifier.create(useCase.updateBranchName(FRANCHISE_ID, BRANCH_ID, "Uptown"))
                .expectNextMatches(response -> response.name().equals("Uptown"))
                .verifyComplete();
    }

    @Test
    void shouldUpdateProductName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID))
                .thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(productRepository.findByIdAndBranchIdAndFranchiseId(PRODUCT_ID, BRANCH_ID, FRANCHISE_ID))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget", 20)));
        when(productRepository.existsByBranchIdAndFranchiseIdAndNormalizedNameAndIdNot(BRANCH_ID, FRANCHISE_ID, "widget pro", PRODUCT_ID))
                .thenReturn(Mono.just(false));
        when(productRepository.save(any(), anyString()))
                .thenReturn(Mono.just(new Product(PRODUCT_ID, FRANCHISE_ID, BRANCH_ID, "Widget Pro", 20)));

        StepVerifier.create(useCase.updateProductName(FRANCHISE_ID, BRANCH_ID, PRODUCT_ID, "Widget Pro"))
                .expectNextMatches(response -> response.name().equals("Widget Pro"))
                .verifyComplete();
    }

    @Test
    void shouldFailWithInvalidIds() {
        StepVerifier.create(useCase.addBranch("invalid-id", "Central"))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void shouldFailWhenDuplicatedProductName() {
        when(franchiseRepository.findById(FRANCHISE_ID)).thenReturn(Mono.just(new Franchise(FRANCHISE_ID, "Acme")));
        when(branchRepository.findByIdAndFranchiseId(BRANCH_ID, FRANCHISE_ID)).thenReturn(Mono.just(new Branch(BRANCH_ID, FRANCHISE_ID, "Central")));
        when(productRepository.existsByBranchIdAndFranchiseIdAndNormalizedName(BRANCH_ID, FRANCHISE_ID, "widget"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(useCase.addProduct(FRANCHISE_ID, BRANCH_ID, "Widget", 10))
                .expectError(ConflictException.class)
                .verify();
    }
}
