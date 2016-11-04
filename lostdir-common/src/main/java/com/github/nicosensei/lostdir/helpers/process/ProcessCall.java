package com.github.nicosensei.lostdir.helpers.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/3/2016.
 */
public final class ProcessCall<T> implements Callable<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessCall.class);

    private final String[] commands;
    private final File workDir;
    private final StdOutProcessor<T> processor;

    private Process process;

    public ProcessCall(String[] commands, File workDir, StdOutProcessor<T> processor) {
        this.commands = commands;
        this.workDir = workDir;
        this.processor = processor;
    }

    @Override
    public T call() throws Exception {
        final ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(workDir);

        process = processBuilder.start();
        final T result = processor.processStdOut(process);

        //Wait to get exit value
        try {
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Timeout for process execution {}", commands);
            }
            process.destroy();
        }

        return result;
    }

    public void destroyProcess() {
        if (process != null) {
            process.destroyForcibly();
        }
    }

}
