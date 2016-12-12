package com.github.nicosensei.lostdir.rename;

import com.github.nicosensei.lostdir.rename.config.Configuration;
import com.github.nicosensei.lostdir.rename.config.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nicos on 11/24/2016.
 */
public final class FileProcessor implements FileVisitor<Path> {

    private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);

    private final ExecutorService executorService;

    private final Configuration configuration;

    public FileProcessor(final ExecutorService executorService, final Configuration configuration) {
        this.executorService = executorService;
        this.configuration = configuration;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        LOG.info("Will scan folder {}", dir.toAbsolutePath().toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        final File f = file.toFile();
        for (final Task t : configuration.getTasks()) {
            final Matcher m = Pattern.compile(t.getFilePattern()).matcher(f.getName());
            if (m.matches()) {
                executorService.submit(new RenameTask(t, f));
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        LOG.error("Failed to process file {}", file.toAbsolutePath().toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        LOG.info("Scanned folder {}", dir.toAbsolutePath().toString());
        return FileVisitResult.CONTINUE;
    }
}
