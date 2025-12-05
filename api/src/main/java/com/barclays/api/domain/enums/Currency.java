package com.barclays.api.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    GBP("GBP");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Currency fromValue(String value) {
        for (Currency currency : Currency.values()) {
            if (currency.value.equals(value)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
