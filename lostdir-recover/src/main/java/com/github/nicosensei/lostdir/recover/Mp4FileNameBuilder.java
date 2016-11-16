package com.github.nicosensei.lostdir.recover;

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

    private static final String VID_PREFIX = "VID";
    private static final String SEP = "_";
    private static final String DOT_MP4 = ".mp4";

    private int seq = 0;

    @Override
    public String build(Map<String, Object> metadata) {
        final StringBuilder name = new StringBuilder(VID_PREFIX);
        final String date = (String) metadata.get(Key.date.key);
        name.append(SEP).append(date != null ? date.replaceAll("\\D", "") : ++seq);
        return name.append(DOT_MP4).toString();
    }

}
