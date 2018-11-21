package com.skerdy.Ryel.ryel;

import java.util.List;

public class RyelRecord{

    private Integer level;
    private Integer index;
    private List<RyelRecord> records;
    private String expression ="";
    private boolean atomic;
    private RyelOperator operator;
    private Integer id;
    private Integer parentId;

    // after first processing




    public RyelRecord(Integer level, Integer index, Integer id, Integer parentId) {
        this.level = level;
        this.index = index;
        this.id = id;
        this.parentId = parentId;
    }

    public RyelRecord(Integer level, Integer index, Integer id) {
        this.level = level;
        this.index = index;
        this.id = id;
    }

    public RyelRecord(Integer level, Integer index) {
        this.level = level;
        this.atomic =true;
        this.index = index;
    }


    public void addChar(StringBuilder stringBuilder, char ch){
        this.expression = stringBuilder.append(expression).append(ch).toString();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<RyelRecord> getRecords() {
        return records;
    }

    public RyelRecord setRecords(List<RyelRecord> records) {
        this.records = records;
        return this;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isAtomic() {
        return atomic;
    }

    public void setAtomic(boolean atomic) {
        this.atomic = atomic;
    }

    public RyelOperator getOperator() {
        return operator;
    }

    public void setOperator(RyelOperator operator) {
        this.operator = operator;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RyelRecord{" +
                "level=" + level +
                ", index=" + index +
                ", records=" + records +
                ", expression='" + expression + '\'' +
                ", atomic=" + atomic +
                ", operator=" + operator +
                ", id=" + id +
                ", parentId=" + parentId +
                '}';
    }




    public void printNice(){
        System.out.println(toString());
        for(RyelRecord record : records){
            System.out.println(records);
            record.printNice();
        }
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public boolean isRoot(){
        return id.equals(0);
    }
}
