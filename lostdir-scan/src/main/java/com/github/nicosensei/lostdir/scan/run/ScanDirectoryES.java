package com.github.nicosensei.lostdir.scan.run;

import com.github.nicosensei.lostdir.elasticsearch.GenericIndexer;
import com.github.nicosensei.lostdir.elasticsearch.LocalNode;
import com.github.nicosensei.lostdir.elasticsearch.LocalNodeDefaults;
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

    public static void main(final String[] args) throws IOException {

        if (args.length != 1) {
            LOG.error("Expected folder to scan as argument");
            System.exit(0);
        }

        final long start = System.currentTimeMillis();

        final LocalNode elastic = new LocalNode(
                GlobalConstants.CURRENT_DIR + File.separator + "elasticsearch",
                LocalNodeDefaults.CLUSTER_NAME,
                LocalNodeDefaults.TCP_PORT,
                true);

        final GenericIndexer genIndex = new GenericIndexer(elastic.client());
        genIndex.createIndex(FileDiagnostic.INDEX);
        genIndex.registerType(FileDiagnostic.INDEX, FileDiagnostic.TYPE);

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
                    FileDiagnostic diag = trid.testFile(f);
                    if (diag == null) {
                        diag = new FileDiagnostic(f);
                    }
                    if (!diag.getExtensions().isEmpty()) {
                        recoverableCount++;
                        LOG.info("[OK] {} - {}", diag.toString());
                    } else {
                        LOG.info("[KO] {}", diag.getPath());
                    }

                    genIndex.indexDocument(
                            FileDiagnostic.INDEX,
                            new MapDocument(FileDiagnostic.TYPE, diag.getPath(), mapper.asMap(diag)));
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
