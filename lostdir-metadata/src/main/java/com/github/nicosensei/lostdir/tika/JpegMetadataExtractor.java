package com.github.nicosensei.lostdir.tika;

import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.jpeg.JpegParser;

/**
 * Created by nicos on 11/5/2016.
 */
public final class JpegMetadataExtractor extends AbstractMetadataExtractor {

    public static final String EXTENSION = "JPG";

    @Override
    protected String getExtension() {
        return EXTENSION;
    }

    @Override
    protected Class<? extends AbstractParser> getParserClass() {
        return JpegParser.class;
    }

}
