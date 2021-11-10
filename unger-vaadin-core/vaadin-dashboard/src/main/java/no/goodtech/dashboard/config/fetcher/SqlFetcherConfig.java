package no.goodtech.dashboard.config.fetcher;

import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.model.AbstractFetcher;
import no.goodtech.dashboard.model.SqlFetcher;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class SqlFetcherConfig extends FetcherConfig {

	@NotNull
	private String datasourceName;

	@NotNull
	private String query;

	SqlFetcherConfig() {
		this(null, null, null);
	}

	public SqlFetcherConfig(String id, String datasourceName, String query) {
		this.setId(id);
		this.datasourceName = datasourceName;
		this.query = query;
	}

	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public AbstractFetcher createFetcher(long cacheRetentionInterval) {
		return new SqlFetcher(getId(), datasourceName, query, isFullFetch(), cacheRetentionInterval);
	}
}
