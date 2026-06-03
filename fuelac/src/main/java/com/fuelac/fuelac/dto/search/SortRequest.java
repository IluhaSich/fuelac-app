package com.fuelac.fuelac.dto.search;

public class SortRequest {
    private String key;
    private String direction;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
