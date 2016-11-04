package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nicos on 11/3/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class FileDiagnostic {

    public static final String INDEX = "trid";
    public static final String TYPE = "diag";

    private final String path;
    private final long size;

    private ArrayList<Extension> extensions = new ArrayList<>(3);

    @JsonCreator
    public FileDiagnostic(
            @JsonProperty("path") final String path,
            @JsonProperty("size") final long size) {
        this.path = path;
        this.size = size;
    }

    public FileDiagnostic(final File file) {
        this(file.getAbsolutePath(), file.length());
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(path).append(": [");
        for (Extension e : extensions) {
            sb.append(GlobalConstants.NEWLINE).append(e.getScore())
                    .append(GlobalConstants.CHAR_SPACE).append(e.getExtension())
                    .append(GlobalConstants.CHAR_SPACE).append(e.getDescription());
        }
        return sb.append("]").toString();
    }

    public ArrayList<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(ArrayList<Extension> extensions) {
        this.extensions = extensions;
    }

    public void addOrMergeExtension(final Extension e) {
        for (Extension ext : extensions) {
            if (e.getExtension().equals(ext.getExtension())) {
                ext.setScore(ext.getScore() + e.getScore());
                return;
            }
        }
        this.extensions.add(e);
    }

}
