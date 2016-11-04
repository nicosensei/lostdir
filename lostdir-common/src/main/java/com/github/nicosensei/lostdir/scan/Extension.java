package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;

/**
 * Created by nicos on 11/3/2016.
 */
public final class Extension {

    private final String extension;

    private final String description;

    private final double score;

    @JsonCreator
    public Extension(
            @JsonProperty("extension") final String extension,
            @JsonProperty("score") final double score,
            @JsonProperty("description") final String description) {
        this.extension = extension;
        this.score = score;
        this.description = description;
    }

    public String getExtension() {
        return extension;
    }

    public double getScore() {
        return score;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return new StringBuilder(extension)
                .append(GlobalConstants.CHAR_SPACE).append(score)
                .append(GlobalConstants.CHAR_SPACE).append(description)
                .toString();
    }
}