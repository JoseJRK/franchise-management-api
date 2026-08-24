package com.test.franchise_management_api.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
        @NotBlank(message = "name is required") String name,
        @Min(value = 0, message = "stock must be greater than or equal to 0") int stock
) {
}

