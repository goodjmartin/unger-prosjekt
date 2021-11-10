package no.goodtech.dashboard.config.fetcher;

import no.goodtech.dashboard.model.IDashboardFetcher;
import no.goodtech.dashboard.model.SimulatorFetcher;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
//TODO: @Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class SimulatorFetcherConfig extends FetcherConfig {

	@NotNull
	private double minValue, maxValue;

	public SimulatorFetcherConfig() {
		this(null, 0, 100, 5);
	}

	public SimulatorFetcherConfig(String id, double minValue, double maxValue, Integer refreshIntervalInSeconds) {
		setId(id);
		this.minValue = minValue;
		this.maxValue = maxValue;
		setRefreshIntervalInSeconds(refreshIntervalInSeconds);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public IDashboardFetcher createFetcher(long cacheRetentionInterval) {
		return new SimulatorFetcher(getId(), minValue, maxValue, isFullFetch(), cacheRetentionInterval);
	}

}
