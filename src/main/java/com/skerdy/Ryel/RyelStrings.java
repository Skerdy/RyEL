package com.skerdy.Ryel;

public class RyelStrings {


    public static String oneLevel(){
        return "{\"key\":\"value\"}";
    }

    public static String oneLevelPayLoad(){
        return "{\"key\":\"payload(\"value\")\"}";
    }

    public static String oneLevelOrOperation(){
        return "{{\"key\":\"value\"}|{\"key1\":\"value1\"}|{\"key2\":\"value2\"}|{\"key3\":\"value3\"}}";
    }

    public static String oneLevelAndOperation(){
        return "{{{\"key\":\"value\"}|{\"key1\":\"value1\"}}&{{\"key2\":\"value2\"}|{\"key3\":\"value3\"}}}";
    }

    public static String twoLevels(){
        return "{{{\"key\":\"value\"}|{\"key1\":\"value1\"}}&{\"key2\":\"value2\"}}";
    }

    public static String oneLevelTwoOrOperation(){
        return "{{\"key\":\"value\"}|{\"key1\":\"value1\"}|{\"key2\":\"value2\"}|{\"key3\":\"value3\"}}";
    }

    public static String twoLevelsTwoOperators(){
        return "{{{\"key\":\"payload(\"key\")\"}|{\"key1\":\"value1\"}}&{{\"key2\":\"value2\"}|{\"key3\":\"value3\"}}";
    }

}
