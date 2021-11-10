package no.goodtech.vaadin.attachment.model;

import no.goodtech.persistence.MainConfig;
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
public class AttachmentFinderTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Test
	public void testFindAttachmentsByOwnerAndGeneratedEvent() {
		// Create: Owner1 with two attachments
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();

		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName2", "randomFileDescription2", "randomFilePath2");

		// Create: Owner2 with no attachments
		AttachmentOwner owner2 = new AttachmentOwner("Owner2").save();

		// Create: Owner3 with one attachment
		AttachmentOwner owner3 = new AttachmentOwner("Owner2").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner3, owner3.getId(), "randomFileName3", "randomFileDescription3", "randomFilePath3");

		// Validate: Two attachments on owner1
		List<Attachment> attachments = new AttachmentFinder().setOwner(owner1).orderByCreated(true).loadList();
		Assert.assertEquals(2, attachments.size());

		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileDescription2", attachments.get(1).getFileDescription());

		Assert.assertEquals(AttachmentOwner.class, attachments.get(0).getOwnerClass());
		Assert.assertEquals(AttachmentOwner.class, attachments.get(1).getOwnerClass());

		Assert.assertEquals(owner1.getPk(), attachments.get(0).getOwnerPk());
		Assert.assertEquals(owner1.getPk(), attachments.get(1).getOwnerPk());

		Assert.assertEquals(owner1.getId(), attachments.get(0).getChangedBy());
		Assert.assertEquals(owner1.getId(), attachments.get(1).getChangedBy());

		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());
		Assert.assertEquals("randomFilePath2", attachments.get(1).getFilePath());

		// Validate: No attachments on owner2
		Assert.assertFalse(new AttachmentFinder().setOwner(owner2).exists());

		//Validate: Generated events for registered attachments
