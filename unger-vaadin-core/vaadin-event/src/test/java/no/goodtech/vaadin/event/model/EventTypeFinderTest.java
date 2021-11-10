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

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class EventTypeFinderTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Test
	public void testFindEventTypesByOwnerClass() {
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

		// Validate: one eventType with id eventCreation
		Assert.assertEquals(1, new EventTypeFinder().setId("eventCreation").list().size());

		//Validate: Fields of eventType
		EventType eventTypeFieldTest = new EventTypeFinder().setId("eventCreation").find();
		Assert.assertEquals(eventTypeFieldTest.getOwnerClass(), EventOwner.class);
		Assert.assertEquals(eventTypeFieldTest.getId(), "eventCreation");

		// Validate: one eventType with id randomEventType
		Assert.assertEquals(1, new EventTypeFinder().setId("randomEventType").list().size());

		// Validate: Two eventTypes with ownerclass equaling user
		Assert.assertEquals(2, new EventTypeFinder().setOwnerClass(EventOwner.class).list().size());

		// Validate: Two eventTypes in total
		Assert.assertEquals(2, new EventTypeFinder().list().size());

		//Registering event on eventType for test-purposes
		EventType eventType = new EventTypeFinder().setId("eventCreation").find();
		eventService.registerEvent(eventType, eventType.getId(), "randomEventType", "randomEventType1");

		// Validate: two eventTypes with id randomEventType
		Assert.assertEquals(2, new EventTypeFinder().setId("randomEventType").list().size());

		//Validate: One eventype with id randomEventType and Owner as ownerclass
		Assert.assertEquals(1, new EventTypeFinder().setId("randomEventType").setOwnerClass(EventOwner.class).list().size());

		//Validate: One eventype with id randomEventType and EventType as ownerclass
		Assert.assertEquals(1, new EventTypeFinder().setId("randomEventType").setOwnerClass(EventType.class).list().size());
	}

}
