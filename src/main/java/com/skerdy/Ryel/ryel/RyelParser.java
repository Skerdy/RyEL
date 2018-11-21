package com.skerdy.Ryel.ryel;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RyelParser {

    private final char OPENING_BRACKET = '{';
    private final char CLOSING_BRACKET = '}';

    private char currentChar;
    private Integer currentLevel;
    private Integer currentIndex;

    private Map<Integer, Integer> currentLevelIndex;

    private RyelRecord currentRecord;

    // level -> index -> record
    private Map<Integer, Map<Integer, RyelRecord>> createdRecords;

    private Map<RyelMapper, RyelRecord> recordMap;

    private Map<RyelMapper, RyelRecord> eligibleRecordsToWrite;

    private List<RyelRecord> ryelRecords;


    public RyelParser() {
        ryelRecords = new ArrayList<>();
    }


    public List<RyelRecord> getRecords(String queryString) {
        createdRecords = new HashMap<>();
        recordMap = new HashMap<>();
        eligibleRecordsToWrite = new HashMap<>();
        currentLevelIndex = new HashMap<>();
        currentLevel = -1;
        currentIndex = 0;
        List<RyelRecord> result = new ArrayList<>();

        for (int i = 0; i < queryString.length(); i++) {
            currentChar = queryString.charAt(i);
            if (currentChar == OPENING_BRACKET) {
                ++currentLevel;

                RyelMapper currentMapper = next(currentLevel, currentIndex);

                currentRecord = new RyelRecord(currentLevel, currentMapper.getIndex());

                recordMap.put(currentMapper, currentRecord);

                eligibleRecordsToWrite.put(currentMapper, currentRecord);

                validateEligibleRecords(currentLevel);
                writeAppendingChar(currentChar);

            } else if (currentChar == CLOSING_BRACKET) {
                --currentLevel;
                writeAppendingChar(currentChar);
                validateEligibleRecords(currentLevel);

            } else if (currentChar == RyelOperator.AND.getOperator()) {
                setOperator(currentLevel, RyelOperator.AND);
            } else if (currentChar == RyelOperator.OR.getOperator()) {
                setOperator(currentLevel, RyelOperator.OR);
            } else {
                writeAppendingChar(currentChar);
            }
        }

        this.ryelRecords = new ArrayList<>(recordMap.values());
        return this.ryelRecords;
    }

    // kthen nje ryel mapper te ri per cilindo level
    private RyelMapper next(Integer level, Integer index) {
        if (this.recordMap.isEmpty()) {
            currentLevelIndex.put(0, 0);
            return new RyelMapper(0, 0);
        }
            for (RyelMapper mapper : this.recordMap.keySet()) {
                if (mapper.getLevel().equals(level) && mapper.getIndex().equals(index)) {
                    currentIndex = index + 1;
                    currentLevelIndex.put(level, currentIndex);
                    return new RyelMapper(level, currentIndex);
                }
            }
        currentLevelIndex.put(level, 0);
        return new RyelMapper(level, 0);
    }

    private void validateEligibleRecords(Integer level) {
        eligibleRecordsToWrite.clear();
        if (level.equals(0)) {
            for (RyelMapper ryelMapper : this.recordMap.keySet()) {
                if (ryelMapper.getLevel().equals(level) && currentLevelIndex.get(level).equals(ryelMapper.getIndex())) {
                    eligibleRecordsToWrite.put(ryelMapper, recordMap.get(ryelMapper));
                }
            }
        } else {
            for (int i = 0; i <= level; i++) {
                for (RyelMapper ryelMapper : this.recordMap.keySet()) {
                    if (ryelMapper.getLevel().equals(i) && currentLevelIndex.get(i).equals(ryelMapper.getIndex())) {
                        eligibleRecordsToWrite.put(ryelMapper, recordMap.get(ryelMapper));
                    }
                }
            }
        }
    }

    private void setOperator(Integer level, RyelOperator operator) {
        for (int i = 0; i <= level; i++) {
            for (RyelMapper ryelMapper : this.recordMap.keySet()) {
                if (ryelMapper.getLevel().equals(i) && currentLevelIndex.get(i).equals(ryelMapper.getIndex())) {
                    recordMap.get(ryelMapper).setOperator(operator);
                }
            }
        }
       // findRecordWithPosition(level,currentLevelIndex.get(level)).setOperator(operator);
    }

    private void writeAppendingChar(char ch) {
        for (RyelMapper mapper : this.eligibleRecordsToWrite.keySet()) {
            this.eligibleRecordsToWrite.get(mapper).addChar(new StringBuilder(), ch);
        }
    }

    public RyelRecord getRootRecord(String queryString) {
        List<RyelRecord> records = getRecords(queryString);
        for (RyelRecord record : records) {
            if (record.getOperator() != null) {
                record.setAtomic(false);
            }
        }
         this.ryelRecords = records;
        // return buildRecordRecursively(findRootRecord(), calculateLevels(), 0, 0);

        return buildRecordIteratively();
    }

/*
    public RyelRecord buildRecord(RyelRecord ryelRecord, List<RyelRecord> records, Integer level, Integer index) {
        if (ryelRecord.isAtomic()||ryelRecord==null) {
            if(ryelRecord !=null)
            ryelRecord.setRecords(records);
            else{
                ++level;
            }
            return ryelRecord;
        }

      if(records==null || records.isEmpty()){
          return ryelRecord;
      }
      else if(!records.isEmpty()){
          for(RyelRecord record : records){
              return record;
          }
      }
         return buildRecord(ryelRecord, getRecordsForLevel(ryelRecord.getLevel()+1),level,index);
    }*/

   private RyelRecord buildRecordIteratively(){
       RyelRecord root = findRootRecord();
       int maxLevel = calculateLevels();
       Map<RyelMapper, RyelRecord> records = new HashMap<>();

       for(int i=maxLevel; i>0;i--){
           for(int j=0;j<=calculateMaxIndexForLevel(i);j++){
               if(!findRecordWithPosition(i,j).isAtomic()){
                   records.put(new RyelMapper(i,j),findRecordWithPosition(i,j).setRecords(getRecordsForLevelForMap(i, records)));
               }
               else {
                   records.put(new RyelMapper(i,j), findRecordWithPosition(i,j));
               }
           }
       }

       root.setRecords(getRecordsForLevel(1));
       return root;
   }



    private RyelRecord buildRecordRecursively(RyelRecord ryelRecord, int levels, int index, int level){
        Integer maxIndexForLevel = calculateMaxIndexForLevel(level);
        // kushti i ndalimit
        if(ryelRecord.getLevel().equals(levels)||ryelRecord.isAtomic()|| (ryelRecord.getIndex().equals(maxIndexForLevel )&&!ryelRecord.getLevel().equals(0))){
            return ryelRecord;
        }
        //ryelRecord.setRecords(getRecordsForLevel(ryelRecord.getLevel()+1));
        return buildRecordRecursively(findRecordWithPosition(++level, ++index), levels, index, level).setRecords(getRecordsForLevel(level));
    }

    private int calculateLevels(){
        int result = 0;
        for(RyelRecord record : ryelRecords){
            if(record.getLevel()>result){
                result = record.getLevel();
            }
        }
        return result;
    }

    private int calculateMaxIndexForLevel(Integer level){
        int result = 0;
        for(Integer levelInt : currentLevelIndex.keySet()){
            if(levelInt.equals(level)){
                result = currentLevelIndex.get(levelInt);
            }
        }
        return  result;
    }

    private RyelRecord findRecordWithPosition(Integer level, Integer index){
        for(RyelRecord record : ryelRecords){
            if(record.getLevel().equals(level) && record.getIndex().equals(index)){
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

    private List<RyelRecord> getRecordsForLevelForMap(Integer level, Map<RyelMapper, RyelRecord> map){
        List<RyelRecord> list = new ArrayList<>(map.values());
        List<RyelRecord> result = new ArrayList<>();
        for (RyelRecord record : list) {
            if (level.equals(record.getLevel())) {
                result.add(record);
            }
        }
        return result;
    }

    private RyelRecord findRootRecord(){
        for(RyelRecord record : ryelRecords){
            if(record.getLevel() == 0){
                return record;
            }
        }
        return  null;
    }

}
