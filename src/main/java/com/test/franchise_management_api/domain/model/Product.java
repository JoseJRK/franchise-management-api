package com.test.franchise_management_api.domain.model;

public record Product(String id, String franchiseId, String branchId, String name, int stock) {
}

