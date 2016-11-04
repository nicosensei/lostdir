package com.github.nicosensei.lostdir.elasticsearch;

import com.github.nicosensei.lostdir.helpers.GlobalConstants;
import com.github.nicosensei.lostdir.helpers.StringSubstitutor;
import com.github.nicosensei.lostdir.helpers.TextUtils;
import com.github.nicosensei.lostdir.helpers.TimeFormatter;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.VersionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicos on 11/3/2016.
 */
public final class GenericIndexer implements BulkProcessor.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(GenericIndexer.class);

    private static final String VAR_MAPPING = "mapping";
    private static final String RESOURCE_SETTINGS = "/elasticsearch/_settings.json";
    private static final String RESOURCE_MAPPING_TEMPLATE = "/elasticsearch/template_mapping.json";
    private static final String TMPL_LOG_ADD_MAPPING = "Registered metadata record mapping '%s'";

    private enum ErrorType {
        indexRecordFailed("Failed to index document (type=%s, id=%s)");

        private final String messageTemplate;

        ErrorType(final String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }
    }

    private final Client client;
    private final IndicesAdminClient adminClient;

    private int bulkActions = 100;
    private int concurrentBulks = 0;
    private long waitForCloseMinutes = 5;
    private final BulkProcessor bulkProcessor;

    public GenericIndexer(final Client client) {
        this.client = client;
        this.adminClient = client.admin().indices();
        this.bulkProcessor = BulkProcessor.builder(client, this)
                .setBulkActions(bulkActions)
                .setConcurrentRequests(concurrentBulks)
                .setBackoffPolicy(BackoffPolicy.noBackoff())
                .build();
    }

    public void indexDocument(final String index, final MapDocument doc) {
        indexDocument(index, doc, false);
    }

    public void indexDocument(final String index, final MapDocument doc, final boolean flush) {
        final String type = doc.getType();
        if (!isRegisteredType(index, type)) {
            registerType(index, type);
        }
        try {
            final IndexRequest indexRequest;

            final String docId = doc.getId();
            if (docId != null) {
                indexRequest = new IndexRequest(index, doc.getType(), docId);
            } else {
                // ES will generate the ID
                indexRequest = new IndexRequest(index, doc.getType());
            }
            indexRequest.source(doc.getSource());

            final Long version = doc.getVersion();
            if (version != null) {
                indexRequest.version(doc.getVersion());
                indexRequest.versionType(VersionType.EXTERNAL);
            }
            bulkProcessor.add(indexRequest);
            if (flush) {
                flush();
            }
        } catch (final ElasticsearchException e) {
            // Non blocking error
            LOG.error(String.format(ErrorType.indexRecordFailed.messageTemplate,
                    doc.getType(),
                    doc.getId()));
        }
    }

    public boolean isRegisteredType(final String index, final String type) {
        final GetMappingsResponse mappingsResp = adminClient.prepareGetMappings(index).get();
        return mappingsResp.mappings().get(index).containsKey(type);
    }

    public void registerType(final String index, final String type) {
        try {
            final StringBuilder template = TextUtils.readTextFile(GenericIndexer.class.getResourceAsStream(RESOURCE_MAPPING_TEMPLATE));
            new StringSubstitutor().replaceIn(template, Collections.singletonMap(VAR_MAPPING, type));

            final PutMappingResponse resp = client.admin().indices().preparePutMapping(index)
                    .setType(type)
                    .setSource(template.toString())
                    .get();
            if (!resp.isAcknowledged()) {
                throw new ElasticsearchException("Failed to create mapping {} in index {}", type, index);
            }
            LOG.info(String.format(TMPL_LOG_ADD_MAPPING, type));
        } catch (final IOException e) {
            throw new IllegalArgumentException("Classpath resource not found!");
        }
    }

    /**
     * Creates an index.
     * @param indexName the index name
     * @throws ElasticsearchException
     */
    public void createIndex(final String indexName) {

        final IndicesExistsResponse existsResponse = adminClient.prepareExists(indexName).get();
        if (existsResponse.isExists()) {
            LOG.info("Index {} already exists", indexName);
            return;
        }

        // Get the index settings file
        StringBuilder settingsText = null;
        try {
            settingsText = TextUtils.readTextFile(GenericIndexer.class.getResourceAsStream(RESOURCE_SETTINGS));
        } catch (final IOException e) {
            throw new ElasticsearchException("Index {} creation failed", indexName, e);
        }

        // Create the index with the settings
        CreateIndexRequestBuilder create = client.admin().indices().prepareCreate(indexName)
                .setSettings(settingsText.toString());
        if (!create.get().isAcknowledged()) {
            throw new ElasticsearchException("Index {} creation failed", indexName);
        }
        LOG.info("Created index '" + indexName + "'");
    }

    @Override
    public void beforeBulk(final long l, final BulkRequest bulkRequest) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending index request bulk of " + bulkRequest.numberOfActions() + " documents");
        }
    }

    @Override
    public void afterBulk(final long l, final BulkRequest bulkRequest, final BulkResponse bulkResponse) {
        if (bulkResponse.hasFailures()) {
            final StringBuilder sb = new StringBuilder("Bulk errors:");
            for (BulkItemResponse itemResponse : bulkResponse) {
                if (itemResponse.isFailed()) {
                    sb.append(GlobalConstants.NEWLINE).append(itemResponse.getOpType())
                            .append(GlobalConstants.CHAR_SPACE).append(itemResponse.getIndex())
                            .append(GlobalConstants.CHAR_SLASH).append(itemResponse.getType())
                            .append(GlobalConstants.CHAR_SLASH).append(itemResponse.getId());
                }
            }
            throw new ElasticsearchException(sb.toString());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Successfully bulk indexed " + bulkRequest.numberOfActions() + " documents in "
                    + TimeFormatter.formatDuration(bulkResponse.getTookInMillis()));
        }
    }

    @Override
    public void afterBulk(final long l, final BulkRequest bulkRequest, final Throwable throwable) {
        String message = throwable.getMessage();
        LOG.error("Bulk indexing raised an error {}: {}",
                throwable.getClass().getCanonicalName(),
                message,
                throwable);
    }

    public void flush() {
        bulkProcessor.flush();
    }

    public void close() {
        bulkProcessor.flush(); // don't forget remaining actions
        final long waitStart = System.currentTimeMillis();
        try {
            bulkProcessor.awaitClose(waitForCloseMinutes, TimeUnit.MINUTES);
        } catch (final InterruptedException e) {
            LOG.info("Bulk processor closed after " + TimeFormatter.formatDurationSince(waitStart));
        }
    }
}
