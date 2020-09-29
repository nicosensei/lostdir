package com.github.nicosensei.lostdir.metadata;

import java.util.Map;

/**
 * Created by nicos on 11/11/2016.
 */
public final class JpgFileNameBuilder implements FileNameBuilder {

    private enum JpegMeta {
        date("date"),
        modified("modified"),
        width("Image Width"),
        height("Image Height"),
        make("Make"),
        fileSize("File Size");

        private final String key;

        JpegMeta(String key) {
            this.key = key;
        }
    }

    private static final String IMG_PREFIX = "IMG_";
    private static final String DOT_JPG = ".jpg";

    @Override
    public String build(final String originalName, Map<String, Object> metadata) {
        if (metadata.keySet().contains(JpegMeta.date.key)) {
            final String date = (String) metadata.get(JpegMeta.date.key);
            return new StringBuilder(IMG_PREFIX).append(date.replaceAll("\\D", ""))
                    .append(DOT_JPG).toString();
        }
        return new StringBuilder(IMG_PREFIX).append(originalName).toString();
    }

}
