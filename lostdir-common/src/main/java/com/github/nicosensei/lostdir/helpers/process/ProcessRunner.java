package com.github.nicosensei.lostdir.helpers.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by nicos on 11/3/2016.
 */
public final class ProcessRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessRunner.class);

    public static final long DEFAULT_TIMEOUT_VALUE = 30L;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public <T> T run(
            final String[] commands,
            final File workDir,
            final StdOutProcessor<T> processor) throws IOException {
        return run(commands, workDir, processor, DEFAULT_TIMEOUT_VALUE, DEFAULT_TIMEOUT_UNIT);
    }

    public <T> T run(
            final String[] commands,
            final File workDir,
            final StdOutProcessor<T> processor,
            final long timeOutValue,
            final TimeUnit timeOutUnit) throws IOException {

        final ExecutorService exec = Executors.newFixedThreadPool(1);

        final ProcessCall<T> call = new ProcessCall<T>(commands, workDir, processor);
        final Future<T> f = exec.submit(call);

        try {
            return f.get(timeOutValue, timeOutUnit);
        } catch (final InterruptedException e) {
            LOG.error(e.getMessage(), e);
        } catch (final ExecutionException e) {
            LOG.error(e.getMessage(), e);
        } catch (final TimeoutException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Timeout for process {}", commands);
            }
            call.destroyProcess();
        } finally {
            exec.shutdownNow();
        }

        return null;
    }
}
