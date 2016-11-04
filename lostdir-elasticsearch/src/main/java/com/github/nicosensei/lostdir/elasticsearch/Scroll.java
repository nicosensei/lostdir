package com.github.nicosensei.lostdir.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Result set that wraps an index scroll.
 */
public final class Scroll {

    private static final org.elasticsearch.search.Scroll SCROLL = new org.elasticsearch.search.Scroll(TimeValue.timeValueMinutes(5));

    private final Client client;

	private SearchHit[] hits;
	private int cursor = 0;

    private String scrollId;

	public Scroll(
	        final Client client,
            final String index,
            final String type,
            final QueryBuilder query,
            final int scrollSize,
            final String sortField) {
		this.client = client;
		final SearchRequestBuilder scrollInit = client.prepareSearch(index)
		        .setScroll(SCROLL)
		        .setQuery(query)
		        .setTypes(type)
		        .setSize(scrollSize);
        if (sortField != null && !sortField.isEmpty()) {
            scrollInit.addSort(sortField, SortOrder.ASC);
        }

        final SearchResponse resp = scrollInit.get();
		hits = resp.getHits().hits();
        scrollId = resp.getScrollId();
	}

	public SearchHit next() {
        if (cursor >= hits.length) {
            final SearchResponse scroll = client.prepareSearchScroll(scrollId)
                    .setScroll(SCROLL)
                    .get();
            hits = scroll.getHits().hits();
            scrollId = scroll.getScrollId();
            if (hits.length == 0) {
                return null;
            } else {
                cursor = 0;
                return hits[cursor++];
            }
        } else {
            return hits[cursor++];
        }
    }

}
