package com.skerdy.Ryel.ryel;

public class RyelPair {

    private String key;
    private Object value;

    public RyelPair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public RyelPair() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
