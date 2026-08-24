package com.test.franchise_management_api.domain.service;

import org.springframework.stereotype.Component;

@Component
public class NameNormalizer {

    public String normalize(String value) {
        return value.trim().toLowerCase();
    }
}

