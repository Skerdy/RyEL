package com.skerdy.Ryel.ryel;

public enum RyelOperator {
    AND('&'),OR('|');

    private final char operator;

    RyelOperator(char operator) {
        this.operator = operator;
    }

    public char getOperator() {
        return operator;
    }
}
