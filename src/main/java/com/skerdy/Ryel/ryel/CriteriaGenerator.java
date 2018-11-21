package com.skerdy.Ryel.ryel;

import org.json.simple.JSONObject;

public interface CriteriaGenerator<T,V> {

    T generateCriteria(V v, JSONObject payload);

}
