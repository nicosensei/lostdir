package com.github.nicosensei.lostdir.helpers.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by nicos on 11/3/2016.
 */
public final class ProcessRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessRunner.class);

    public <T> T run(
            final String[] commands,
            final File workDir,
            final StdOutProcessor<T> processor) throws IOException {

        final ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(workDir);

        final Process process = processBuilder.start();
        final T result = processor.processStdOut(process);

        //Wait to get exit value
        try {
            int exitValue = process.waitFor();
            LOG.info("Exit Value is {}", exitValue);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
