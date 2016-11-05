package com.github.nicosensei.lostdir.scan.tika;

import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.parser.mp4.MP4Parser;

/**
 * Created by nicos on 11/5/2016.
 */
public abstract class MP4MetadataExtractor extends AbstractMetadataExtractor {

    @Override
    protected Class<? extends AbstractParser> getParserClass() {
        return MP4Parser.class;
    }

}
