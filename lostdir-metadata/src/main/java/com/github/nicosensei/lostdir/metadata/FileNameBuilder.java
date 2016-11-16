package com.github.nicosensei.lostdir.metadata;

import java.util.Map;

/**
 * Created by nicos on 11/11/2016.
 */
public interface FileNameBuilder {

    String build(final Map<String, Object> metadata);

}
