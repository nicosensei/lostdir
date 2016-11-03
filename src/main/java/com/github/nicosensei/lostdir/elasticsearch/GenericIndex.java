package com.github.nicosensei.lostdir.elasticsearch;

import com.github.nicosensei.lostdir.helpers.StringSubstitutor;
import com.github.nicosensei.lostdir.helpers.TextUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nicos on 11/3/2016.
 */
public final class GenericIndex {

    private static final Logger LOG = LoggerFactory.getLogger(GenericIndex.class);

    private static final String VAR_MAPPING = "mapping";
    private static final String RESOURCE_SETTINGS = "/elasticsearch/_settings.json";
    private static final String RESOURCE_MAPPING_TEMPLATE = "/elasticsearch/template_mapping.json";
    private static final String TMPL_LOG_INDEX_RECORD = "Indexed metadata record of type '%s' with id '%s'";
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

    public GenericIndex(final Client client) {
        this.client = client;
        this.adminClient = client.admin().indices();
    }

    public void indexDocument(final String index, final MapDocument record) {
        final String type = record.getType();
        if (!isRegisteredType(index, type)) {
            registerType(index, type);
        }
        try {
            client.prepareIndex(
                    index,
                    record.getType(),
                    record.getId())
                    .setSource(record.getSource())
                    .get();
            LOG.info(String.format(TMPL_LOG_INDEX_RECORD, record.getType(), record.getId()));
        } catch (final ElasticsearchException e) {
            // Non blocking error
            LOG.error(String.format(ErrorType.indexRecordFailed.messageTemplate,
                    record.getType(),
                    record.getId()));
        }
    }

    public final MapDocument getDocument(final String index, final String type, final String id) {
        if (!isRegisteredType(index, type)) {
            return null;
        }
        final GetResponse get = client.prepareGet(index, type, id).get();
        if (!get.isExists()) {
            return null;
        }
        return new MapDocument(type, id, get.getSource());
    }

    public final List<MapDocument> searchDocuments(
            final String index,
            final String type,
            final Map<String, Object> filter,
            final Map<String, Object> mustNot,
            final Set<String> exist,
            final Set<String> missing,
            final Map<String, SortOrder> sorts,
            final int size) {
        if (!isRegisteredType(index, type)) {
            return Collections.EMPTY_LIST;
        }

        final BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (String field : filter.keySet()) {
            query.filter(QueryBuilders.termQuery(field, filter.get(field)));
        }
        for (String field : mustNot.keySet()) {
            query.mustNot(QueryBuilders.termQuery(field, mustNot.get(field)));
        }
        for (String field : exist) {
            query.filter(QueryBuilders.existsQuery(field));
        }
        for (String field : missing) {
            query.mustNot(QueryBuilders.existsQuery(field));
        }

        final SearchRequestBuilder search = client.prepareSearch(index)
                .setTypes(type)
                .setSize(size)
                .setQuery(query);

        for (String field : sorts.keySet()) {
            search.addSort(SortBuilders.fieldSort(field).order(sorts.get(field)));
        }

        final SearchResponse resp = search.get();
        if (resp.getHits().getTotalHits() == 0) {
            return Collections.EMPTY_LIST;
        }

        final SearchHit[] hits = resp.getHits().hits();
        final ArrayList<MapDocument> docs = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            docs.add(new MapDocument(type, hit.getId(), hit.getSource()));
        }
        return docs;
    }

    public boolean isRegisteredType(final String index, final String type) {
        final GetMappingsResponse mappingsResp = adminClient.prepareGetMappings(index).get();
        return mappingsResp.mappings().get(index).containsKey(type);
    }

    public void registerType(final String index, final String type) {
        try {
            final StringBuilder template = TextUtils.readTextFile(GenericIndex.class.getResourceAsStream(RESOURCE_MAPPING_TEMPLATE));
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
    public void createIndex(final String indexName)
    {

        // Get the index settings file
        StringBuilder settingsText = null;
        try {
            settingsText = TextUtils.readTextFile(GenericIndex.class.getResourceAsStream(RESOURCE_SETTINGS));
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

}
