package com.skerdy.Ryel.ryel.mongodb;


import com.skerdy.Ryel.ryel.core.Ryel;
import com.skerdy.Ryel.ryel.core.RyelOperator;
import org.json.simple.JSONObject;

import java.util.List;

public abstract class MongoRyel<T,V> extends Ryel {

    protected T mongoOperator;

    protected List<? extends MongoRyel<T,V>> ryelList;

    public MongoRyel(RyelOperator operator) {
        super(operator);
    }

    public abstract T build(JSONObject payload);

    public abstract V getOperation();

    public abstract int getChildsNumber();


    public T getMongoOperator() {
        return mongoOperator;
    }

    public List<? extends MongoRyel<T, V>> getRyelList() {
        return ryelList;
    }

    public void setRyelList(List<? extends MongoRyel<T, V>> ryelList) {
        this.ryelList = ryelList;
    }

    public void setMongoOperator(T mongoOperator) {
        this.mongoOperator = mongoOperator;
    }

}
