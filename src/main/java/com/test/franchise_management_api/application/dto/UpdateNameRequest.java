package com.test.franchise_management_api.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(@NotBlank(message = "name is required") String name) {
}

