package com.vizzionnaire.server.dao.attributes;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.kv.AttributeKvEntry;
import com.vizzionnaire.server.dao.exception.IncorrectParameterException;
import com.vizzionnaire.server.dao.service.Validator;

public class AttributeUtils {
    public static void validate(EntityId id, String scope) {
        Validator.validateId(id.getId(), "Incorrect id " + id);
        Validator.validateString(scope, "Incorrect scope " + scope);
    }

    public static void validate(AttributeKvEntry kvEntry) {
        if (kvEntry == null) {
            throw new IncorrectParameterException("Key value entry can't be null");
        } else if (kvEntry.getDataType() == null) {
            throw new IncorrectParameterException("Incorrect kvEntry. Data type can't be null");
        } else {
            Validator.validateString(kvEntry.getKey(), "Incorrect kvEntry. Key can't be empty");
            Validator.validatePositiveNumber(kvEntry.getLastUpdateTs(), "Incorrect last update ts. Ts should be positive");
        }
    }
}