//		EventFinder eventFinder = new EventFinder();
//		List<Event> generatedEvents = eventFinder.setOwner(owner1).setOwnerClass(AttachmentOwner.class).setEventType("Vedlegg lagt til").orderByCreated(true).list();
//		Assert.assertEquals(2, generatedEvents.size());
//		Assert.assertEquals(owner1.getPk(), generatedEvents.get(0).getOwnerPk());
//		Assert.assertEquals(owner1.getPk(), attachments.get(1).getOwnerPk());

		//Validate: Two attachments for owner1's pk
		attachments = new AttachmentFinder().setOwnerPk(owner1.getPk()).orderByCreated(true).loadList();
		Assert.assertEquals(2, attachments.size());

		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileDescription2", attachments.get(1).getFileDescription());

		Assert.assertEquals(AttachmentOwner.class, attachments.get(0).getOwnerClass());
		Assert.assertEquals(AttachmentOwner.class, attachments.get(1).getOwnerClass());

		Assert.assertEquals(owner1.getPk(), attachments.get(0).getOwnerPk());
		Assert.assertEquals(owner1.getPk(), attachments.get(1).getOwnerPk());

		Assert.assertEquals(owner1.getId(), attachments.get(0).getChangedBy());
		Assert.assertEquals(owner1.getId(), attachments.get(1).getChangedBy());

		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());
		Assert.assertEquals("randomFilePath2", attachments.get(1).getFilePath());

		// Validate: Three attachments with valueClass owner
		attachments = new AttachmentFinder().setOwnerClass(owner1.getClass()).orderByCreated(true).loadList();
		Assert.assertEquals(3, attachments.size());

		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileDescription2", attachments.get(1).getFileDescription());
		Assert.assertEquals("randomFileDescription3", attachments.get(2).getFileDescription());

		Assert.assertEquals(AttachmentOwner.class, attachments.get(0).getOwnerClass());
		Assert.assertEquals(AttachmentOwner.class, attachments.get(1).getOwnerClass());
		Assert.assertEquals(AttachmentOwner.class, attachments.get(2).getOwnerClass());

		Assert.assertEquals(owner1.getPk(), attachments.get(0).getOwnerPk());
		Assert.assertEquals(owner1.getPk(), attachments.get(1).getOwnerPk());
		Assert.assertEquals(owner3.getPk(), attachments.get(2).getOwnerPk());

		Assert.assertEquals(owner1.getId(), attachments.get(0).getChangedBy());
		Assert.assertEquals(owner1.getId(), attachments.get(1).getChangedBy());
		Assert.assertEquals(owner2.getId(), attachments.get(2).getChangedBy());

		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());
		Assert.assertEquals("randomFilePath2", attachments.get(1).getFilePath());
		Assert.assertEquals("randomFilePath3", attachments.get(2).getFilePath());
	}

	@Test
	public void testFindAttachmentsForAllOwners() {
		// Create: Owner1 with attachment
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");

		// Create: Owner2 with attachment
		AttachmentOwner owner2 = new AttachmentOwner("Owner2").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName2", "randomFileDescription2", "randomFilePath2");

		// Validate attachments on owner1 and owner2
		List<Attachment> attachments = new AttachmentFinder().setOwnerClass(AttachmentOwner.class).orderByCreated(true).loadList();
		Assert.assertEquals(2, attachments.size());
		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileDescription2", attachments.get(1).getFileDescription());
	}

	@Test
	public void testFindAttachmentsByFileDescription() {
		// Create: Owner1 with attachment
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");

		// Create: Owner2 with attachment
		AttachmentOwner owner2 = new AttachmentOwner("Owner2").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName2", "randomFileDescription2", "randomFilePath2");
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName3", "randomFileDescription2", "randomFilePath3");

		// Validate: One attachment with fileDescription 'randomFileDescription'
		List<Attachment> attachments = new AttachmentFinder().setFileDescription("randomFileDescription").loadList();
		Assert.assertEquals(1, attachments.size());
		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileName", attachments.get(0).getFileName());
		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());

		// Validate: Two different attachments with fileDescription 'randomFileDescription2'
		attachments = new AttachmentFinder().setFileDescription("randomFileDescription2").orderByCreated(true).loadList();
		Assert.assertEquals(2, attachments.size());

		Assert.assertEquals("randomFileDescription2", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileName2", attachments.get(0).getFileName());
		Assert.assertEquals("randomFilePath2", attachments.get(0).getFilePath());

		Assert.assertEquals("randomFileDescription2", attachments.get(1).getFileDescription());
		Assert.assertEquals("randomFileName3", attachments.get(1).getFileName());
		Assert.assertEquals("randomFilePath3", attachments.get(1).getFilePath());
	}

	@Test
	public void testFindAttachmentsByFilePath() {
		// Create: Owner1 with attachment
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath");

		// Create: Owner2 with attachment
		AttachmentOwner owner2 = new AttachmentOwner("Owner2").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName2", "randomFileDescription2", "randomFilePath2");
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName3", "randomFileDescription3", "randomFilePath2");
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName4", "randomFileDescription4", "randomFilePath3");

		// Validate: One attachment with filePath 'randomFilePath2'
		List<Attachment> attachments = new AttachmentFinder().setFilePath("randomFilePath").loadList();
		Assert.assertEquals(1, attachments.size());
		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFileName", attachments.get(0).getFileName());
		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());

		// Validate: Two different attachments with filePath 'randomFilePath2'
		attachments = new AttachmentFinder().setFilePath("randomFilePath").loadList();
		Assert.assertEquals(1, attachments.size());

		Assert.assertEquals("randomFileName", attachments.get(0).getFileName());
		Assert.assertEquals("randomFileDescription", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFilePath", attachments.get(0).getFilePath());

		// Validate: One attachment with filePath 'randomFilePath3'
		attachments = new AttachmentFinder().setFilePath("randomFilePath2").orderByCreated(true).loadList();
		Assert.assertEquals(2, attachments.size());

		Assert.assertEquals("randomFileName2", attachments.get(0).getFileName());
		Assert.assertEquals("randomFileDescription2", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFilePath2", attachments.get(0).getFilePath());

		Assert.assertEquals("randomFileName3", attachments.get(1).getFileName());
		Assert.assertEquals("randomFileDescription3", attachments.get(1).getFileDescription());
		Assert.assertEquals("randomFilePath2", attachments.get(1).getFilePath());

		// Validate: Two different attachments with fileDescription 'randomFileDescription2'
		attachments = new AttachmentFinder().setFilePath("randomFilePath3").loadList();
		Assert.assertEquals(1, attachments.size());
		Assert.assertEquals("randomFileName4", attachments.get(0).getFileName());
		Assert.assertEquals("randomFileDescription4", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFilePath3", attachments.get(0).getFilePath());
	}

	@Test
	public void testFindAttachmentsByFilePathFileDescriptionAndOwner() {
		// Create: Owner1 with attachment
		AttachmentOwner owner1 = new AttachmentOwner("Owner1").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner1, owner1.getId(), "randomFileName", "randomFileDescription", "randomFilePath3");

		// Create: Owner2 with attachment
		AttachmentOwner owner2 = new AttachmentOwner("Owner2").save();
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName2", "randomFileDescription2", "randomFilePath2");
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName2", "randomFileDescription3", "randomFilePath2");
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName4", "randomFileDescription2", "randomFilePath3");
		AttachmentFactory.getAttachmentService().registerAttachment(owner2, owner2.getId(), "randomFileName5", "randomFileDescription3", "randomFilePath2");

		// Validate: One attachment with filePath 'randomFilePath2', description 'randomFileDescription2' and owner 'owner2'
		List<Attachment> attachments = new AttachmentFinder().setOwner(owner2).setFilePath("randomFilePath2").setFileDescription("randomFileDescription2").loadList();
		Assert.assertEquals(1, attachments.size());
		Assert.assertEquals("randomFileName2", attachments.get(0).getFileName());
		Assert.assertEquals("randomFileDescription2", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFilePath2", attachments.get(0).getFilePath());

		// Validate: no attachment with filePath 'randomFilePath2', description 'randomFileDescription2' and owner 'owner1'
		attachments = new AttachmentFinder().setOwner(owner1).setFilePath("randomFilePath2").setFileDescription("randomFileDescription2").loadList();
		Assert.assertEquals(0, attachments.size());

		// Validate: Two attachments with filePath 'randomFilePath2', description 'randomFileDescription3' and owner 'owner2'
		attachments = new AttachmentFinder().setOwner(owner2).setFilePath("randomFilePath2").setFileDescription("randomFileDescription3").loadList();
		Assert.assertEquals(2, attachments.size());
		Assert.assertEquals("randomFileName2", attachments.get(0).getFileName());
		Assert.assertEquals("randomFileDescription3", attachments.get(0).getFileDescription());
		Assert.assertEquals("randomFilePath2", attachments.get(0).getFilePath());

		Assert.assertEquals(2, attachments.size());
		Assert.assertEquals("randomFileName5", attachments.get(1).getFileName());
		Assert.assertEquals("randomFileDescription3", attachments.get(1).getFileDescription());
		Assert.assertEquals("randomFilePath2", attachments.get(1).getFilePath());
	}
}

