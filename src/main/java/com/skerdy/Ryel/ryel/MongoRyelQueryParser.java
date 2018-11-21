package com.skerdy.Ryel.ryel;

import com.skerdy.Ryel.ryel.core.RyelRecordParser;
import com.skerdy.Ryel.ryel.core.RyelRecord;
import com.skerdy.Ryel.ryel.mongodb.MongoRyelQuery;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MongoRyelQueryParser extends RyelRecordParser<MongoRyelQuery> {

    public MongoRyelQueryParser() {
    }

    @Override
    protected MongoRyelQuery convert(RyelRecord record, JSONObject jsonObject) {
        MongoRyelQuery ryel = new MongoRyelQuery(record.getOperator());
        if(record.isAtomic()){
            ryel.setRyelList(new ArrayList<>());
        }
        else{
            ryel.setRyelList(getChildrensForRecord(record));
        }
        ryel.setAtomic(record.isAtomic());
        ryel.setExpression(record.getExpression());
        ryel.setRoot(record.isRoot());
        ryel.setRyelRecordId(record.getId());
        ryel.build(jsonObject);
        return ryel;
    }

    @Override
    protected List<MongoRyelQuery> getChildrensForRecord(RyelRecord ryelRecord) {
        List<MongoRyelQuery> result = new ArrayList<>();
        for(RyelRecord record : getChildrenRecords(ryelRecord)){
            for( MongoRyelQuery ryel : ryels){
                if(ryel.getRyelRecordId().equals(record.getId())){
                    result.add(ryel);
                }
            }
        }
        return result;
    }
}
