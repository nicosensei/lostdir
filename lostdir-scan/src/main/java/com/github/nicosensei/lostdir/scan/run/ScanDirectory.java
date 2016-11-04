package com.github.nicosensei.lostdir.scan.run;

import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import com.github.nicosensei.lostdir.scan.Trid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by nicos on 11/3/2016.
 */
public final class ScanDirectory {

    private static final Logger LOG = LoggerFactory.getLogger(ScanDirectory.class);

    public static void main(final String[] args) throws IOException {
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
                FileDiagnostic diag = trid.testFile(f);
                if (diag == null) {
                    diag = new FileDiagnostic(f.getAbsolutePath());
                }
                if (diag.getExtension() != null) {
                    recoverableCount++;
                    LOG.info("[OK] {} - {}", diag.getPath(), diag.getExtension().toString());
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
