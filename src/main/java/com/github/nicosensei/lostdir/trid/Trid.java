package com.github.nicosensei.lostdir.trid;

import com.github.nicosensei.lostdir.helpers.GlobalConstants;
import com.github.nicosensei.lostdir.helpers.process.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/3/2016.
 */
public final class Trid {

    private static final File TRID_DIR = new File(GlobalConstants.CURRENT_DIR
            + File.separator + "src"
            + File.separator + "main"
            + File.separator + "resources"
            + File.separator + "trid");
    private static final String TRID_EXE = TRID_DIR + File.separator + "trid.exe";

    private final long timeOutValue;
    private final TimeUnit timeOutUnit;

    public Trid() {
        this.timeOutValue = ProcessRunner.DEFAULT_TIMEOUT_VALUE;
        this.timeOutUnit = ProcessRunner.DEFAULT_TIMEOUT_UNIT;
    }

    public Trid(long timeOutValue, TimeUnit timeOutUnit) {
        this.timeOutValue = timeOutValue;
        this.timeOutUnit = timeOutUnit;
    }

    public FileDiagnostic testFile(final File file) throws IOException {
        if (!file.isFile() && !file.canRead()) {
            throw new IOException(file.getAbsolutePath() + " is not a readable file");
        }
        if (0 == file.length()) {
            throw new IOException(file.getAbsolutePath() + " has zero size");
        }

        final TridOuputProcessor proc = new TridOuputProcessor(file.getAbsolutePath());
        final FileDiagnostic result = new ProcessRunner().run(
                new String[] { TRID_EXE, file.getAbsolutePath() },
                TRID_DIR,
                proc,
                timeOutValue,
                timeOutUnit);
        return result;

    }

}
