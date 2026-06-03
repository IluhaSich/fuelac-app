package com.fuelac.fuelac.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VehicleType {
    LIGHT("Легковой"),
    TRUCK("Грузовой"),
    SPECIAL("Специальный");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static VehicleType fromDisplayName(String value) {
        if (value == null) return null;
        for (VehicleType type : values()) {
            if (type.displayName.equalsIgnoreCase(value)) return type;
        }
        try { return Enum.valueOf(VehicleType.class, value.toUpperCase()); }
        catch (IllegalArgumentException ignored) {}
        return null;
    }
}
