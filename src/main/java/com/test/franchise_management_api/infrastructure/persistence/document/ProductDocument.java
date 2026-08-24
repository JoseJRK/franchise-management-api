package com.test.franchise_management_api.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@CompoundIndex(name = "ux_branch_product_name", def = "{'franchiseId': 1, 'branchId': 1, 'normalizedName': 1}", unique = true)
@CompoundIndex(name = "ix_branch_stock", def = "{'franchiseId': 1, 'branchId': 1, 'stock': -1}")
public class ProductDocument {

    @Id
    private String id;
    private String franchiseId;
    private String branchId;
    private String name;
    private String normalizedName;
    private int stock;

    public ProductDocument() {
    }

    public ProductDocument(String id, String franchiseId, String branchId, String name, String normalizedName, int stock) {
        this.id = id;
        this.franchiseId = franchiseId;
        this.branchId = branchId;
        this.name = name;
        this.normalizedName = normalizedName;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }
}

