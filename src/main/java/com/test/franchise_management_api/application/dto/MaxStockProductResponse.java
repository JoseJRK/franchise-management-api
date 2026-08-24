package com.test.franchise_management_api.application.dto;

public record MaxStockProductResponse(
        String franchiseId,
        String franchise,
        String branchId,
        String branch,
        String productId,
        String product,
        int stock
) {
}

