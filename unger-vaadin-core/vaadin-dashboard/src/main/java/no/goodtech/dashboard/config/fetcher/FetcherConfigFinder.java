package no.goodtech.dashboard.config.fetcher;

import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.search.FilterPanel;

public class FetcherConfigFinder
		extends AbstractFinder<FetcherConfig, FetcherConfig, FetcherConfigFinder>
		implements FilterPanel.IMaxRowsAware {

	public FetcherConfigFinder() {
		super("select c from FetcherConfig c", "c");
	}

	public FetcherConfigFinder setType(Class<?> type) {
		if (type != null) {
			addEqualFilter("TYPE(c)", type.getSimpleName());
		}
		return this;
	}

	public FetcherConfigFinder setDatasourceName(String datasourceName) {
		addEqualFilter(prefixWithAlias("datasourceName"), datasourceName);
		return this;
	}

	public FetcherConfigFinder orderById() {
		addSortOrder(prefixWithAlias("id"));
		return this;
	}

	/**
	 * Find only fetcher configs where we have automatic refresh
	 * @see FetcherConfig#getRefreshIntervalInSeconds()
	 */
	public FetcherConfigFinder setLiveOnly() {
		addNullFilter(prefixWithAlias("refreshIntervalInSeconds"));
		return this;
	}
}
