package com.github.nicosensei.lostdir.rename;

import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import com.github.nicosensei.lostdir.rename.config.Configuration;
import com.github.nicosensei.lostdir.rename.config.ConfigurationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/24/2016.
 */
public final class Renamer {

    private static final Logger LOG = LoggerFactory.getLogger(Renamer.class);

    private final String sourceDir;

    private final int threadCount;

    public Renamer(final String sourceDir, final int threadCount) {
        this.sourceDir = sourceDir;
        this.threadCount = threadCount;
    }

    public static final void main(String[] args) throws IOException {
        if (args.length != 2) {
            LOG.error("Expected arguments: <source dir> <thread count>");
            System.exit(-1);
        }
        new Renamer(args[0], Integer.parseInt(args[1])).scanAndRename();
    }

    public void scanAndRename() throws IOException {
        final Configuration conf = ConfigurationMapper.getDefaults();
        conf.setAwaitTerminationInMinutes(30);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        try {
            Files.walkFileTree(new File(sourceDir).toPath(), new FileProcessor(executorService, conf));
        } finally {
            executorService.shutdown();
            final long startWaitForTermination = System.currentTimeMillis();
            try {
                executorService.awaitTermination(conf.getAwaitTerminationInMinutes(), TimeUnit.MINUTES);
            } catch (final InterruptedException e) {
                LOG.info("Stopped after {}", TimeFormatter.formatDurationSince(startWaitForTermination));
            }
        }
    }

}
