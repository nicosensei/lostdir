package com.github.nicosensei.lostdir.rename;

import com.github.nicosensei.lostdir.metadata.Mp3FileNameBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by nicos on 12/12/2016.
 */
public final class HasTitleRule extends PickRule {

    private final HashMap<String, Integer> minRes = new HashMap<>(2);

    @Override
    protected boolean evaluateMetaDataRecord(String key, Object value) {
        return value instanceof String && !((String) value).isEmpty();
    }

    @Override
    protected Set<String> getRequiredKeys() {
        return Arrays.asList(Mp3FileNameBuilder.Key.title.getKey()).stream().collect(Collectors.toSet());
    }

}
