package no.goodtech.dashboard.config.fetcher;

import no.goodtech.dashboard.model.AbstractFetcher;
import no.goodtech.dashboard.model.PiWebApiPlotFetcher;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class PiWebApiFetcherConfig extends FetcherConfig {

	@NotNull
	private String url;

	@NotNull
	private String tagWebId;

	private Integer intervals;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTagWebId() {
		return tagWebId;
	}

	public void setTagWebId(String tagWebId) {
		this.tagWebId = tagWebId;
	}

	public Integer getIntervals() {
		return intervals;
	}

	public void setIntervals(Integer intervals) {
		this.intervals = intervals;
	}

	@Override
	public AbstractFetcher createFetcher(long cacheRetentionInterval) {
		return new PiWebApiPlotFetcher(getId(), isFullFetch(), cacheRetentionInterval, url, tagWebId, intervals);
	}
}
