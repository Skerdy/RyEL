package com.skerdy.Ryel.ryel;

import com.skerdy.Ryel.MongoRyel;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

public class RyelMongoParser extends AbstractRyelParser<MongoRyel> {



    @Override
    protected void calculate(String query, JSONObject jsonObject) {
        parse(query);
        int queueSize = queue.size();
        for (int i = 0; i < queueSize; i++) {
             ryels.add(convert(queue.remove(), jsonObject));
        }
    }

    @Override
    protected MongoRyel convert(RyelRecord record, JSONObject jsonObject) {
        MongoRyel ryel = new MongoRyel(record.getOperator());
        if(record.isAtomic()){
            ryel.setRyelList(new ArrayList<>());
        }
        else{
            ryel.setRyelList(getRyelList(record));
        }
        ryel.setAtomic(record.isAtomic());
        ryel.setExpression(record.getExpression());
        ryel.setRoot(record.isRoot());
        ryel.setRyelRecordId(record.getId());
        ryel.buildCriteria(jsonObject);
        return ryel;
    }
}
