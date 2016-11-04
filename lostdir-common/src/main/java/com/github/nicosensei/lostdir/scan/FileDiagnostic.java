package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;

import java.io.File;

/**
 * Created by nicos on 11/3/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FileDiagnostic {

    public static final String INDEX = "trid";
    public static final String TYPE = "diag";

    private final String path;

    private Extension extension;

    @JsonCreator
    public FileDiagnostic(@JsonProperty("path") final String path) {
        this.path = path;
    }

    public FileDiagnostic(final File file) {
        this(file.getAbsolutePath());
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new StringBuilder(path).append(": [")
            .append(GlobalConstants.CHAR_SPACE).append(extension.toString())
                .toString();
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
