package com.test.franchise_management_api.application.mapper;

import com.test.franchise_management_api.application.dto.BranchResponse;
import com.test.franchise_management_api.application.dto.FranchiseResponse;
import com.test.franchise_management_api.application.dto.MaxStockProductResponse;
import com.test.franchise_management_api.application.dto.ProductResponse;
import com.test.franchise_management_api.domain.model.Branch;
import com.test.franchise_management_api.domain.model.Franchise;
import com.test.franchise_management_api.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class FranchiseMapper {

    public FranchiseResponse toResponse(Franchise franchise) {
        return new FranchiseResponse(franchise.id(), franchise.name());
    }

    public BranchResponse toResponse(Branch branch) {
        return new BranchResponse(branch.id(), branch.franchiseId(), branch.name());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.id(), product.franchiseId(), product.branchId(), product.name(), product.stock());
    }

    public MaxStockProductResponse toMaxStockResponse(Franchise franchise, Branch branch, Product product) {
        return new MaxStockProductResponse(
                franchise.id(),
                franchise.name(),
                branch.id(),
                branch.name(),
                product.id(),
                product.name(),
                product.stock()
        );
    }
}

