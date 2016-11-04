package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;

import java.io.File;

/**
 * Created by nicos on 11/3/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FileDiagnostic {

    private final String path;

    private Extension extension;

    public FileDiagnostic(final File file) {
        this.path = file.getAbsolutePath();
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
