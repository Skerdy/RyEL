package com.skerdy.Ryel.ryel;

import com.skerdy.Ryel.MongoRyel;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RyelParser {

    private final char OPENING_BRACKET = '{';
    private final char CLOSING_BRACKET = '}';

    private char currentChar;
    private Integer currentLevel;
    private Integer currentIndex;

    private RyelRecord currentRecord;

    private Map<RyelMapper, RyelRecord> mappedRecords;

    private List<RyelRecord> ryelRecords;

    private Integer incrementalId;

    private List<Integer> generatedIds;

    private Stack<RyelRecord> eligibleStack;

    private Queue<RyelRecord> queue;

    private List<MongoRyel<Criteria, Query>> mongoRyels;

    public RyelParser() {
        ryelRecords = new ArrayList<>();
        incrementalId = 0;
    }

    public List<RyelRecord> getRecords(String queryString) {
        mappedRecords = new HashMap<>();
        generatedIds = new ArrayList<>();
        eligibleStack = new Stack<>();
        mongoRyels = new ArrayList<>();
        currentLevel = -1;
        currentIndex = 0;
        incrementalId = -1;
        List<RyelRecord> result = new ArrayList<>();

        for (int i = 0; i < queryString.length(); i++) {
            currentChar = queryString.charAt(i);
            if (currentChar == OPENING_BRACKET) {
                ++currentLevel;
                RyelMapper currentMapper = next(currentLevel);

                incrementalId = incrementalId+1;
                generatedIds.add(incrementalId);

                currentRecord = new RyelRecord(currentLevel, currentMapper.getIndex(), incrementalId);

                eligibleStack.push(currentRecord);

                mappedRecords.put(currentMapper, currentRecord);

                //validateEligibleRecords(currentLevel, currentIndex);
                writeAppendingChar(currentChar);

            } else if (currentChar == CLOSING_BRACKET) {

                --currentLevel;
                writeAppendingChar(currentChar);
                RyelRecord popped = eligibleStack.pop();
                if(eligibleStack.size()>0)
                popped.setParentId(eligibleStack.peek().getId());

                // validateEligibleRecords(currentLevel, currentIndex);

            } else if (currentChar == RyelOperator.AND.getOperator()) {
                setOperator(eligibleStack.peek().getId(), RyelOperator.AND);
            } else if (currentChar == RyelOperator.OR.getOperator()) {
                setOperator(eligibleStack.peek().getId(), RyelOperator.OR);
            } else {
                writeAppendingChar(currentChar);
            }
        }

        this.ryelRecords = new ArrayList<>(mappedRecords.values());
        validateAtomic();
        return this.ryelRecords;
    }

    // kthen nje ryel mapper te ri per cilindo level
    private RyelMapper next(Integer level) {

        if (this.mappedRecords.isEmpty()) {
            return new RyelMapper(0, 0);
        }

        Integer maxIndex = -1;
        for (RyelMapper mapper : this.mappedRecords.keySet()) {
            if (mapper.getLevel().equals(level)) {
                RyelRecord record = mappedRecords.get(mapper);
                if (maxIndex < record.getIndex()) {
                    maxIndex = record.getIndex();
                }
            }
        }
        currentIndex = maxIndex + 1;
        return new RyelMapper(level, currentIndex);
    }

    private void setOperator(Integer id, RyelOperator operator) {
            for (RyelMapper ryelMapper : this.mappedRecords.keySet()) {
                RyelRecord record = this.mappedRecords.get(ryelMapper);
                if (record.getId().equals(id)) {
                    record.setOperator(operator);
                }
            }
    }

    private void writeAppendingChar(char ch) {
        List<RyelRecord> eligible = new ArrayList<>(eligibleStack);
        for (RyelRecord ryelRecord : eligible) {
            ryelRecord.addChar(new StringBuilder(), ch);
        }
    }

    private void validateAtomic(){
        for (RyelRecord record : ryelRecords) {
            if (record.getOperator() != null) {
                record.setAtomic(false);
            }
        }
    }

    public RyelRecord getRootRecord(String queryString, JSONObject jsonObject) {
        List<RyelRecord> records = getRecords(queryString);
        return buildRecordIteratively(jsonObject);
    }

    private RyelRecord buildRecordIteratively(JSONObject jsonObject) {
        // rekorded e mbushura
        List<RyelRecord> records = new ArrayList<>();
        queue = new LinkedList<>();

        for(int i = generatedIds.size()-1; i >= 0 ; i--){
            RyelRecord record = findRecordById(i);
            if(record!=null) {
                if(!record.isAtomic()) {

                    record.setRecords(getChildRecords(record));
                }
                else {
                    record.setRecords(new ArrayList<>());
                }
                queue.add(record);
                records.add(record);
            }
        }

        int size = queue.size();
        for(int i=0; i<size; i++){
            mongoRyels.add(convert(queue.remove(), jsonObject));
        }

        RyelRecord result = null;
        for(RyelRecord  record : records){
            if(record.getId().equals(0)){
                result = record;
            }
        }
        return  result;
    }


    public MongoRyel<Criteria,Query> getRootMongoRyel(JSONObject object){
        buildRecordIteratively(object);
        MongoRyel<Criteria,Query> result = null;
        System.out.println("MONGO RYELS  COUNT :  " + mongoRyels.size());
        for(MongoRyel<Criteria,Query>  ryel : mongoRyels){
            if(ryel.getRyelRecordId().equals(0)){
               return ryel;
            }
        }
        return  result;
    }

    private MongoRyel<Criteria,Query> convert(RyelRecord record, JSONObject jsonObject){
        MongoRyel<Criteria,Query> ryel = new MongoRyel<Criteria, Query>(record.getOperator());
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

    private List<Ryel<Criteria, Query>> getRyelList(RyelRecord ryelRecord){
        List<Ryel<Criteria, Query> >result = new ArrayList<>();
        for(RyelRecord record : getChildRecords(ryelRecord)){
            for(MongoRyel  mongoRyel : mongoRyels){
                if(mongoRyel.getRyelRecordId().equals(record.getId())){
                    result.add(mongoRyel);
                }
            }
        }
        return result;
    }

    //kthen bosh nese nuk gjen child records per ate record
    private List<RyelRecord> getChildRecords(RyelRecord ryelRecord){
        List<RyelRecord> records = new ArrayList<>();
        if(ryelRecord==null){
            return records;
        }
        for(RyelRecord record: ryelRecords){
            if(record.getId()!=0) {
                if (record.getParentId().equals(ryelRecord.getId())) {
                    records.add(record);
                }
            }
        }
        return records;
    }

    private RyelRecord findRecordById(Integer id){
        for(RyelRecord record: ryelRecords){
            if (record.getId().equals(id)){
                return record;
            }
        }
        return null;
    }

    private RyelRecord getRootRecordForMap(Map<RyelMapper, RyelRecord> map) {
        List<RyelRecord> records = new ArrayList<>(map.values());
        for (RyelRecord record : records) {
            if (record.getLevel().equals(0)) {
                return record;
            }
        }
        return null;
    }


    private boolean levelHasMultipleIndexes(Integer level) {
        Integer iterator = 0;
        for (RyelRecord record : ryelRecords) {
            if (record.getLevel().equals(level)) {
                iterator = iterator + 1;
            }
        }
        if (iterator > 1) {
            return true;
        }
        return false;
    }


    private RyelRecord buildRecordRecursively(RyelRecord ryelRecord, int levels, int index, int level) {
        Integer maxIndexForLevel = calculateMaxIndexForLevel(level);
        // kushti i ndalimit
        if (ryelRecord.getLevel().equals(levels) || ryelRecord.isAtomic() || (ryelRecord.getIndex().equals(maxIndexForLevel) && !ryelRecord.getLevel().equals(0))) {
            return ryelRecord;
        }
        //ryelRecord.setRecords(getRecordsForLevel(ryelRecord.getLevel()+1));
        return buildRecordRecursively(findRecordWithPosition(++level, ++index), levels, index, level).setRecords(getRecordsForLevel(level));
    }

    private int calculateLevels() {
        int result = 0;
        for (RyelRecord record : ryelRecords) {
            if (record.getLevel() > result) {
                result = record.getLevel();
            }
        }
        return result;
    }

    private Integer calculateMaxIndexForLevel(Integer level) {
        Integer result = 0;
        for (RyelRecord ryelRecord : ryelRecords) {
            if (ryelRecord.getLevel().equals(level)) {
                if (ryelRecord.getIndex() > result) {
                    result = ryelRecord.getIndex();
                }
            }
        }
        return result;
    }

    private RyelRecord findRecordWithPosition(Integer level, Integer index) {
        for (RyelRecord record : ryelRecords) {
            if (record.getLevel().equals(level) && record.getIndex().equals(index)) {
                return record;
            }
        }
        return null;
    }

    private List<RyelRecord> getRecordsForLevel(Integer level) {
        List<RyelRecord> result = new ArrayList<>();
        for (RyelRecord record : ryelRecords) {
            if (level.equals(record.getLevel())) {
                result.add(record);
            }
        }

        return result;
    }

    private List<RyelRecord> getRecordsForLevelForMap(Integer level, Map<RyelMapper, RyelRecord> map) {
        List<RyelRecord> list = new ArrayList<>(map.values());
        List<RyelRecord> result = new ArrayList<>();
        for (RyelRecord record : list) {
            if (record.getLevel().equals(level+1)) {
                result.add(record);
            }
        }
        return result;
    }

    // kthe null nese nuk gjen root record ( means the query is not valid)

    private RyelRecord findRootRecord() {
        for (RyelRecord record : ryelRecords) {
            if (record.getLevel() == 0) {
                return record;
            }
        }
        return null;
    }

}
