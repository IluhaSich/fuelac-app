package com.fuelac.fuelac.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
    URBAN("Городские перевозки"),
    SUBURBAN("Пригородные перевозки"),
    INTERCITY("Междугородние перевозки"),
    INTERNATIONAL("Международные перевозки");

    private final String displayName;

    MessageType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static MessageType fromDisplayName(String value) {
        if (value == null) return null;
        for (MessageType type : values()) {
            if (type.displayName.equalsIgnoreCase(value)) return type;
        }
        try { return Enum.valueOf(MessageType.class, value.toUpperCase()); }
        catch (IllegalArgumentException ignored) {}
        return null;
    }
}
