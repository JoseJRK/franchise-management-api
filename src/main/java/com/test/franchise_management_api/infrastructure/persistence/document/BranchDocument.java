package com.test.franchise_management_api.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "branches")
@CompoundIndex(name = "ux_franchise_branch_name", def = "{'franchiseId': 1, 'normalizedName': 1}", unique = true)
public class BranchDocument {

    @Id
    private String id;
    private String franchiseId;
    private String name;
    private String normalizedName;

    public BranchDocument() {
    }

    public BranchDocument(String id, String franchiseId, String name, String normalizedName) {
        this.id = id;
        this.franchiseId = franchiseId;
        this.name = name;
        this.normalizedName = normalizedName;
    }

    public String getId() {
        return id;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }
}

