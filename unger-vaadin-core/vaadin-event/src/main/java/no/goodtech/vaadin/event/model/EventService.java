package no.goodtech.vaadin.event.model;


import no.goodtech.persistence.entity.EntityStub;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service klasse for registrering av hendelser
 */
public class EventService implements IEventService {

	public EventService(){

	}

	/**
	 * Registrerer ny hendelse og hendelsestype, hvis det ikke finnes en hendelsestype med eventTypeId og owner allerede.
	 * Ellers blir eksisterende hendelsestype brukt for hendelsen
	 *
	 * @param owner       Entiteten som eier hendelsen og hendelsestypen
	 * @param changedBy   Hvem/Hva som opprettet hendelsen
	 * @param eventTypeId ID for hendelsestypen(opprettes hvis den ikke allerede eksisterer)
	 * @param eventDetail beskrivelse av hendelsen
	 * @return Den lagrede hendelsen
	 */
	@Transactional
	public Event registerEvent(final EntityStub owner, final String changedBy, final String eventTypeId, final String eventDetail) {
		// Return or create event type
		EventType eventType = createOrReturnEventType(owner.getClass(), eventTypeId);

		// Create event
		return new Event(owner.getClass(), owner.getPk(), changedBy, eventType, eventDetail, new Date()).save();
	}

	/**
	 * Registrerer ny hendelse og hendelsestype, hvis det ikke finnes en hendelsestype med eventTypeId og owner allerede.
	 * Ellers blir eksisterende hendelsestype brukt for hendelsen
	 *
	 * @param ownerPk     pk til entiten som eier hendelsen
	 * @param ownerClass  Klasse-typen som eier hendelsestypen, kombinert med ownerPk bestemmer det eier av hendelse
	 * @param changedBy   Hvem/Hva som opprettet hendelsen
	 * @param eventTypeId ID for hendelsestypen(opprettes hvis den ikke allerede eksisterer)
	 * @param eventDetail beskrivelse av hendelsen
	 * @return Den lagrede hendelsen
	 */
	@Transactional
	public Event registerEvent(final Class<?> ownerClass, final Long ownerPk, final String changedBy, final String eventTypeId, final String eventDetail) {
		// Return or create event type
		EventType eventType = createOrReturnEventType(ownerClass, eventTypeId);

		// Create event
		return new Event(ownerClass, ownerPk, changedBy, eventType, eventDetail, new Date()).save();
	}


	/**
	 * Return existing or create new event type if not found
	 *
	 * @param ownerClass  Owner class
	 * @param eventTypeId Event type
	 * @return Existing or new event type
	 */
	private EventType createOrReturnEventType(final Class<?> ownerClass, final String eventTypeId) {
		// Check if event type exists
		EventType eventType = new EventTypeFinder().setOwnerClass(ownerClass).setId(eventTypeId).find();

		// If not found, create new event type
		if (eventType == null) {
			eventType = new EventType(ownerClass, eventTypeId).save();
		}

		return eventType;
	}

	/**
	 * Remove all events for the specified entity
	 */
	@Transactional
	public void deleteAllEvents(final EntityStub entity) {
		List<Event> events = new EventFinder().setOwner(entity).list();
		if (events != null) {
			for (Event event : events) {
				event.delete();
			}
		}
	}
}
