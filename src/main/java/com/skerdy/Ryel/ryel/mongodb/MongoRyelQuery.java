package com.skerdy.Ryel.ryel.mongodb;

import com.skerdy.Ryel.ryel.core.RyelOperator;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoRyelQuery extends MongoRyel<Criteria,Query> {

    private MongoCriteriaGenerator criteriaGenerator;

    public MongoRyelQuery(RyelOperator operator) {
        super(operator);
        criteriaGenerator = new MongoCriteriaGenerator();
    }

    @Override
    public Criteria build(JSONObject payload) {
        if(ryelList !=null && ryelList.isEmpty()){
            this.mongoOperator = criteriaGenerator.generateCriteria(getExpression(), payload);
        }
        else {
            Criteria[] criteria = new Criteria[getChildsNumber()];

            for (int i = 0; i < getChildsNumber(); i++) {
                MongoRyel<Criteria,Query> ryel = ryelList.get(i);
                if (ryel.isAtomic()) {
                    if(ryel.getMongoOperator()==null) {
                        criteria[i] = criteriaGenerator.generateCriteria(ryel.getExpression(), payload);
                    }
                    else {
                        criteria[i] = ryel.getMongoOperator();
                    }
                } else {
                    criteria[i] = ryel.getMongoOperator();
                }
            }

            if(operator!=null) {
                if (operator.equals(RyelOperator.AND)) {
                    this.mongoOperator = Criteria.where("").andOperator(criteria);
                } else {
                    this.mongoOperator = Criteria.where("").orOperator(criteria);
                }
            }
        }
        return mongoOperator;
    }

    @Override
    public Query getOperation() {
        Query query = new Query();
        if (root) {
            query.addCriteria(this.mongoOperator);

        }
        return query;
    }

    @Override
    public int getChildsNumber() {
        return ryelList.size();
    }




}
