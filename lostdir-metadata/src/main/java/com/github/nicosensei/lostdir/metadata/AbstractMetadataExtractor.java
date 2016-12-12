package com.github.nicosensei.lostdir.metadata;

import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import com.github.nicosensei.lostdir.scan.KeyValuePair;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nicos on 11/5/2016.
 */
public abstract class AbstractMetadataExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMetadataExtractor.class);

    private final AbstractParser parser;

    private final String extension;

    public AbstractMetadataExtractor() {
        this.extension = getExtension();
        try {
            this.parser = getParserClass().newInstance();
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to instantiate parser", e);
        }
    }

    public final void extractTo(final FileDiagnostic diag) {
        final BodyContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();
        final String filePath = diag.getPath();
        final ParseContext pcontext = new ParseContext();
        final FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(new File(filePath));
        } catch (final IOException e) {
            LOG.error("Failed to open file {}" + filePath, e);
            return;
        }
        try {
            parser.parse(inputStream, handler, metadata, pcontext);

            final ArrayList<KeyValuePair> md = new ArrayList<>(metadata.size());
            for (String key : metadata.names()) {
                md.add(new KeyValuePair(
                        key,
                        metadata.isMultiValued(key) ? metadata.getValues(key) : metadata.get(key)));
            }
            diag.getExtension(extension).setMetadata(md);
        } catch (final Throwable t) {
            LOG.error("Failed to parse file " + filePath, t);
            return;
        } finally {
            try {
                inputStream.close();
            } catch (final IOException e) {
                LOG.error("Failed to close {}", filePath);
            }
        }
    }

    public abstract String getExtension();

    protected abstract Class<? extends AbstractParser> getParserClass();

}
