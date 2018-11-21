package com.skerdy.Ryel.ryel;

import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.HashMap;
import java.util.Map;

public class MongoCriteriaGenerator implements CriteriaGenerator<Criteria,String> {

    private final String PAYLOAD_REGEX = "payload(";

    @Override
    public Criteria generateCriteria(String s, JSONObject payload) {
        RyelPair pair = generatePair(s,payload);
        return Criteria.where(pair.getKey()).is(pair.getValue());
    }

    private RyelPair generatePair(String s, JSONObject payload){
        RyelPair result = new RyelPair();
        s = s.replace("\"","").replace("{", "").replace("}","");
        String[] pair = s.split(":");
        result.setKey(pair[0]);
        result.setValue(formatValue(pair[1],payload));
        return result;
    }

    private Object formatValue(String value, JSONObject payload){
        if(value.contains(PAYLOAD_REGEX)){
            value = value.replace(PAYLOAD_REGEX,"").replace(")", "");
            return payload.get(value);
        }
        return value;
    }

}
