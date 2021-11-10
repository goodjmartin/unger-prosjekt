package no.goodtech.vaadin.event.model;

public class EventFactory {

	private static volatile IEventService eventService;

	public EventFactory(final IEventService eventService) {
		EventFactory.eventService = eventService;
	}

	public static IEventService getEventService() {
		return eventService;
	}

}
