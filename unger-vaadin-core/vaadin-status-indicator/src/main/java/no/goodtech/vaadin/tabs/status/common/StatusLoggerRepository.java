package no.goodtech.vaadin.tabs.status.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.goodtech.vaadin.tabs.status.model.StatusIndicator;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorFinder;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Denne klassen holder på listen over over status indikatorer som et prosjekt ønsker å monitorere
 * <p/>
 * User: bakke
 */
public class StatusLoggerRepository implements IStatusLoggerRepository {

	private static final Logger logger = LoggerFactory.getLogger(StatusLoggerRepository.class);
	private final Map<String, IStatusLogger> statusLoggerMap = new HashMap<>();
	@SuppressWarnings("UnnecessaryBoxing")
	private final Integer LOCK = new Integer(0);

    /**
	 * {@inheritDoc}
	 */
	@Override
	public List<IStatusLogger> getStatusLoggers() {
		for (StatusIndicatorStub indicator : new StatusIndicatorFinder().list()) {
			lookupStatusLogger(indicator.getId());
		}
		return new ArrayList<>(statusLoggerMap.values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatusLogger lookupStatusLogger(final String id) {
		synchronized (LOCK) {
			IStatusLogger statusLogger = statusLoggerMap.get(id);

			if (statusLogger == null) {
				// Find status indicator
				StatusIndicator statusIndicator = new StatusIndicatorFinder().setId(id).find();

				// Create status indicator (if needed)
				if (statusIndicator == null) {
					statusIndicator = new StatusIndicator(id, id).save();
				}

				// Create status logger
				statusLogger = new StatusLogger(statusIndicator);

				logger.info("Registrert status indikator: id=" + statusIndicator.getId());

				// Add status logger to map
				statusLoggerMap.put(id, statusLogger);
			}

			return statusLogger;
		}
	}

}
