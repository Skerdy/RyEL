package com.skerdy.Ryel.ryel.generators;

import org.json.simple.JSONObject;

public interface CriteriaGenerator<T,V> {

    T generateCriteria(V v, JSONObject payload);

}
