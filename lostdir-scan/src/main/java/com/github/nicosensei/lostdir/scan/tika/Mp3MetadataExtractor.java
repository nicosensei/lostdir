package com.github.nicosensei.lostdir.scan.tika;

import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.parser.mp3.Mp3Parser;

/**
 * Created by nicos on 11/5/2016.
 */
public final class Mp3MetadataExtractor extends AbstractMetadataExtractor {

    public static final String EXTENSION = "MP3";

    @Override
    protected String getExtension() {
        return EXTENSION;
    }

    @Override
    protected Class<? extends AbstractParser> getParserClass() {
        return Mp3Parser.class;
    }

}
