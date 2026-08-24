package com.test.franchise_management_api.infrastructure.web;

import com.test.franchise_management_api.application.dto.BranchResponse;
import com.test.franchise_management_api.application.dto.CreateBranchRequest;
import com.test.franchise_management_api.application.dto.CreateFranchiseRequest;
import com.test.franchise_management_api.application.dto.CreateProductRequest;
import com.test.franchise_management_api.application.dto.FranchiseResponse;
import com.test.franchise_management_api.application.dto.MaxStockProductResponse;
import com.test.franchise_management_api.application.dto.ProductResponse;
import com.test.franchise_management_api.application.dto.UpdateNameRequest;
import com.test.franchise_management_api.application.dto.UpdateStockRequest;
import com.test.franchise_management_api.application.usecase.FranchiseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/api/v1/franchises")
@Tag(name = "Franchises", description = "Reactive API for franchise management")
public class FranchiseController {

    private final FranchiseUseCase useCase;

    public FranchiseController(FranchiseUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create franchise")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Franchise created"),
            @ApiResponse(responseCode = "409", description = "Duplicated franchise name", content = @Content(schema = @Schema(hidden = true)))
    })
    public Mono<FranchiseResponse> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        return useCase.createFranchise(request.name());
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create branch in a franchise")
    public Mono<BranchResponse> addBranch(
            @PathVariable String franchiseId,
            @Valid @RequestBody CreateBranchRequest request
    ) {
        return useCase.addBranch(franchiseId, request.name());
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create product in branch")
    public Mono<ProductResponse> addProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody CreateProductRequest request
    ) {
        return useCase.addProduct(franchiseId, branchId, request.name(), request.stock());
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product from branch")
    public Mono<Void> deleteProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId
    ) {
        return useCase.deleteProduct(franchiseId, branchId, productId);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    @Operation(summary = "Update product stock")
    public Mono<ProductResponse> updateStock(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        return useCase.updateProductStock(franchiseId, branchId, productId, request.stock());
    }

    @GetMapping("/{franchiseId}/products/max-stock")
    @Operation(summary = "Get top stock product by branch")
    public Flux<MaxStockProductResponse> getMaxStockByBranch(@PathVariable String franchiseId) {
        return useCase.getMaxStockProductsByBranch(franchiseId);
    }

    @PatchMapping("/{franchiseId}")
    @Operation(summary = "Update franchise name")
    public Mono<FranchiseResponse> updateFranchiseName(
            @PathVariable String franchiseId,
            @Valid @RequestBody UpdateNameRequest request
    ) {
        return useCase.updateFranchiseName(franchiseId, request.name());
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}")
    @Operation(summary = "Update branch name")
    public Mono<BranchResponse> updateBranchName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody UpdateNameRequest request
    ) {
        return useCase.updateBranchName(franchiseId, branchId, request.name());
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @Operation(summary = "Update product name")
    public Mono<ProductResponse> updateProductName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateNameRequest request
    ) {
        return useCase.updateProductName(franchiseId, branchId, productId, request.name());
    }
}

