package com.github.nicosensei.lostdir.rename;

import com.github.nicosensei.lostdir.metadata.*;
import com.github.nicosensei.lostdir.rename.config.Task;
import com.github.nicosensei.lostdir.scan.Extension;
import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by nicos on 11/24/2016.
 */
public final class RenameTask implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(RenameTask.class);

    private final Task task;
    private final File file;
    private final AbstractMetadataExtractor metadataExtractor;
    private final FileNameBuilder fileNameBuilder;
    private final ArrayList<PickRule> pickRules = new ArrayList<>(5);

    public RenameTask(final Task task, final File file) {
        this.task = task;
        this.file = file;

        switch (task.getExt()) {
            case "jpg":
                metadataExtractor = new JpegMetadataExtractor();
                fileNameBuilder = new JpgFileNameBuilder();
                pickRules.add(new JpgMinResolutionRule("tiff:ImageWidth", 640, "tiff:ImageLength", 480));
                pickRules.add(new JpgMinResolutionRule("Exif Image Width", 640, "Exif Image Height", 480));
                pickRules.add(new JpgMinResolutionRule("Image Width", 640, "Image Height", 480));
                break;
            case "mp4":
                metadataExtractor = new M4vMetadataExtractor();
                fileNameBuilder = new Mp4FileNameBuilder("mp4");
                pickRules.add(new JpgMinResolutionRule("tiff:ImageWidth", 0, "tiff:ImageLength", 0));
                break;
            case "mp3":
                metadataExtractor = new Mp3MetadataExtractor();
                fileNameBuilder = new Mp3FileNameBuilder("mp3");
                pickRules.add(new HasTitleRule());
                break;
            default:
                throw new IllegalArgumentException("Unsupported extension " + task.getExt());
        }
    }

    @Override
    public Boolean call() throws Exception {
        final FileDiagnostic fd = new FileDiagnostic(file);
        fd.addOrMergeExtension(new Extension(metadataExtractor.getExtension(), 100, ""));
        metadataExtractor.extractTo(fd);
        final Extension extension = fd.getExtensions().get(0);
        boolean pick = false;
        for (PickRule rule : pickRules) {
            pick |= rule.pickFile(fd.metadataAsMap(extension));
        }

        if (!pick) {
            LOG.info("[KO] File {} discarded, no pick rule matched", fd.getPath());
            return false;
        }

        final String renameTo = fileNameBuilder.build(new File(fd.getPath()).getName(), fd.metadataAsMap(extension));
        final File source = new File(fd.getPath());
        final File dest = new File(source.getParent(), renameTo);
        try {
            Files.move(source.toPath(), dest.toPath());
        } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage());
        }
        LOG.info("[OK] Renamed {} to {}", fd.getPath(), dest.getAbsolutePath());

        return true;
    }
}
