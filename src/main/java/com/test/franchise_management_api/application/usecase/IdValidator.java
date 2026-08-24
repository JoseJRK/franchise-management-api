package com.test.franchise_management_api.application.usecase;

import com.test.franchise_management_api.application.usecase.exception.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class IdValidator {

    public void validateMongoId(String id, String fieldName) {
        if (!ObjectId.isValid(id)) {
            throw new BadRequestException(fieldName + " must be a valid Mongo ObjectId");
        }
    }
}

