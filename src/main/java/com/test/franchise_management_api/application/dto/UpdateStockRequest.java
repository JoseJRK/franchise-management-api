package com.test.franchise_management_api.application.dto;

import jakarta.validation.constraints.Min;

public record UpdateStockRequest(@Min(value = 0, message = "stock must be greater than or equal to 0") int stock) {
}

