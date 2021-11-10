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
public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void testRegisterEventOnEntity() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		//Register one events on owner1
		EventFactory.getEventService().registerEvent(owner1, owner1.getId(), "eventTypeId", "eventDetail");
		validateEventAndType(owner1);
	}

	@Test
	public void testRegisterEventOnOwnerPkOwnerClass() {
		// Create: Owner1 with no events
		EventOwner owner1 = new EventOwner("Owner1").save();

		//Register one events on owner1
		EventFactory.getEventService().registerEvent(owner1.getClass(), owner1.getPk(), owner1.getId(), "eventTypeId", "eventDetail");
		validateEventAndType(owner1);
	}

	private void validateEventAndType(EventOwner owner1) {
		//Validate: one event registered
		Event event = new EventFinder().find();
		Assert.assertEquals("eventDetail", event.getEventDetail());
		Assert.assertEquals(owner1.getPk(), event.getOwnerPk());
		Assert.assertEquals(owner1.getClass(), event.getOwnerClass());
		Assert.assertEquals(owner1.getId(), event.getChangedBy());
		Assert.assertEquals("eventTypeId", event.getEventType().getId());

		//Validate: one eventType registered
		EventType eventType = new EventTypeFinder().find();
		Assert.assertEquals("eventTypeId", eventType.getId());
		Assert.assertEquals(owner1.getClass(), eventType.getOwnerClass());
	}

}
