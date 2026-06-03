package com.fuelac.fuelac.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransportationType {
    PASSENGER("Пассажирские перевозки"),
    CARGO("Грузовые перевозки"),
    SPECIAL("Специальные перевозки");

    private final String displayName;

    TransportationType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static TransportationType fromDisplayName(String value) {
        if (value == null) return null;
        for (TransportationType type : values()) {
            if (type.displayName.equalsIgnoreCase(value)) return type;
        }
        try { return Enum.valueOf(TransportationType.class, value.toUpperCase()); }
        catch (IllegalArgumentException ignored) {}
        return null;
    }
}
