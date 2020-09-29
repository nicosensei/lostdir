package com.github.nicosensei.lostdir.metadata;

import java.util.Map;

/**
 * Created by nicos on 11/11/2016.
 */
public final class Mp4FileNameBuilder implements FileNameBuilder {

    private enum Key {
        date("date"),
        modified("modified"),
        width("Image Width"),
        height("Image Height"),
        make("Make"),
        fileSize("File Size");

        private final String key;

        Key(String key) {
            this.key = key;
        }
    }

    private static final String VID_PREFIX = "VID_";
    private static final String DOT = ".";

    private final String ext;

    public Mp4FileNameBuilder(String ext) {
        this.ext = ext;
    }

    @Override
    public String build(final String originalName, final Map<String, Object> metadata) {
        if (metadata.keySet().contains(Key.date.key)) {
            final String date = (String) metadata.get(Key.date.key);
            return new StringBuilder(VID_PREFIX).append(date.replaceAll("\\D", ""))
                    .append(DOT).append(ext).toString();
        }
        return new StringBuilder(VID_PREFIX).append(originalName).toString();
    }

}
