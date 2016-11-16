package com.github.nicosensei.lostdir.recover;

import com.github.nicosensei.lostdir.elasticsearch.LocalNode;
import com.github.nicosensei.lostdir.elasticsearch.LocalNodeDefaults;
import com.github.nicosensei.lostdir.elasticsearch.Scroll;
import com.github.nicosensei.lostdir.helpers.GenericJsonObjectMapper;
import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import com.github.nicosensei.lostdir.metadata.FileNameBuilder;
import com.github.nicosensei.lostdir.metadata.JpgFileNameBuilder;
import com.github.nicosensei.lostdir.metadata.Mp4FileNameBuilder;
import com.github.nicosensei.lostdir.scan.Extension;
import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import com.github.nicosensei.lostdir.scan.KeyValuePair;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nicos on 11/4/2016.
 */
public final class Recovery {

    private static final Logger LOG = LoggerFactory.getLogger(Recovery.class);

    private enum Field {
        extension("extensions.extension");

        private final String field;

        Field(String field) {
            this.field = field;
        }
    }

    private final LocalNode localNode;
    private final GenericJsonObjectMapper<FileDiagnostic> mapper = new GenericJsonObjectMapper<>();

    public Recovery(final String esHomeDir) {
        localNode = new LocalNode(
                esHomeDir,
                LocalNodeDefaults.CLUSTER_NAME,
                LocalNodeDefaults.TCP_PORT,
                true);
        localNode.client().admin().indices().prepareRefresh(FileDiagnostic.INDEX).get();
    }

    public final void recover(final String ext, final File outputDir, final FileNameBuilder fileNameBuilder) {
        final long start = System.currentTimeMillis();
        long seq = 0;

        final Scroll scroll = new Scroll(
                localNode.client(),
                FileDiagnostic.INDEX,
                FileDiagnostic.TYPE,
                QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(Field.extension.field, ext)),
                500,
                null);
        SearchHit hit;
        long count = 0;
        while ((hit = scroll.next()) != null) {
            count++;
            final FileDiagnostic diag;
            try {
                diag = mapper.deserialize(hit.getSource(), FileDiagnostic.class);
            } catch (final IOException e) {
                LOG.error("Failed to deserialize doc " + hit.getId(), e);
                continue;
            }

            final String filePath = diag.getPath();
            final String name = fileNameBuilder.build(metadataAsMap(diag, ext));
            recover(filePath, outputDir.getAbsolutePath() + File.separator + name);
            LOG.info("Recovered {} to {}", filePath, name.toString());
        }

        LOG.info("Processed {} files in {}", count, TimeFormatter.formatDurationSince(start));
    }

    public final void close() {
        localNode.destroy();
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            LOG.error("Expected arguments: <ES home dir> <output dir>");
            System.exit(-1);
        }

        final String esHomeDir = args[0];
        final String outDirPath = args[1];

        final File outputDir = new File(outDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        if (!outputDir.isDirectory() || !outputDir.canWrite()) {
            LOG.error("Cannot write to " + outputDir.getAbsolutePath());
            return;
        }
        LOG.info("Will recover files to " + outputDir.getAbsolutePath());

        final Recovery reco = new Recovery(esHomeDir);
        try {
            reco.recover("JPG", outputDir, new JpgFileNameBuilder());
            reco.recover("M4V", outputDir, new Mp4FileNameBuilder("m4v"));
        } finally {
            reco.close();
        }
    }

    private final void recover(final String sourcePath, final String targetPath) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(sourcePath);
            out = new FileOutputStream(targetPath);
            final int size = IOUtils.copy(in, out);
            if (size < 0) {
                LOG.error("Failed to copy {}", sourcePath);
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private final Map<String, Object> metadataAsMap(final FileDiagnostic diag, final String ext) {
        final Extension extension = diag.getExtension(ext);
        if (extension == null) {
            throw new IllegalArgumentException("No extension " + ext + " for " + diag.getPath());
        }
        final ArrayList<KeyValuePair> metadata = extension.getMetadata();
        if (metadata == null) {
            return Collections.emptyMap();
        }
        final HashMap<String, Object> metaMap = new HashMap<>(metadata.size());
        for (KeyValuePair data : metadata) {
            metaMap.put(data.getKey(), data.getValue());
        }
        return metaMap;
    }

}
