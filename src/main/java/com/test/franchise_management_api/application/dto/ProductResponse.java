package com.test.franchise_management_api.application.dto;

public record ProductResponse(String id, String franchiseId, String branchId, String name, int stock) {
}

