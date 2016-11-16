package com.github.nicosensei.lostdir.scan.trid;

import com.github.nicosensei.lostdir.helpers.GlobalConstants;
import com.github.nicosensei.lostdir.helpers.process.ProcessRunner;
import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/3/2016.
 */
public final class Trid {

    private static final Logger LOG = LoggerFactory.getLogger(Trid.class);

    private static final File TRID_RUNTIME_DIR =
            new File(GlobalConstants.CURRENT_DIR + File.separator + "trid");
    private static final String TRID_RUNTIME_EXE =
            TRID_RUNTIME_DIR.getAbsolutePath() + File.separator + "trid.exe";
    private static final String TRID_RUNTIME_DEFS =
            TRID_RUNTIME_DIR.getAbsolutePath() + File.separator + "triddefs" + ".trd";

    private static final String RESOURCE_TRID_EXE = "/trid/trid.exe";
    private static final String RESOURCE_TRID_DEFS = "/trid/triddefs.trd";

    private final long timeOutValue;
    private final TimeUnit timeOutUnit;

    public Trid()throws IOException  {
        this(ProcessRunner.DEFAULT_TIMEOUT_VALUE, ProcessRunner.DEFAULT_TIMEOUT_UNIT);
    }

    public Trid(long timeOutValue, TimeUnit timeOutUnit) throws IOException {
        this.timeOutValue = timeOutValue;
        this.timeOutUnit = timeOutUnit;
        unpackTridRuntime();
    }

    public FileDiagnostic testFile(final File file) throws IOException {
        if (!file.isFile() && !file.canRead()) {
            throw new IOException(file.getAbsolutePath() + " is not a readable file");
        }
        if (0 == file.length()) {
            LOG.info(file.getAbsolutePath() + " has zero size");
            return new FileDiagnostic(file);
        }

        final TridOuputProcessor proc = new TridOuputProcessor(file.getAbsolutePath());
        final FileDiagnostic result = new ProcessRunner().run(
                new String[] { TRID_RUNTIME_EXE, file.getAbsolutePath() },
                TRID_RUNTIME_DIR,
                proc,
                timeOutValue,
                timeOutUnit);
        return result;
    }

    private final void unpackTridRuntime() throws IOException {
        if (!TRID_RUNTIME_DIR.exists()) {
            TRID_RUNTIME_DIR.mkdirs();
            copyClasspathResource(RESOURCE_TRID_EXE, TRID_RUNTIME_EXE);
            copyClasspathResource(RESOURCE_TRID_DEFS, TRID_RUNTIME_DEFS);
        }
    }

    private final void copyClasspathResource(final String resource, final String file) throws IOException {
        final FileOutputStream fos = new FileOutputStream(file);
        try {
            IOUtils.copy(Trid.class.getResourceAsStream(resource), fos);
        } finally {
            fos.close();
        }
    }

}
