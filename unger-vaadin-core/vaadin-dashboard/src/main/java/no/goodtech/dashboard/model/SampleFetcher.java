package no.goodtech.dashboard.model;


import java.util.Date;
import java.util.List;

import no.cronus.common.utils.CollectionFactory;
import no.goodtech.persistence.PersistenceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleFetcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String dataSource;

	public SampleFetcher(final String dataSource) {
		this.dataSource = dataSource;
	}

	public List<SampleDTO> getQueryResult(final String expression) {

		// Execute DB query
		List<List<?>> rows = PersistenceFactory.getQueryExecutor(dataSource).execute(expression);

		// Construct an empty result list
		List<SampleDTO> result = CollectionFactory.getArrayList();

		// Add sample values to result list
		for (List<?> row : rows) {
			try {
				Object value = row.get(1);
				Object date = row.get(0);
				if (value != null && date != null)
					result.add(new SampleDTO((Date)date, ((Number)value).doubleValue()));
			} catch (Exception e) {
				logger.warn("Exception during conversion of date / value. Ignoring sample.");
			}
		}
		return result;
	}

}
