package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicos on 11/5/2016.
 */
public class KeyValuePair {

    private final String key;

    private final Object value;

    @JsonCreator
    public KeyValuePair(
            @JsonProperty("key") final String key,
            @JsonProperty("value") final Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
