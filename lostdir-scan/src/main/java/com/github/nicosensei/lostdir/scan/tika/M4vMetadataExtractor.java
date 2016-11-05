package com.github.nicosensei.lostdir.scan.tika;

import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.jpeg.JpegParser;

/**
 * Created by nicos on 11/5/2016.
 */
public final class M4vMetadataExtractor extends MP4MetadataExtractor {

    public static final String EXTENSION = "M4V";

    @Override
    protected String getExtension() {
        return EXTENSION;
    }

}
