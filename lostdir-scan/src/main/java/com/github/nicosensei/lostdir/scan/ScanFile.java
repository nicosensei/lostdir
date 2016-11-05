package com.github.nicosensei.lostdir.scan;

import com.github.nicosensei.lostdir.scan.trid.Trid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/3/2016.
 */
public final class ScanFile {

    private static final Logger LOG = LoggerFactory.getLogger(ScanFile.class);

    public static void main(final String[] args) throws IOException {
        final Trid trid = new Trid(10, TimeUnit.SECONDS);
        try {
            LOG.info("Testing {}", args[0]);
            final FileDiagnostic diag = trid.testFile(new File(args[0]));
            LOG.info(diag != null ? diag.toString() : "no result");
        } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
            System.exit(-1);
        }
    }
}
