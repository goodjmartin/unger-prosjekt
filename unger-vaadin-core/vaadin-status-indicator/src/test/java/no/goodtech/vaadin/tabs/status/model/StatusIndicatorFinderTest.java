package no.goodtech.vaadin.tabs.status.model;

import java.util.List;

import no.goodtech.persistence.MainConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class StatusIndicatorFinderTest {

	@Test
	public void testLoadSingleEntity() {
		// Create test data
		createTestData();

		// Load first entity by id
		StatusIndicator statusIndicator = new StatusIndicatorFinder().setId("id1").load();
		Assert.assertNotNull(statusIndicator);
		Assert.assertEquals("id1", statusIndicator.getId());
		Assert.assertEquals("name1", statusIndicator.getName());

		// Load second entity by id
		statusIndicator = new StatusIndicatorFinder().setId("id2").load();
		Assert.assertNotNull(statusIndicator);
		Assert.assertEquals("id2", statusIndicator.getId());
		Assert.assertEquals("name2", statusIndicator.getName());

		// Load third entity by id
		statusIndicator = new StatusIndicatorFinder().setId("id3").load();
		Assert.assertNotNull(statusIndicator);
		Assert.assertEquals("id3", statusIndicator.getId());
		Assert.assertEquals("name3", statusIndicator.getName());

		// Load forth (non-existing) entity
		try {
			new StatusLogEntryFinder().setMessage("id4", true).load();
			Assert.assertFalse(true);      // Should never get here
		} catch (RuntimeException ignored) {
		}
	}

	@Test
	public void testLoadAll() {
		// Create test data
		createTestData();

		// Load all entities
		List<StatusIndicator> statusIndicators = new StatusIndicatorFinder().loadList();
		Assert.assertNotNull(statusIndicators);
		Assert.assertEquals(3, statusIndicators.size());

		// First entity
		Assert.assertEquals("id1", statusIndicators.get(0).getId());
		Assert.assertEquals("name1", statusIndicators.get(0).getName());

		// Second entity
		Assert.assertEquals("id2", statusIndicators.get(1).getId());
		Assert.assertEquals("name2", statusIndicators.get(1).getName());

		// Third entity
		Assert.assertEquals("id3", statusIndicators.get(2).getId());
		Assert.assertEquals("name3", statusIndicators.get(2).getName());
	}

	@Test
	public void testLoadAllNoData() {
		// Load all entities
		List<StatusLogEntry> statusLogEntries = new StatusLogEntryFinder().loadList();
		Assert.assertNotNull(statusLogEntries);
		Assert.assertEquals(0, statusLogEntries.size());
	}

	private void createTestData() {
		// Create status indicator 1
		StatusIndicator statusIndicator1 = new StatusIndicator("id1", "name1");
		statusIndicator1 = statusIndicator1.save();
		Assert.assertNotNull(statusIndicator1);

		// Create status indicator 2
		StatusIndicator statusIndicator2 = new StatusIndicator("id2", "name2");
		statusIndicator2 = statusIndicator2.save();
		Assert.assertNotNull(statusIndicator2);

		// Create status indicator 3
		StatusIndicator statusIndicator3 = new StatusIndicator("id3", "name3");
		statusIndicator3 = statusIndicator3.save();
		Assert.assertNotNull(statusIndicator3);
	}

}
