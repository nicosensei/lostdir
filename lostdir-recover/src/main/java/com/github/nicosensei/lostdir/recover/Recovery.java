package com.github.nicosensei.lostdir.recover;

import com.github.nicosensei.lostdir.elasticsearch.LocalNode;
import com.github.nicosensei.lostdir.elasticsearch.LocalNodeDefaults;
import com.github.nicosensei.lostdir.elasticsearch.Scroll;
import com.github.nicosensei.lostdir.helpers.GenericJsonObjectMapper;
import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import com.github.nicosensei.lostdir.scan.FileDiagnostic;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.PropertyTypeException;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by nicos on 11/4/2016.
 */
public final class Recovery {

    private static final Logger LOG = LoggerFactory.getLogger(Recovery.class);

    private enum Field {
        path("path"),
        extension("extension.extension");

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

    public final void recoverJPG(final File outputDir) {
        final long start = System.currentTimeMillis();

        final BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        JpegParser JpegParser = new JpegParser();

        final Scroll scroll = new Scroll(
                localNode.client(),
                FileDiagnostic.INDEX,
                FileDiagnostic.TYPE,
                QueryBuilders.boolQuery()
                        .filter(QueryBuilders.termQuery(Field.extension.field, "JPG")),
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
            final ParseContext pcontext = new ParseContext();
            final FileInputStream inputstream;
            try {
                inputstream = new FileInputStream(new File(filePath));
            } catch (final IOException e) {
                LOG.error("Failed to deserialize doc " + hit.getId(), e);
                continue;
            }
            try {
                JpegParser.parse(inputstream, handler, metadata, pcontext);
            } catch (final IOException | TikaException | SAXException | PropertyTypeException e) {
                LOG.error("Failed to parse file " + filePath, e);
                continue;
            }

            LOG.info(filePath);
        }

        LOG.info("Processed {} files in {}", count, TimeFormatter.formatDurationSince(start));
    }

    public final void close() {
        localNode.destroy();
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 3) {
            LOG.error("Expected arguments: <extension> <ES home dir> <output dir>");
            System.exit(-1);
        }

        final String ext = args[0];
        final String esHomeDir = args[1];
        final String outDirPath = args[2];

        if (!"JPG".equals(ext)) {
            LOG.error("Recovery for type {} not yet supported!", ext);
            System.exit(0);
        }

        final Recovery reco = new Recovery(esHomeDir);
        try {
            reco.recoverJPG(new File(outDirPath));
        } finally {
            reco.close();
        }
    }

}
