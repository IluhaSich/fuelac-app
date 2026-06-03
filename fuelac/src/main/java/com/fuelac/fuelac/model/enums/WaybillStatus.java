package com.fuelac.fuelac.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WaybillStatus {
    OPEN("Открыт"),
    CLOSED("Закрыт");

    private final String displayName;

    WaybillStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static WaybillStatus fromDisplayName(String value) {
        if (value == null) return null;
        for (WaybillStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(value)) return status;
        }
        try { return Enum.valueOf(WaybillStatus.class, value.toUpperCase()); }
        catch (IllegalArgumentException ignored) {}
        return null;
    }
}
