package no.goodtech.dashboard.config.fetcher;

import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.model.AbstractFetcher;
import no.goodtech.dashboard.model.IDashboardFetcher;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.EntityStub;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration for one dashboard fetcher.
 * A fetcher is a service that fetches data for one or more dashboard series
 */
@Entity
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public abstract class FetcherConfig extends AbstractEntityImpl<FetcherConfig> {

	@NotNull
	private String id;

	private Integer refreshIntervalInSeconds = 60;

	private Boolean fullFetch;


	public FetcherConfig() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getRefreshIntervalInSeconds() {
		return refreshIntervalInSeconds;
	}

	public void setRefreshIntervalInSeconds(Integer refreshIntervalInSeconds) {
		this.refreshIntervalInSeconds = refreshIntervalInSeconds;
	}

	public Boolean getFullFetch() {
		return fullFetch;
	}

	public boolean isFullFetch() {
		return fullFetch != null && fullFetch;
	}

	public void setFullFetch(Boolean fullFetch) {
		this.fullFetch = fullFetch;
	}

	public abstract IDashboardFetcher createFetcher(long cacheRetentionInterval);
}
