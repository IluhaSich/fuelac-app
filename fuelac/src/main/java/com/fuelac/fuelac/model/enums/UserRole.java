package com.fuelac.fuelac.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN("Администратор"),
    ORGANIZATION_MANAGER("Менеджер организации"),
    DISPATCHER("Диспетчер");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static UserRole fromDisplayName(String displayName) {
        for (UserRole role : values()) {
            if (role.displayName.equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        return null;
    }
}
