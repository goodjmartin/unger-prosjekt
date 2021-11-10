package no.goodtech.vaadin.exception;

import com.vaadin.ui.Notification;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.IErrorNotifier;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a generic Vaadin exception handler that will handle all uncaught exceptions propagated to the
 * Vaadin framework an display relevant information to the user.
 *
 * @author bakke
 */
public class UncaughtExceptionHandler implements ErrorHandler {

	private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);
	private final IErrorNotifier errorNotifier;
	private final String optimisticLockExceptionMessage = ApplicationResourceBundle.getInstance("vaadin-core").getString("uncaughtExceptionHandler.optimisticLockExceptionMessage");

    public UncaughtExceptionHandler(final IErrorNotifier errorNotifier) {
		this.errorNotifier = errorNotifier;
    }

    /**
     * This method is called by Vaadin when an uncaught exception is propagated to the Vaadin framework
     *
     * @param event The error event
     */
	@Override
	public void error(final ErrorEvent event) {
		handleError(event.getThrowable());
	}

	/**
	 * Logger og viser feilmelding og stacktrace 
	 * @param throwable feilen som skjedde
	 */
	private void handleError(final Throwable throwable) {
		logger.error(throwable.getMessage(), throwable);

		// Find root cause of exception
		Throwable rootCause = throwable, cause = throwable.getCause();
		while (cause != null) {
			rootCause = cause;
			cause = cause.getCause();
		}

		if (rootCause instanceof StaleObjectStateException) {
			// Show human readable message for StaleObjectStateException
			Notification.show(optimisticLockExceptionMessage, Notification.Type.WARNING_MESSAGE);
		} else {
			// Show the pop-up window stack trace
			errorNotifier.showUnhandledExceptions(throwable);
		}

	}
}
