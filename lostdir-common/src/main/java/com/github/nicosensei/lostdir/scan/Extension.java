package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nicos on 11/3/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Extension {

    private final String extension;

    private final String description;

    private double score;

    private ArrayList<KeyValuePair> metadata = null;

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

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<KeyValuePair> getMetadata() {
        return metadata;
    }

    public void setMetadata(final ArrayList<KeyValuePair> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return new StringBuilder(extension)
                .append(GlobalConstants.CHAR_SPACE).append(score)
                .append(GlobalConstants.CHAR_SPACE).append(description)
                .append(GlobalConstants.NEWLINE).append(metadata.toString())
                .toString();
    }
}
