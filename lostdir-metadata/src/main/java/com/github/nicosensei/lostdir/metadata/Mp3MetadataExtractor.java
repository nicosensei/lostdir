package com.github.nicosensei.lostdir.metadata;

import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.mp3.Mp3Parser;

/**
 * Created by nicos on 11/5/2016.
 */
public final class Mp3MetadataExtractor extends AbstractMetadataExtractor {

    public static final String EXTENSION = "MP3";

    @Override
    public String getExtension() {
        return EXTENSION;
    }

    @Override
    protected Class<? extends AbstractParser> getParserClass() {
        return Mp3Parser.class;
    }

}
