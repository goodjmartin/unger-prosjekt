package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.event.model.Event;
import no.goodtech.vaadin.event.model.EventFinder;
import no.goodtech.vaadin.event.model.EventType;
import no.goodtech.vaadin.event.model.EventTypeFinder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class AttachmentServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void testRegisterAttachment() {
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");

		// Validate: One attachments on owner1
		List<Attachment> attachments = new AttachmentFinder().setOwner(owner1).loadList();
		Assert.assertEquals(1, attachments.size());
		Assert.assertEquals("randomFileName", attachments.get(0).getFileName());
		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());

		// Validate: One registered event
		List<Event> events = new EventFinder().setOwner(owner1).orderByCreated(true).loadList();
		Assert.assertEquals(1, events.size());
		Assert.assertEquals("randomFileDescription", events.get(0).getEventDetail());
		Assert.assertEquals(owner1.getId(), events.get(0).getChangedBy());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_ADDED, events.get(0).getEventType().getId());

		// Validate: One registered eventType
		List<EventType> eventTypes = new EventTypeFinder().list();
		Assert.assertEquals(1, eventTypes.size());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_ADDED, eventTypes.get(0).getId());
		Assert.assertEquals(AttachmentOwner.class, eventTypes.get(0).getOwnerClass());

	}

	@Test
	public void testRegisterAndEditAttachment() {
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");

		AttachmentFinder attachmentFinder = new AttachmentFinder();
		attachmentFinder.setOwner(owner1);

		// Validate: One attachment on owner1
		Attachment attachment = attachmentFinder.find();
		Assert.assertEquals("randomFileName", attachment.getFileName());
		Assert.assertEquals("randomFileDescription", attachment.getFileDescription());
		Assert.assertEquals("randomFilePath", attachment.getFilePath());

		//Change file description
		AttachmentFactory.getAttachmentService().changeFileDescription(attachment, "randomUserName", "newDescription");
		attachmentFinder = new AttachmentFinder();
		attachmentFinder.setOwner(owner1);

		// Validate: Changed description on the attachment
		attachment = attachmentFinder.find();
		Assert.assertEquals("randomFileName", attachment.getFileName());
		Assert.assertEquals("newDescription", attachment.getFileDescription());
		Assert.assertEquals("randomFilePath", attachment.getFilePath());

		// Validate: two registered events
		EventFinder eventFinder = new EventFinder();
		List<Event> events = eventFinder.setOwner(owner1).orderByCreated(true).loadList();
		Assert.assertEquals(2, events.size());

		Assert.assertEquals("randomFileDescription", events.get(0).getEventDetail());
		Assert.assertEquals(owner1.getId(), events.get(0).getChangedBy());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_ADDED, events.get(0).getEventType().getId());

		Assert.assertEquals("newDescription", events.get(1).getEventDetail());
		Assert.assertEquals("randomUserName", events.get(1).getChangedBy());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_EDITED, events.get(1).getEventType().getId());

		// Validate: Two registered eventTypes
		List<EventType> eventTypes = new EventTypeFinder().list();
		Assert.assertEquals(2, eventTypes.size());

		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_ADDED, eventTypes.get(0).getId());
		Assert.assertEquals(AttachmentOwner.class, eventTypes.get(0).getOwnerClass());

		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_EDITED, eventTypes.get(1).getId());
		Assert.assertEquals(AttachmentOwner.class, eventTypes.get(1).getOwnerClass());

	}

	@Test
	public void testRegisterEditAndDeleteAttachment() {
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");

		AttachmentFinder attachmentFinder = new AttachmentFinder();
		attachmentFinder.setOwner(owner1);

		// Validate: One attachment on owner1
		Attachment attachment = attachmentFinder.find();
		Assert.assertEquals("randomFileName", attachment.getFileName());
		Assert.assertEquals("randomFileDescription", attachment.getFileDescription());
		Assert.assertEquals("randomFilePath", attachment.getFilePath());

		AttachmentFactory.getAttachmentService().changeFileDescription(attachment, "randomUserName", "newDescription");
		attachmentFinder = new AttachmentFinder();
		attachmentFinder.setOwner(owner1);

		// Validate: Changed description on the attachment
		attachment = attachmentFinder.find();
		Assert.assertEquals("randomFileName", attachment.getFileName());
		Assert.assertEquals("newDescription", attachment.getFileDescription());
		Assert.assertEquals("randomFilePath", attachment.getFilePath());

		// Validate: no registered attachment after deleting
		AttachmentFactory.getAttachmentService().deleteAttachment(attachment, "randomUserName2");
		Assert.assertEquals(0, new AttachmentFinder().loadList().size());

		// Validate: three registered events
		EventFinder eventFinder = new EventFinder();
		List<Event> events = eventFinder.setOwner(owner1).orderByCreated(true).loadList();
		Assert.assertEquals(3, events.size());

		Assert.assertEquals("randomFileDescription", events.get(0).getEventDetail());
		Assert.assertEquals(owner1.getId(), events.get(0).getChangedBy());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_ADDED, events.get(0).getEventType().getId());

		Assert.assertEquals("newDescription", events.get(1).getEventDetail());
		Assert.assertEquals("randomUserName", events.get(1).getChangedBy());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_EDITED, events.get(1).getEventType().getId());

		Assert.assertEquals("newDescription", events.get(2).getEventDetail());
		Assert.assertEquals("randomUserName2", events.get(2).getChangedBy());
		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_DELETED, events.get(2).getEventType().getId());

		// Validate: Three registered eventTypes
		List<EventType> eventTypes = new EventTypeFinder().list();
		Assert.assertEquals(3, eventTypes.size());

		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_ADDED, eventTypes.get(0).getId());
		Assert.assertEquals(AttachmentOwner.class, eventTypes.get(0).getOwnerClass());

		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_EDITED, eventTypes.get(1).getId());
		Assert.assertEquals(AttachmentOwner.class, eventTypes.get(1).getOwnerClass());

		Assert.assertEquals(AttachmentService.EVENT_TYPE_ATTACHMENT_DELETED, eventTypes.get(2).getId());
		Assert.assertEquals(AttachmentOwner.class, eventTypes.get(1).getOwnerClass());
	}
}
