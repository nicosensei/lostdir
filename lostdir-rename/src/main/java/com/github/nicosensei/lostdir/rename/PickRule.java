package com.github.nicosensei.lostdir.rename;

import java.util.Map;
import java.util.Set;

/**
 * Created by nicos on 12/12/2016.
 */
public abstract class PickRule {

    public final boolean pickFile(final Map<String, Object> metadata) {
        for (final String key : getRequiredKeys()) {
            final Object value = metadata.get(key);
            final boolean ruleMatched = value == null ? false : evaluateMetaDataRecord(key, value);
            if (!ruleMatched) {
                return false;
            }
        }
        return true;
    }

    protected abstract Set<String> getRequiredKeys();

    protected abstract boolean evaluateMetaDataRecord(final String key, final Object value);

}
