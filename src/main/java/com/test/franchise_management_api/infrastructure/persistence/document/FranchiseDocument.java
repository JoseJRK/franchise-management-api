package com.test.franchise_management_api.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "franchises")
public class FranchiseDocument {

    @Id
    private String id;
    private String name;

    @Indexed(unique = true)
    private String normalizedName;

    public FranchiseDocument() {
    }

    public FranchiseDocument(String id, String name, String normalizedName) {
        this.id = id;
        this.name = name;
        this.normalizedName = normalizedName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }
}

