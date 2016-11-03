package com.github.nicosensei.lostdir.trid;

import com.github.nicosensei.lostdir.helpers.GlobalConstants;
import com.github.nicosensei.lostdir.helpers.process.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by nicos on 11/3/2016.
 */
public final class RunTrid {

    private static final Logger LOG = LoggerFactory.getLogger(RunTrid.class);

    private static final File TRID_DIR = new File(GlobalConstants.CURRENT_DIR
            + File.separator + "src"
            + File.separator + "main"
            + File.separator + "resources"
            + File.separator + "trid");
    private static final String TRID_EXE = TRID_DIR + File.separator + "trid.exe";

    public void runTrid(final File file) throws IOException {

        assert file.isFile();
        assert file.canRead();

        final TridOuputProcessor proc = new TridOuputProcessor(file.getAbsolutePath());
        final FileTrid result = new ProcessRunner().run(
                new String[] { TRID_EXE, file.getAbsolutePath() },
                TRID_DIR,
                proc);
        assert result.size() > 0;
        LOG.info(result.toString());
    }

    public static void main(final String[] args) {
        final RunTrid trid = new RunTrid();
        try {
            trid.runTrid(new File(args[0]));
        } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
            System.exit(-1);
        }
    }

}
