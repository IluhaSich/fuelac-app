package com.fuelac.fuelac.dto.search;

public class FilterRequest {
    private String key;
    private Operator operator;
    private FieldType fieldType;
    private Object value;
    private Object valueTo;
    private String orGroup;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValueTo() {
        return valueTo;
    }

    public void setValueTo(Object valueTo) {
        this.valueTo = valueTo;
    }

    public String getOrGroup() {
        return orGroup;
    }

    public void setOrGroup(String orGroup) {
        this.orGroup = orGroup;
    }
}
