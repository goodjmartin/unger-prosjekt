package no.goodtech.dashboard.model;

import no.cronus.common.utils.CollectionUtils;
import no.goodtech.admin.tabs.report.IReportParameter;
import no.goodtech.admin.tabs.report.SqlDataSetFetcher;
import no.goodtech.dashboard.config.ui.SeriesConfig;

import java.text.SimpleDateFormat;
import java.util.*;

public class SqlFetcher extends AbstractFetcher implements IAdHocFetcher {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private final String query;
	private final SampleFetcher sampleFetcher;
	private final String dataSource;

	public SqlFetcher(final String id, final String dataSource, final String query, final boolean fullFetch, final long cacheRetentionInterval) {
		super(id, fullFetch, cacheRetentionInterval);
		this.query = query;
		this.dataSource = dataSource;
		sampleFetcher = new SampleFetcher(dataSource);
	}

	@Override
	public List<SampleDTO> fetchNewSamplePoints(final Date startTime) {
		// Execute query
		//TODO: Send parameter to JDBC
		return sampleFetcher.getQueryResult(query.replaceAll("FROM_DATE", DATE_FORMAT.format(startTime)));
	}

	/**
	 * Run query and group values by series
	 * Result set should have 3 columns: time, seriesPk, value
	 * @param seriesConfigs info about relevant series
	 * @param parameters parameters to the query
	 * @return values by series
	 */
	@Override
	public Map<SeriesConfig, SeriesInfo> getData(List<SeriesConfig> seriesConfigs, Map<String, IReportParameter> parameters) {
		final SqlDataSetFetcher sqlDataSetFetcher = new SqlDataSetFetcher(query, CollectionUtils.flipMap(parameters), dataSource);
		final List<List<?>> rows = sqlDataSetFetcher.getRows();
		Map<Long, SeriesConfig> seriesConfigPerPk = new HashMap<>();
		for (SeriesConfig seriesConfig : seriesConfigs) {
			seriesConfigPerPk.put(seriesConfig.getPk(), seriesConfig);
		}

		Map<SeriesConfig, SeriesInfo> results = new HashMap<>();
		for (List<?> row : rows) {
			Object date = getX(row);
			Object seriesPk = getSeriesPk(row);
			Object value = getValue(row);

			if (value != null && date != null && seriesPk != null) {
				final SeriesConfig seriesConfig = seriesConfigPerPk.get(seriesPk);
				SeriesInfo seriesInfo = results.get(seriesConfig);
				if (seriesInfo == null) {
					seriesInfo = new SeriesInfo(true, new ArrayList<>());
				}
				seriesInfo.add(new SampleDTO((Date)date, ((Number)value).doubleValue()));
				results.put(seriesConfig, seriesInfo);
			}
		}
		return results;
	}

	protected Object getX(List<?> row) {
		return row.get(4);
	}

	protected Object getSeriesPk(List<?> row) {
		return row.get(1);
	}

	protected Object getValue(List<?> row) {
		return row.get(3);
	}
}
