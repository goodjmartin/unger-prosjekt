package no.goodtech.vaadin.tabs.status;

import no.goodtech.vaadin.tabs.status.common.IStatusLoggerRepository;
import no.goodtech.vaadin.tabs.status.common.StatusIndicatorSubscriptionScheduler;

public class Globals {

    private static IStatusLoggerRepository statusLoggerRepository;
    private static StatusIndicatorSubscriptionScheduler statusIndicatorSubscriptionScheduler;

    public Globals(final IStatusLoggerRepository statusLoggerRepository, final StatusIndicatorSubscriptionScheduler statusIndicatorSubscriptionScheduler) {
        Globals.statusLoggerRepository = statusLoggerRepository;
        Globals.statusIndicatorSubscriptionScheduler = statusIndicatorSubscriptionScheduler;
    }

    public static IStatusLoggerRepository getStatusLoggerRepository() {
        return statusLoggerRepository;
    }

	public static StatusIndicatorSubscriptionScheduler getStatusIndicatorSubscriptionScheduler() {
		return statusIndicatorSubscriptionScheduler;
	}
}
