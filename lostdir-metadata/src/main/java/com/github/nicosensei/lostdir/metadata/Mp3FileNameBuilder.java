package com.github.nicosensei.lostdir.metadata;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by nicos on 11/11/2016.
 */
public final class Mp3FileNameBuilder implements FileNameBuilder {

    public enum Key {
        title("title"),
        author("meta:author");

        private final String key;

        Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private static final String PREFIX = "MP3_";
    private static final String SEP = " - ";
    private static final String EXT = ".mp3";

    private int seq = 0;

    private final String ext;

    public Mp3FileNameBuilder(String ext) {
        this.ext = ext;
    }

    @Override
    public String build(final String originalName, Map<String, Object> metadata) {
        final List<String> meta = Arrays.asList(
                (String) metadata.get(Key.author.key),
                (String) metadata.get(Key.title.key)
        );
        return meta.isEmpty()
                ? PREFIX + originalName
                : meta.stream().collect(Collectors.joining(SEP)) + EXT;
    }

}
