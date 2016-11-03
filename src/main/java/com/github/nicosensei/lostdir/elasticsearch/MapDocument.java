package com.github.nicosensei.lostdir.elasticsearch;

import java.util.Map;

/**
 * @author Nicolas Giraud
 */
public final class MapDocument {

    private final String type;

    private final Map<String, Object> source;

    private final String id;

    public MapDocument(final String type, final String id, final Map<String, Object> source) {
        this.type = type;
        this.id = id;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public Long getVersion() {
        return null;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getSource() {
        return source;
    }
}
