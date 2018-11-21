package com.skerdy.Ryel;


import com.skerdy.Ryel.ryel.generators.MongoCriteriaGenerator;
import com.skerdy.Ryel.ryel.Ryel;
import com.skerdy.Ryel.ryel.RyelOperator;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoRyel extends Ryel<Criteria, Query> {

    private MongoCriteriaGenerator criteriaGenerator;

    private MongoRyel

    public MongoRyel(RyelOperator operator) {
        super(operator);
        criteriaGenerator = new MongoCriteriaGenerator();
    }

    //metoda qe kthen kriterian per childed
    @Override
    public void buildCriteria(JSONObject payload) {
        if(ryelList !=null && ryelList.isEmpty()){
            this.criteria = criteriaGenerator.generateCriteria(getExpression(), payload);
        }
        else {
            Criteria[] criteria = new Criteria[getChildsNumber()];

            for (int i = 0; i < getChildsNumber(); i++) {
                Ryel<Criteria, Query> ryel = ryelList.get(i);
                if (ryel.isAtomic()) {
                    if(ryel.getCriteria()==null) {
                        criteria[i] = criteriaGenerator.generateCriteria(ryel.getExpression(), payload);
                    }
                    else {
                        criteria[i] = ryel.getCriteria();
                    }
                } else {
                    criteria[i] = ryel.getCriteria();
                }
            }

            if(operator!=null) {
                if (operator.equals(RyelOperator.AND)) {
                    this.criteria = Criteria.where("").andOperator(criteria);
                } else {
                    this.criteria = Criteria.where("").orOperator(criteria);
                }
            }
        }
    }


    //metoda publike qe kthen query e root

    @Override
    public Query getQuery() {
        Query query = new Query();
        if (root) {
                query.addCriteria(this.criteria);

        }
        return query;
    }


    @Override
    public String toString() {
        return "MongoRyel{" +
                "criteria=" + criteria +
                ", operator=" + operator +
                ", ryelList=" + ryelList +
                ", atomic=" + atomic +
                ", root=" + root +
                '}';
    }
}
