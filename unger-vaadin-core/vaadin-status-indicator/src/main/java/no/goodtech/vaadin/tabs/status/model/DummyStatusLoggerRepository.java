package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.vaadin.tabs.status.common.IStatusLogger;
import no.goodtech.vaadin.tabs.status.common.StatusLoggerRepository;

/**
 * Bruk denne hvis du vil fake en statuslogger i en test.
 * Da kan du lure objektet du vil teste til å tro at den logger til en ordentlig statuslogger.
 * @author oystein
 * @deprecated
 */
public class DummyStatusLoggerRepository extends StatusLoggerRepository {

	public DummyStatusLoggerRepository() {
		new no.goodtech.vaadin.tabs.status.Globals(this, null);
	}

	/**
	 * Legg til en dummy-statuslogger med angitt ID
	 * @param id ID til loggeren
	 * @deprecated Not needed anymore, bacause all dummy loggers is now created automatically. Will be removed soon
	 */
	public void addDummyLogger(String id) {
	}
	
	@Override
	public IStatusLogger lookupStatusLogger(String id) {
		return new DummyStatusLogger(id);
	}

	private class DummyStatusLogger implements IStatusLogger {

		final String id;

		/**
		 * Opprett en jukse-statuslogger med angitt ID
		 * @param id ID til status-indikatoren
		 */
		public DummyStatusLogger(String id) {
			this.id = id;
		}
	
		@Override
		public void success(String message) {
			System.out.println(String.format("%s: Success: %s", id, message));
		}
	
		@Override
		public StatusLogEntry warning(String message) {
			return warning(message, null);
		}
	
		@Override
		public StatusLogEntry warning(String message, Throwable throwable) {
			System.out.println(String.format("%s: Warning: %s", id, message));
			return null;
		}
	
		@Override
		public StatusLogEntry failure(String message) {
			return failure(message, null);
		}
	
		@Override
		public StatusLogEntry failure(String message, Throwable throwable) {
			System.out.println(String.format("%s: Failure: %s", id, message));
			return null;
		}
	
		@Override
		public StatusIndicator getStatusIndicator() {
			return new StatusIndicator(id, id);
		}
	
		@Override
		public StatusLogEntry getCurrentStatusLogEntry() {
			return new StatusLogEntry();
		}

		public void setOk() {
			System.out.println(String.format("%s: OK!", id));
		}
	}
}
