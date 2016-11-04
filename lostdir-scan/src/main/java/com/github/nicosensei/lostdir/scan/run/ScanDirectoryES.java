package com.github.nicosensei.lostdir.scan.run;

import com.github.nicosensei.lostdir.elasticsearch.GenericIndex;
import com.github.nicosensei.lostdir.elasticsearch.LocalNode;
import com.github.nicosensei.lostdir.elasticsearch.MapDocument;
import com.github.nicosensei.lostdir.helpers.GenericJsonObjectMapper;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;
import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import com.github.nicosensei.lostdir.scan.Trid;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by nicos on 11/3/2016.
 */
public final class ScanDirectoryES {

    private static final Logger LOG = LoggerFactory.getLogger(ScanDirectoryES.class);

    private static final String ES_CLUSTER = "lostdir-es";
    private static final int ES_TCP_PORT = 10300;

    public static final String INDEX = "trid";
    public static final String TYPE = "diag";

    public static void main(final String[] args) throws IOException {

        if (args.length != 1) {
            LOG.error("Expected folder to scan as argument");
            System.exit(0);
        }

        final long start = System.currentTimeMillis();

        final LocalNode elastic = new LocalNode(
                GlobalConstants.CURRENT_DIR + File.separator + "elasticsearch",
                ES_CLUSTER,
                ES_TCP_PORT,
                false);

        final GenericIndex genIndex = new GenericIndex(elastic.client());
        genIndex.createIndex(INDEX);
        genIndex.registerType(INDEX, TYPE);

        final GenericJsonObjectMapper<FileDiagnostic> mapper = new GenericJsonObjectMapper<>();

        try {
            final Trid trid = new Trid();
            final File dir = new File(args[0]);
            assert dir.isDirectory();
            assert dir.canRead();

            long count = 0;
            long recoverableCount = 0;
            for (File f : dir.listFiles()) {
                try {
                    count++;
                    LOG.info("Testing {}", f.getAbsolutePath());
                    FileDiagnostic diag = trid.testFile(f);
                    if (diag == null) {
                        diag = new FileDiagnostic(f.getAbsolutePath());
                    }
                    if (diag.getExtension() != null) {
                        recoverableCount++;
                        LOG.info("[OK] {} - {}", diag.getPath(), diag.getExtension().toString());
                    } else {
                        LOG.info("[KO] {}", diag.getPath());
                    }

                    genIndex.indexDocument(
                            INDEX,
                            new MapDocument(TYPE, diag.getPath(), mapper.asMap(diag)));
                } catch (final IOException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
            LOG.info("Scanned {} files: {} recoverable ({})",
                    count, recoverableCount, TimeFormatter.formatDurationSince(start));
        } finally {
            try {
                genIndex.close();
                elastic.destroy();
            } catch (final Exception e) {
                throw new ElasticsearchException(e.getMessage(), e);
            }
        }
    }
}
