package com.barclays.api.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    PERSONAL("personal");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AccountType fromValue(String value) {
        for (AccountType accountType : AccountType.values()) {
            if (accountType.value.equals(value)) {
                return accountType;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
