package com.skerdy.Ryel.ryel.core;

import com.skerdy.Ryel.ryel.core.RyelOperator;

 public abstract class Ryel {
    protected RyelOperator operator;
    protected boolean atomic;
    protected boolean root;
    private String expression;
    private Integer ryelRecordId;

    public Ryel(RyelOperator operator) {
        this.operator = operator;
        this.root = false;
        this.atomic = true;
    }

    public RyelOperator getOperator() {
        return operator;
    }

    public void setOperator(RyelOperator operator) {
        this.operator = operator;
    }

    public boolean isAtomic() {
        return atomic;
    }

    public void setAtomic(boolean atomic) {
        this.atomic = atomic;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Integer getRyelRecordId() {
        return ryelRecordId;
    }

    public void setRyelRecordId(Integer ryelRecordId) {
        this.ryelRecordId = ryelRecordId;
    }
}
