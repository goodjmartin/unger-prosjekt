package no.goodtech.vaadin.event.model;

import no.goodtech.persistence.MainConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class EventFinderTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void testFindEventsByOwner() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		// Create: Owner2 with no events
		EventOwner owner2 = new EventOwner("Owner2").save();

		//Create: Owner3 with no events
		EventOwner owner3 = new EventOwner("Owner3").save();

		//Register Three events on owner1
		IEventService eventService = EventFactory.getEventService();
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation1");
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation2");
		eventService.registerEvent(owner1, owner1.getId(), "randomEventType", "randomEventType1");
		//Register one event on owner2
		eventService.registerEvent(owner2, owner2.getId(), "randomEventType", "randomEventType1");

		// Validate: No event on owner3
		Assert.assertEquals(0, new EventFinder().setOwner(owner3).list().size());

		// Validate: One event on owner2
		Assert.assertEquals(1, new EventFinder().setOwner(owner2).list().size());

		//Validate: Fields of the event
		Event event = new EventFinder().setOwner(owner2).find();
		Assert.assertEquals(event.getChangedBy(), owner2.getId());
		Assert.assertEquals(event.getEventDetail(), "randomEventType1");
		Assert.assertEquals(event.getOwnerClass(), EventOwner.class);
		Assert.assertEquals(event.getEventType(), new EventTypeFinder().setOwnerClass(EventOwner.class).setId("randomEventType").find());
		Assert.assertEquals(event.getOwnerPk(), owner2.getPk());

		//Validate: One event on owner2 filtering on ownerPk and Class
		Assert.assertEquals(1, new EventFinder().setOwnerPk(owner2.getPk()).setOwnerClass(owner2.getClass()).list().size());

		// Validate: Three events on owner1
		Assert.assertEquals(3, new EventFinder().setOwner(owner1).list().size());

		//Validate: Four events in total
		Assert.assertEquals(4, new EventFinder().list().size());

		//Validate: One event changed by owner2
		Assert.assertEquals(1, new EventFinder().setChangedBy(owner2.getId()).list().size());

		//Validate: Three events changed by owner1
		Assert.assertEquals(3, new EventFinder().setChangedBy(owner1.getId()).list().size());

	}

	@Test
	public void testFindEventsByEventType() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		// Create: Owner2 with no events
		EventOwner owner2 = new EventOwner("Owner2").save();

		//Register Three events on owner1
		IEventService eventService = EventFactory.getEventService();
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation1");
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation2");
		eventService.registerEvent(owner1, owner1.getId(), "randomEventType", "randomEventType1");
		//Register one event on owner2
		eventService.registerEvent(owner2, owner1.getId(), "randomEventType", "randomEventType1");

		//Validate: Two events of type eventCreation
		Assert.assertEquals(2, new EventFinder().setEventType("eventCreation").list().size());

		//Validate: Two events of type randomEventType
		Assert.assertEquals(2, new EventFinder().setEventType("randomEventType").list().size());

	}

	@Test
	public void testFindEventsByEventDetail() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		// Create: Owner2 with no events
		EventOwner owner2 = new EventOwner("Owner2").save();

		//Register Three events on owner1
		IEventService eventService = EventFactory.getEventService();
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation1");
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation2");
		eventService.registerEvent(owner1, owner1.getId(), "randomEventType", "randomEventType1");
		//Register one event on owner2
		eventService.registerEvent(owner2, owner2.getId(), "randomEventType", "randomEventType1");

		//Validate: One event for each eventDetail filtering on detail and owner
		Assert.assertEquals("eventCreation1", new EventFinder().setOwner(owner1).setEventDetail("eventCreation1").find().getEventDetail());
		Assert.assertEquals("eventCreation2", new EventFinder().setOwner(owner1).setEventDetail("eventCreation2").find().getEventDetail());
		Assert.assertEquals("randomEventType1", new EventFinder().setOwner(owner1).setEventDetail("randomEventType1").find().getEventDetail());
		Assert.assertEquals("randomEventType1", new EventFinder().setOwner(owner2).setEventDetail("randomEventType1").find().getEventDetail());

		//Validate: two events with identical eventDetail filtering on detail only
		Assert.assertEquals(2, new EventFinder().setEventDetail("randomEventType1").list().size());

		//Validate: one event for each of the other eventDetails
		Assert.assertEquals(1, new EventFinder().setEventDetail("eventCreation1").list().size());
		Assert.assertEquals(1, new EventFinder().setEventDetail("eventCreation2").list().size());
	}

	@Test
	public void testFindEventsByOwnerDetailAndType() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		// Create: Owner2 with no events
		EventOwner owner2 = new EventOwner("Owner2").save();

		//Register four events on owner1
		IEventService eventService = EventFactory.getEventService();
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation1");
		eventService.registerEvent(owner1, owner1.getId(), "eventCreation", "eventCreation2");
		eventService.registerEvent(owner1, owner1.getId(), "randomEventType", "randomEventType1");
		eventService.registerEvent(owner1, owner1.getId(), "randomEventType", "randomEventType2");

		//Register one event on owner2
		eventService.registerEvent(owner2, owner2.getId(), "randomEventType", "randomEventType1");

		//Validate: one event for owner1 with type randomEventType and detail randomEventType1
		Assert.assertEquals(1, new EventFinder().setEventType("randomEventType").setOwner(owner1).setEventDetail("randomEventType1").list().size());

		//Validate: 2 events for eventType eventCreation
		EventTypeFinder eventTypeFinder = new EventTypeFinder();
		List<EventType> eventTypes = eventTypeFinder.setId("eventCreation").list();
		Assert.assertEquals(2, new EventFinder().setEventTypes(eventTypes.toArray()).loadList().size());

		//Validate: 3 events for eventType randomEventType
		eventTypeFinder = new EventTypeFinder();
		eventTypes = eventTypeFinder.setId("randomEventType").list();
		Assert.assertEquals(3, new EventFinder().setEventTypes(eventTypes.toArray()).loadList().size());

		//Validate: 5 events filtered on both eventTypes
		eventTypeFinder = new EventTypeFinder();
		eventTypes = eventTypeFinder.setIds("randomEventType", "eventCreation").list();
		Assert.assertEquals(5, new EventFinder().setEventTypes(eventTypes.toArray()).loadList().size());

		//Validate: 4 events for owner1
		Assert.assertEquals(4, new EventFinder().setSources(new String[]{owner1.getId()}).loadList().size());

		//Validate: 1 event for owner2
		Assert.assertEquals(1, new EventFinder().setSources(new String[]{owner2.getId()}).loadList().size());

		//Validate: no events created after new date
		try {Thread.sleep(100L);} catch (InterruptedException ignored) {}
		Assert.assertEquals(0, new EventFinder().setCreatedAfter(new Date()).loadList().size());

		//Validate: 5 events created before new date
		Assert.assertEquals(5, new EventFinder().setCreatedBefore(new Date()).loadList().size());
	}

	@Test
	public void testFindEventsByDate() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		// Create: Owner2 with no events
		EventOwner owner2 = new EventOwner("Owner2").save();

		//Register four events on owner1
		IEventService eventService = EventFactory.getEventService();
		eventService.registerEvent(owner1, owner1.getId(), "eventTypeId", "eventDetail1");
		try {Thread.sleep(10L);} catch (InterruptedException ignored) {}
		eventService.registerEvent(owner1, owner1.getId(), "eventTypeId", "eventDetail2");
		try {Thread.sleep(10L);} catch (InterruptedException ignored) {}
		eventService.registerEvent(owner1, owner1.getId(), "eventTypeId", "eventDetail3");
		try {Thread.sleep(10L);} catch (InterruptedException ignored) {}
		eventService.registerEvent(owner1, owner1.getId(), "eventTypeId", "eventDetail4");
		try {Thread.sleep(10L);} catch (InterruptedException ignored) {}
		//Register one event on owner2
		eventService.registerEvent(owner2, owner2.getId(), "eventTypeId", "eventDetail5");

		//Validate: events in order
		List<Event> events = new EventFinder().orderByCreated(true).list();
		Assert.assertEquals(5, events.size());
		Assert.assertEquals("eventDetail1", events.get(0).getEventDetail());
		Assert.assertEquals("eventDetail2", events.get(1).getEventDetail());
		Assert.assertEquals("eventDetail3", events.get(2).getEventDetail());
		Assert.assertEquals("eventDetail4", events.get(3).getEventDetail());
		Assert.assertEquals("eventDetail5", events.get(4).getEventDetail());

		events = new EventFinder().orderByCreated(false).list();
		Assert.assertEquals(5, events.size());
		Assert.assertEquals("eventDetail1", events.get(4).getEventDetail());
		Assert.assertEquals("eventDetail2", events.get(3).getEventDetail());
		Assert.assertEquals("eventDetail3", events.get(2).getEventDetail());
		Assert.assertEquals("eventDetail4", events.get(1).getEventDetail());
		Assert.assertEquals("eventDetail5", events.get(0).getEventDetail());
	}
}
