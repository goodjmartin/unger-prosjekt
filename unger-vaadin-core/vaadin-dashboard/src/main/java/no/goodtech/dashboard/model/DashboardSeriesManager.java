package no.goodtech.dashboard.model;

import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.dashboard.AxisChangedAdaptor;
import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.config.fetcher.FetcherConfigFinder;
import no.goodtech.dashboard.config.ui.*;
import no.goodtech.push.ISubscriber;
import org.hibernate.WrongClassException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class DashboardSeriesManager implements IDashboardSeriesManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardSeriesManager.class);
	private final Map<SeriesConfig, IDashboardFetcher> fetchersPerSeries = new HashMap<>();
	private final boolean enabled;
	private final long cacheRetentionInterval;
	private final Set<ScheduledExecutorService> scheduledExecutorServices = new HashSet<>();

	public DashboardSeriesManager(final boolean enabled, final long cacheRetentionInterval, AxisChangedAdaptor axisChangedAdaptor) {
		this.enabled = enabled;
		this.cacheRetentionInterval = cacheRetentionInterval;

		refresh();

		final ISubscriber<AxisConfig> axisConfigChangedSubscriber = createAxisConfigChangeSubscriber();
		axisChangedAdaptor.register(axisConfigChangedSubscriber);
	}

	private ISubscriber<AxisConfig> createAxisConfigChangeSubscriber() {
		return new ISubscriber<AxisConfig>() {
			@Override
			public void handle(AxisConfig axisConfigThatIsChanged) {
				for (SeriesConfig seriesConfig : fetchersPerSeries.keySet()) {
					final AxisConfig axisConfig = seriesConfig.getAxisConfig();
					if (axisConfig != null && axisConfig.equals(axisConfig)) {
						axisConfig.setMinValue(axisConfigThatIsChanged.getMinValue());
						axisConfig.setMaxValue(axisConfigThatIsChanged.getMaxValue());
						final ChartConfig panelConfig = axisConfig.getPanelConfig();
						LOGGER.debug("axis changed on chart {}, pk = {} on dashboard {}, min={}, max={}", panelConfig.getTitle(),
								panelConfig.getPk(), panelConfig.getDashboardConfig().getId(), axisConfig.getMinValue(), axisConfig.getMaxValue());
					}
				}
			}
		};
	}

	public void refresh() {
		fetchersPerSeries.clear();
		for (ScheduledExecutorService service : scheduledExecutorServices) {
			service.shutdown();
		}
		scheduledExecutorServices.clear();

		if (enabled) {
			Map<FetcherConfig, IDashboardFetcher> fetchers = new HashMap<>();
			try {
				final List<FetcherConfig> fetcherConfigs = new FetcherConfigFinder().list();
				for (FetcherConfig fetcherConfig : fetcherConfigs) {
					final IDashboardFetcher seriesFetcher = createFetcher(fetcherConfig);
					if (seriesFetcher != null) {
						final Integer refreshIntervalInSeconds = fetcherConfig.getRefreshIntervalInSeconds();
						if (refreshIntervalInSeconds != null) {
							// Schedule a new fetcher task
							//noinspection NullableProblems
							ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
								@Override
								public Thread newThread(final Runnable runnable) {
									return new Thread(runnable, "goodtech-dashboard-fetcher-" + fetcherConfig.getId());
								}
							});
							final ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(seriesFetcher, 0, refreshIntervalInSeconds, TimeUnit.SECONDS);
							scheduledExecutorServices.add(scheduledExecutorService);
						}
						fetchers.put(fetcherConfig, seriesFetcher);
					}
				}
				// Place fetcher on map keyed on series
				final List<SeriesConfig> seriesConfigs = new DashboardConfigFinder().listSeriesConfigs();
				for (SeriesConfig seriesConfig : seriesConfigs) {
					fetchersPerSeries.put(seriesConfig, fetchers.get(seriesConfig.getFetcherConfig()));
				}
				LOGGER.info("Dashboard service initialized: {} fetchers handling {} series", fetcherConfigs.size(), seriesConfigs.size());
			} catch (WrongClassException e) {
				LOGGER.error("No dashboards loaded because of unknown dashboard config: " + e.getMessage(), e);
			}
		} else {
			LOGGER.warn("Dashboard is disabled");
		}
	}

	private IDashboardFetcher createFetcher(FetcherConfig fetcherConfig) {
		try {
			return fetcherConfig.createFetcher(cacheRetentionInterval);
		} catch (RuntimeException exception) {
			LOGGER.error("Couldn't create fetcher: " + exception.getMessage(), exception);
		}
		return null;
	}

	public SeriesInfo getSamplePoints(final SeriesConfig seriesConfig, final Date startTime) {
		IDashboardFetcher fetcher = fetchersPerSeries.get(seriesConfig);
		if (fetcher != null ) {
			if (fetcher instanceof ISeriesFetcher) {
				final ISeriesFetcher singleSeriesLiveFetcher = (ISeriesFetcher) fetcher;
				return singleSeriesLiveFetcher.getSamplePoints(startTime);
			} if (fetcher instanceof IMultiSeriesLiveFetcher) {
				final IMultiSeriesLiveFetcher multiSeriesLiveFetcher = (IMultiSeriesLiveFetcher) fetcher;
				return multiSeriesLiveFetcher.getSamplePoints(seriesConfig, startTime);
			} else {
				LOGGER.warn("{} is not a live fetcher, so data for {} will not be fetched", fetcher, seriesConfig);
				return null;
			}
		}
		return null;
	}

	@Override
	public Set<SeriesConfig> getSeriesConfigs(PanelConfig panelConfig) {
		Set<SeriesConfig> seriesConfigs = new HashSet<>();
		for (SeriesConfig seriesConfig : fetchersPerSeries.keySet()) {
			if (seriesConfig.getPanelConfig().equals(panelConfig)) {
				seriesConfigs.add(seriesConfig);
			}
		}
		for (IDashboardFetcher fetcher : new HashSet<>(fetchersPerSeries.values())) {
			//include possibly auto-generated series from the fetchers
			if (fetcher instanceof MultiSeriesFetcher)  {
				MultiSeriesFetcher multiSeriesFetcher = (MultiSeriesFetcher) fetcher;
				for (SeriesConfig seriesConfig : multiSeriesFetcher.getSeriesConfigs().getSeriesConfigs(panelConfig)) {
					//include auto-generated series connected to panel (for instance order swaps)
					seriesConfigs.add(seriesConfig);
					if (!fetchersPerSeries.containsKey(seriesConfig)) {
						fetchersPerSeries.put(seriesConfig, multiSeriesFetcher);
					}
				}
				for (SeriesConfig seriesConfig : multiSeriesFetcher.getSeriesConfigs()) {
					final SeriesConfig connectedSeriesConfig = seriesConfig.getConnectedSeriesConfig();
					if (connectedSeriesConfig != null) {
						if (seriesConfigs.contains(connectedSeriesConfig)) {
							//add series that is connected to a series we want. This is relevant for auto-generated min/max/target
							seriesConfigs.add(seriesConfig);
							if (!fetchersPerSeries.containsKey(seriesConfig)) {
								fetchersPerSeries.put(seriesConfig, multiSeriesFetcher);
							}
						}
					}
				}
			}
		}
		return seriesConfigs;
	}

	/**
	 * Run ad-hoc queries from relevant fetchers and put data together
	 * @param seriesConfigs the series we need data for
	 * @param parameters query parameters, e.g. orderNumber -> 4242
	 * @return data per series
	 */
	public Map<SeriesConfig, SeriesInfo> getData(List<SeriesConfig> seriesConfigs, Map<String, IReportParameter> parameters) {
		Map<SeriesConfig, SeriesInfo> results = new HashMap<>();

		//sort series by fetcher, so we can fetch data for multiple series at once
		Map<IAdHocFetcher, List<SeriesConfig>> seriesPerFetcher = new HashMap<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			IDashboardFetcher fetcher = fetchersPerSeries.get(seriesConfig);
			if (fetcher instanceof IAdHocFetcher) {
				List<SeriesConfig> seriesConfigsForThisFetcher = seriesPerFetcher.get(fetcher);
				if (seriesConfigsForThisFetcher == null) {
					seriesConfigsForThisFetcher = new ArrayList<>();
				}
				seriesConfigsForThisFetcher.add(seriesConfig);
				seriesPerFetcher.put((IAdHocFetcher) fetcher, seriesConfigsForThisFetcher);
			} else {
				LOGGER.warn("Fetcher {} is not supported, so data for {} will not be fetched", fetcher, seriesConfig);
			}
		}
		for (Map.Entry<IAdHocFetcher, List<SeriesConfig>> entry : seriesPerFetcher.entrySet()) {
			//use fetcher to fetch data
			final IAdHocFetcher fetcher = entry.getKey();
			results.putAll(fetcher.getData(entry.getValue(), parameters));
		}
		return results;
	}
}
