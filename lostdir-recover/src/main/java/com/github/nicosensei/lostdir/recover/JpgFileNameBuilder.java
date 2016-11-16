package com.github.nicosensei.lostdir.recover;

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

    private static final String IMG_PREFIX = "IMG";
    private static final String SEP = "_";
    private static final String DOT_JPG = ".jpg";

    private int seq = 0;

    @Override
    public String build(Map<String, Object> metadata) {
        final StringBuilder name = new StringBuilder(IMG_PREFIX);
        final String date = (String) metadata.get(JpegMeta.date.key);
        name.append(SEP).append(date != null ? date.replaceAll("\\D", "") : ++seq);
        return name.append(DOT_JPG).toString();
    }

}
