package com.github.nicosensei.lostdir.scan;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        final StringBuilder sb = new StringBuilder(path);
        for (Extension e : extensions) {
            sb.append(GlobalConstants.NEWLINE).append(e.getScore())
                    .append(GlobalConstants.CHAR_SPACE).append(e.getExtension())
                    .append(GlobalConstants.CHAR_SPACE).append(e.getDescription());
        }
        return sb.toString();
    }

    public ArrayList<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(ArrayList<Extension> extensions) {
        this.extensions = extensions;
    }

    public void addOrMergeExtension(final Extension e) {
        final Extension ext = getExtension(e.getExtension());
        if (ext != null) {
            ext.setScore(new BigDecimal(ext.getScore())
                    .add(new BigDecimal(e.getScore()))
                    .setScale(1, BigDecimal.ROUND_HALF_DOWN)
                    .doubleValue());
            return;
        }
        this.extensions.add(e);
    }

    public Extension getExtension(final String ext) {
        for (Extension e : extensions) {
            if (e.getExtension().equals(ext)) {
                return e;
            }
        }
        return null;
    }

    public void apply(final ScanPolicy policy) {
        final ArrayList<Extension> keep = new ArrayList<>(extensions.size());
        for (Extension ext : extensions) {
            if (ScanPolicy.LOOK_FOR_EXT.contains(ext.getExtension())
                    && ext.getScore() >= policy.getKeepThreshold()) {
                keep.add(ext);
            }
        }
        this.extensions.clear();
        this.extensions.addAll(keep);
    }

    public final Map<String, Object> metadataAsMap(final String ext) {
        final Extension extension = getExtension(ext);
        if (extension == null) {
            throw new IllegalArgumentException("No extension " + ext + " for " + path);
        }
        return metadataAsMap(extension);
    }

    public final Map<String, Object> metadataAsMap(final Extension extension) {
        final ArrayList<KeyValuePair> metadata = extension.getMetadata();
        if (metadata == null) {
            return Collections.emptyMap();
        }
        final HashMap<String, Object> metaMap = new HashMap<>(metadata.size());
        for (KeyValuePair data : metadata) {
            metaMap.put(data.getKey(), data.getValue());
        }
        return metaMap;
    }

}
