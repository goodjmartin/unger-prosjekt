package no.goodtech.vaadin.tabs.status.common;

import no.cronus.common.utils.StackTraceUtils;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorFinder;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementasjonen for en status indikator. Denne klassen holder på siste status og listen over status oppdateringer.
 *
 * <p/>
 * User: bakke
 */
public class StatusLogger implements IStatusLogger {

    private static final int MAX_MESSAGE_LENGTH = 255;
    private static final int MAX_DETAILS_LENGTH = 6144;
	private static final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
    private final StatusIndicatorStub statusIndicator;
	private volatile StatusLogEntry currentStatusLogEntry;

	/**
	 * Opprett en logger for angitt indikator
	 * @param statusIndicator status-indikatoren du vil logge til
	 */
	public StatusLogger(final StatusIndicatorStub statusIndicator) {
        this.statusIndicator = statusIndicator;
	}

	private StatusLogEntry createLogEntry(final String message, StateType stateType, Throwable throwable) {
		String stackTrace = null;
		if (throwable != null)
			stackTrace = getStackTrace(throwable);
		
		statusIndicator.setMessage(message);

		// Lookup status indicator from database (needed for unit tests)
		// - database is wiped out between test executions, while status loggers are still on map in StatusLoggerRepository
		StatusIndicatorStub statusIndicatorFromDb = new StatusIndicatorFinder().setId(statusIndicator.getId()).find();

		currentStatusLogEntry = new StatusLogEntry(getSafeMessage(message), stackTrace, stateType, statusIndicatorFromDb).save();
		return currentStatusLogEntry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void success(final String message) {
		setOk();
		statusIndicator.setMessage(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatusLogEntry warning(final String message) {
		statusIndicator.setOk(false);
        logger.warn(message);
        return createLogEntry(message, StateType.WARNING, null);
	}

    @Override
    public StatusLogEntry warning(String message, Throwable throwable) {
		statusIndicator.setOk(false);
        logger.warn(message);
        return createLogEntry(message, StateType.WARNING, throwable);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public StatusLogEntry failure(final String message) {
		statusIndicator.setOk(false);
        logger.error(message);
        return createLogEntry(message, StateType.FAILURE, null);
	}

    @Override
    public StatusLogEntry failure(String message, Throwable throwable) {
		statusIndicator.setOk(false);
        logger.error(message);
        return createLogEntry(message, StateType.FAILURE, throwable);
    }

    public StatusIndicatorStub getStatusIndicator() {
        return statusIndicator;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public StatusLogEntry getCurrentStatusLogEntry() {
		return currentStatusLogEntry;
	}

    private String getSafeMessage(final String message) {
        return (message != null) ? ((message.length() <= MAX_MESSAGE_LENGTH) ? message : message.substring(0, MAX_MESSAGE_LENGTH - 1)) : "";
    }

    private String getStackTrace(final Throwable throwable) {
		String stackTrace = StackTraceUtils.getStackTrace(throwable);

        return (stackTrace.length() <= MAX_DETAILS_LENGTH) ? stackTrace : stackTrace.substring(0, MAX_DETAILS_LENGTH - 1);
    }

	public void setOk() {
		statusIndicator.setOk(true);
	}
}
