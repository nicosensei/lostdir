package com.github.nicosensei.lostdir.metadata;

/**
 * Created by nicos on 11/5/2016.
 */
public final class M4rMetadataExtractor extends MP4MetadataExtractor {

    public static final String EXTENSION = "M4R";

    @Override
    public String getExtension() {
        return EXTENSION;
    }

}
