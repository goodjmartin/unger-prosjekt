package no.goodtech.vaadin.tabs.status;

import no.goodtech.vaadin.tabs.status.common.IStatusLogger;
import no.goodtech.vaadin.tabs.status.common.StateType;

import java.util.concurrent.*;

public class TestStatusIndicator {

    public static final int NUMBER_OF_INDICATORS = 10;

	public TestStatusIndicator() {
		// Lookup the status indicators
/*
		final List<IStatusLogger> statusLoggers = new ArrayList<IStatusLogger>();
		for (int indicatorIndex = 1; indicatorIndex <= NUMBER_OF_INDICATORS; indicatorIndex++) {
            statusLoggers.add(Globals.getStatusIndicatorRepository().lookupStatusLogger("id" + indicatorIndex));
		}
*/

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		final Runnable beeper = new Runnable() {
			int indicatorIndex = 1;
            StateType stateType = StateType.WARNING;
            IStatusLogger previousStatusLogger = null;
            public void run() {
                // Reset previous status update
                if (previousStatusLogger != null) {
                    previousStatusLogger.success("A SUCCESS reported on: " + previousStatusLogger.getStatusIndicator().getName());
                }

                previousStatusLogger = Globals.getStatusLoggerRepository().lookupStatusLogger("id" + indicatorIndex);

				if (stateType.equals(StateType.WARNING)) {
                    previousStatusLogger.warning("A WARNING reported on: " + previousStatusLogger.getStatusIndicator().getName() + ". Just some random text to make the text not fit the available space allocated for the message text field.");
				} else if (stateType.equals(StateType.FAILURE)) {
                    previousStatusLogger.failure("A FAILURE reported on: " + previousStatusLogger.getStatusIndicator().getName(), new Throwable("Some cause"));
				}

				indicatorIndex = (indicatorIndex < NUMBER_OF_INDICATORS) ? indicatorIndex + 1 : 1;
                stateType = stateType.equals(StateType.WARNING) ? StateType.FAILURE : StateType.WARNING;
			}
		};

		scheduler.scheduleAtFixedRate(beeper, 5, 5, TimeUnit.SECONDS);

	}

}
