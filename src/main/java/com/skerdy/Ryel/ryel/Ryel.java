package com.skerdy.Ryel.ryel;

import org.json.simple.JSONObject;

import java.util.List;

public abstract class Ryel<T,V> {

    protected T criteria;
    protected RyelOperator operator;
    protected List<Ryel<T,V>>  ryelList;
    protected boolean atomic;
    protected boolean root;
    private String expression;
    private Integer ryelRecordId;

    public Ryel(RyelOperator operator) {
        this.operator = operator;
        this.root = false;
    }


    public abstract void buildCriteria(JSONObject payload);

    public abstract V getQuery();

    public int getChildsNumber(){
        return ryelList.size();
    }


    public T getCriteria() {
        return criteria;
    }

    public void setCriteria(T criteria) {
        this.criteria = criteria;
    }

    public RyelOperator getOperator() {
        return operator;
    }

    public void setOperator(RyelOperator operator) {
        this.operator = operator;
    }

    public List<Ryel<T,V>> getRyelList() {
        return ryelList;
    }

    public void setRyelList(List<Ryel<T,V>> ryelList) {
        this.ryelList = ryelList;
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
