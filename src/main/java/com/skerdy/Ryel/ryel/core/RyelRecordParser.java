package com.skerdy.Ryel.ryel.core;

import com.skerdy.Ryel.ryel.core.Ryel;
import com.skerdy.Ryel.ryel.core.RyelMapper;
import com.skerdy.Ryel.ryel.core.RyelOperator;
import com.skerdy.Ryel.ryel.core.RyelRecord;
import com.skerdy.Ryel.ryel.mongodb.MongoRyel;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

public abstract class RyelRecordParser<E extends Ryel> {

    private final char OPENING_BRACKET = '{';
    private final char CLOSING_BRACKET = '}';

    private char currentChar;
    private Integer currentLevel;
    private Integer currentIndex;

    private RyelRecord currentRecord;

    private Map<RyelMapper, RyelRecord> mappedRecords;

    private List<RyelRecord> halfRyelRecords;

    private Integer incrementalId;

    private List<Integer> generatedIds;

    private Stack<RyelRecord> eligibleStack;

    protected Queue<RyelRecord> queue;

    private RyelRecord rootRecord;

    private List<RyelRecord> fullRyelRecords;

    protected E root;

    protected List<E> ryels;

    public RyelRecordParser() {
        initParser();
    }

    // abstract methods
    protected abstract E convert(RyelRecord record, JSONObject jsonObject);

    protected abstract List<E> getChildrensForRecord(RyelRecord ryelRecord);

    public  void calculate(String query, JSONObject jsonObject){
        parse(query);
        int queueSize = queue.size();
        for (int i = 0; i < queueSize; i++) {
            ryels.add(convert(queue.remove(), jsonObject));
        }
        findRootRyel(query,jsonObject);
    }


    public List<RyelRecord> parse(String queryString) {
        resetParser();
        for (int i = 0; i < queryString.length(); i++) {
            currentChar = queryString.charAt(i);
            if (currentChar == OPENING_BRACKET) {

                ++currentLevel;
                RyelMapper currentMapper = next(currentLevel);
                incrementalId = incrementalId + 1;
                generatedIds.add(incrementalId);
                currentRecord = new RyelRecord(currentLevel, currentMapper.getIndex(), incrementalId);
                eligibleStack.push(currentRecord);
                mappedRecords.put(currentMapper, currentRecord);
                writeAppendingChar(currentChar);

            } else if (currentChar == CLOSING_BRACKET) {
                --currentLevel;
                writeAppendingChar(currentChar);
                RyelRecord popped = eligibleStack.pop();
                if (eligibleStack.size() > 0)
                    popped.setParentId(eligibleStack.peek().getId());

            } else if (currentChar == RyelOperator.AND.getOperator()) {
                setOperator(eligibleStack.peek().getId(), RyelOperator.AND);
            } else if (currentChar == RyelOperator.OR.getOperator()) {
                setOperator(eligibleStack.peek().getId(), RyelOperator.OR);
            } else {
                writeAppendingChar(currentChar);
            }
        }

        this.halfRyelRecords = new ArrayList<>(mappedRecords.values());
        validateAtomic();
        // nderton full ryel recordet dhe kthen rootin gjithashtu mbush edhe queuen me recordet full processed
        this.rootRecord = buildRecordIteratively();
        return this.halfRyelRecords;
    }

    private void initParser(){
        mappedRecords = new HashMap<>();
        generatedIds = new ArrayList<>();
        eligibleStack = new Stack<>();
        ryels = new ArrayList<>();
        currentLevel = -1;
        currentIndex = 0;
        incrementalId = -1;
    }

    private void resetParser(){
        initParser();
    }


    private RyelMapper next(Integer level) {
        Integer maxIndex = -1;
        if (this.mappedRecords.isEmpty()) {
            return new RyelMapper(0, 0);
        }
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

    private void writeAppendingChar(char ch) {
        List<RyelRecord> eligible = new ArrayList<>(eligibleStack);
        for (RyelRecord ryelRecord : eligible) {
            ryelRecord.addChar(new StringBuilder(), ch);
        }
    }

    private void validateAtomic() {
        for (RyelRecord record : halfRyelRecords) {
            if (record.getOperator() != null) {
                record.setAtomic(false);
            }
        }
    }

    private void setOperator(Integer id, RyelOperator operator) {
        for (RyelMapper ryelMapper : this.mappedRecords.keySet()) {
            RyelRecord record = this.mappedRecords.get(ryelMapper);
            if (record.getId().equals(id)) {
                record.setOperator(operator);
            }
        }
    }

    private RyelRecord buildRecordIteratively() {
        // rekorded e mbushura
        fullRyelRecords = new ArrayList<>();
        queue = new LinkedList<>();

        for (int i = generatedIds.size() - 1; i >= 0; i--) {
            RyelRecord record = findRecordById(i);
            if (record != null) {
                if (!record.isAtomic()) {
                    record.setRecords(getChildrenRecords(record));
                } else {
                    record.setRecords(new ArrayList<>());
                }
                queue.add(record);
                fullRyelRecords.add(record);
            }
        }

        RyelRecord result = null;
        for (RyelRecord record : fullRyelRecords) {
            if (record.getId().equals(0)) {
                result = record;
            }
        }
        return result;
    }

    //kthen bosh nese nuk gjen child records per ate record
    protected List<RyelRecord> getChildrenRecords(RyelRecord ryelRecord) {
        List<RyelRecord> records = new ArrayList<>();
        if (ryelRecord == null) {
            return records;
        }
        for (RyelRecord record : halfRyelRecords) {
            if (record.getId() != 0) {
                if (record.getParentId().equals(ryelRecord.getId())) {
                    records.add(record);
                }
            }
        }
        return records;
    }

    private RyelRecord findRecordById(Integer id) {
        for (RyelRecord record : halfRyelRecords) {
            if (record.getId().equals(id)) {
                return record;
            }
        }
        return null;
     }

    public E findRootRyel(String query, JSONObject jsonObject){
        E result = null;
        for(E ryel : ryels){
            if(ryel.getRyelRecordId().equals(0)){
                root = ryel;
                return ryel;
            }
        }
        return result;
    }

    public Queue<RyelRecord> getQueue() {
        return queue;
    }

    public RyelRecord getRootRecord() {
        return rootRecord;
    }

    public E getRoot() {
        return root;
    }

    public void setRoot(E root) {
        this.root = root;
    }

    public List<E> getRyels() {
        return ryels;
    }

    public void setRyels(List<E> ryels) {
        this.ryels = ryels;
    }
}
