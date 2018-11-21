package com.skerdy.Ryel.ryel.mongodb;

import com.skerdy.Ryel.ryel.core.RyelOperator;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

public class MongoRyelUpdate extends MongoRyel<Criteria,Update> {

    public MongoRyelUpdate(RyelOperator operator) {
        super(operator);
    }

    @Override
    public Criteria build(JSONObject payload) {


        return null;
    }

    @Override
    public Update getOperation() {
        return null;
    }

    @Override
    public int getChildsNumber() {
        return ryelList.size();
    }
}
