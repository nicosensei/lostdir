package com.github.nicosensei.lostdir.trid.run;

import com.github.nicosensei.lostdir.elasticsearch.LocalNode;
import com.github.nicosensei.lostdir.helpers.GlobalConstants;
import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import com.github.nicosensei.lostdir.trid.FileDiagnostic;
import com.github.nicosensei.lostdir.trid.Trid;
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

    public static void main(final String[] args) {

        if (args.length != 1) {
            LOG.error("Expected folder to scan as argument");
            System.exit(0);
        }

//        final LocalNode elastic = new LocalNode(
//                GlobalConstants.CURRENT_DIR,
//                ,
//                )

        final long start = System.currentTimeMillis();
        final Trid trid = new Trid();
        final File dir = new File(args[0]);
        assert dir.isDirectory();
        assert dir.canRead();

        long count = 0;
        long recoverableCount = 0;
        long failedCount = 0;
        for (File f : dir.listFiles()) {
            try {
                count++;
                LOG.info("Testing {}", f.getAbsolutePath());
                final FileDiagnostic diag = trid.testFile(f);
                if (diag.size() > 0) {
                    recoverableCount++;
                    LOG.info("[OK] {} - {}", diag.getPath(), diag.get(0).toString());
                } else {
                    failedCount++;
                    LOG.info("[KO] {}", diag.getPath());
                }
            } catch (final IOException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
        LOG.info("Scanned {} files: {} recoverable ({})",
                count, recoverableCount, TimeFormatter.formatDurationSince(start));
    }
}
