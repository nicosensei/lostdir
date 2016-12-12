package com.github.nicosensei.lostdir.rename;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by nicos on 12/12/2016.
 */
public final class JpgMinResolutionRule extends PickRule {

    private final HashMap<String, Integer> minRes = new HashMap<>(2);

    public JpgMinResolutionRule(
            final String widthKey,
            final int minWidth,
            final String heightKey,
            final int minHeight) {
        minRes.put(widthKey, minWidth);
        minRes.put(heightKey, minHeight);
    }

    @Override
    protected boolean evaluateMetaDataRecord(String key, Object value) {
        final Integer dimension = minRes.get(key);
        if (dimension == null) {
            return false;
        }
        final String numericPrefix = ((String) value).split("\\s+")[0];
        return Integer.parseInt(numericPrefix) >= dimension.intValue();
    }

    @Override
    protected Set<String> getRequiredKeys() {
        return minRes.keySet();
    }
}
