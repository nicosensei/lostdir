package com.github.nicosensei.lostdir.helpers;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

/**
 * Created by b010kuo on 21/10/2016.
 */
public final class StringSubstitutor {

    public static final String DEFAULT_VAR_MARKER = "@";

    private final String marker;

    public StringSubstitutor() {
        this(DEFAULT_VAR_MARKER);
    }

    public StringSubstitutor(final String varMarker) {
        this.marker = varMarker;
    }

    public StringBuilder replaceIn(StringBuilder template, final Map<String, String> varMap) {
        final StrSubstitutor subst = new StrSubstitutor(varMap)
                .setVariablePrefix(marker)
                .setVariableSuffix(marker);
        subst.replaceIn(template);
        return template;
    }

    public String replace(final String template, final Map<String, String> varMap) {
        final StrSubstitutor subst = new StrSubstitutor(varMap)
                .setVariablePrefix(marker)
                .setVariableSuffix(marker);
        return subst.replace(template);
    }
}
