package no.goodtech.vaadin.tabs.status.model;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import no.goodtech.persistence.MainConfig;
import no.goodtech.vaadin.tabs.status.Globals;
import no.goodtech.vaadin.tabs.status.common.IStatusLogger;
import no.goodtech.vaadin.tabs.status.common.StateType;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true) // Rollback fails whenever this is false
@Transactional
@ContextConfiguration(classes = MainConfig.class, locations={"classpath*:goodtech-server.xml"})
public class StatusLogEntryFinderTest {

    private volatile StatusIndicator statusIndicator2;

    @Test
	public void testLoadSingleEntity() {
        // Create test data
        createTestData();

        // Load first entity by message
        StatusLogEntry statusLogEntry = new StatusLogEntryFinder().setMessage("Message A", false).load();
		Assert.assertNotNull(statusLogEntry);
		Assert.assertEquals("Message A", statusLogEntry.getMessage());
		Assert.assertEquals(StateType.SUCCESS, statusLogEntry.getStateType());
		Assert.assertEquals("id1", statusLogEntry.getStatusIndicator().getId());

		// Load second entity by message
        statusLogEntry = new StatusLogEntryFinder().setMessage("Message AA", true).load();
		Assert.assertNotNull(statusLogEntry);
        Assert.assertEquals("Message AA", statusLogEntry.getMessage());
        Assert.assertEquals(StateType.WARNING, statusLogEntry.getStateType());
        Assert.assertEquals("id2", statusLogEntry.getStatusIndicator().getId());

		// Load third entity by message
        statusLogEntry = new StatusLogEntryFinder().setMessage("Message B", true).load();
		Assert.assertNotNull(statusLogEntry);
        Assert.assertEquals("Message B", statusLogEntry.getMessage());
        Assert.assertEquals(StateType.SUCCESS, statusLogEntry.getStateType());
        Assert.assertEquals("id2", statusLogEntry.getStatusIndicator().getId());

		// Load forth (non-existing) entity
        try {
            new StatusLogEntryFinder().setMessage("Message 4", true).load();
            Assert.assertFalse(true);      // Should never get here
        } catch (RuntimeException ignored) {
        }
    }

    @Test
	public void testLoadMultipleEntitiesByMessage() {
        // Create test data
        createTestData();

        // Load first entity by message
        List<StatusLogEntry> statusLogEntries = new StatusLogEntryFinder().setMessage("Message A", true).loadList();
		Assert.assertNotNull(statusLogEntries);
        Assert.assertEquals(2, statusLogEntries.size());

        // Load first entity by message
        Assert.assertEquals("Message A", statusLogEntries.get(0).getMessage());
        Assert.assertEquals(StateType.SUCCESS, statusLogEntries.get(0).getStateType());
        Assert.assertEquals("id1", statusLogEntries.get(0).getStatusIndicator().getId());

		// Load second entity by message
        Assert.assertEquals("Message AA", statusLogEntries.get(1).getMessage());
        Assert.assertEquals(StateType.WARNING, statusLogEntries.get(1).getStateType());
        Assert.assertEquals("id2", statusLogEntries.get(1).getStatusIndicator().getId());
    }

    @Test
	public void testLoadMultipleEntitiesByStateType() {
        // Create test data
        createTestData();

        // Load first entity by message
        List<StatusLogEntry> statusLogEntries = new StatusLogEntryFinder().setStateTypes(StateType.SUCCESS).loadList();
		Assert.assertNotNull(statusLogEntries);
        Assert.assertEquals(2, statusLogEntries.size());

        // Load first entity by message
        Assert.assertEquals("Message A", statusLogEntries.get(0).getMessage());
        Assert.assertEquals(StateType.SUCCESS, statusLogEntries.get(0).getStateType());
        Assert.assertEquals("id1", statusLogEntries.get(0).getStatusIndicator().getId());

		// Load second entity by message
        Assert.assertEquals("Message B", statusLogEntries.get(1).getMessage());
        Assert.assertEquals(StateType.SUCCESS, statusLogEntries.get(1).getStateType());
        Assert.assertEquals("id2", statusLogEntries.get(1).getStatusIndicator().getId());
    }

    @Test
	public void testLoadMultipleEntitiesByStatusIndicator() {
        // Create test data
        createTestData();

        // Load first entity by message
        List<StatusLogEntry> statusLogEntries = new StatusLogEntryFinder().setStatusIndicator(statusIndicator2).loadList();
		Assert.assertNotNull(statusLogEntries);
        Assert.assertEquals(2, statusLogEntries.size());

        // Load first entity by message
        Assert.assertEquals("Message AA", statusLogEntries.get(0).getMessage());
        Assert.assertEquals(StateType.WARNING, statusLogEntries.get(0).getStateType());
        Assert.assertEquals("id2", statusLogEntries.get(0).getStatusIndicator().getId());

		// Load second entity by message
        Assert.assertEquals("Message B", statusLogEntries.get(1).getMessage());
        Assert.assertEquals(StateType.SUCCESS, statusLogEntries.get(1).getStateType());
        Assert.assertEquals("id2", statusLogEntries.get(1).getStatusIndicator().getId());
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
        statusIndicator2 = new StatusIndicator("id2", "name1");
        statusIndicator2 = statusIndicator2.save();
        Assert.assertNotNull(statusIndicator2);

        // Create log entry 1
		StatusLogEntry s1 = new StatusLogEntry("Message A", StateType.SUCCESS, statusIndicator1);
		Assert.assertNotNull(s1.save());

        // Create log entry 2
		s1 = new StatusLogEntry("Message AA", StateType.WARNING, statusIndicator2);
		Assert.assertNotNull(s1.save());

        // Create log entry 2
		s1 = new StatusLogEntry("Message B", StateType.SUCCESS, statusIndicator2);
		Assert.assertNotNull(s1.save());
    }

    /**
     * Test av listCountPerStateType()
     */
    @Test
    public void testListCountPerStateType() {
        StatusIndicator indicator1 = new StatusIndicator("indicator1", null).save();
        StatusIndicator indicator2 = new StatusIndicator("indicator2", null).save();
    	final IStatusLogger logger1 = Globals.getStatusLoggerRepository().lookupStatusLogger("indicator1");
    	final IStatusLogger logger2 = Globals.getStatusLoggerRepository().lookupStatusLogger("indicator2");
    	logger1.failure("urgh");
    	logger1.failure("urgh2");
    	logger1.success("juhu");
    	logger2.success("juhu");
    	logger2.warning("urgh3");
    	
    	final SortedMap<StatusIndicatorStub, Map<StateType, Long>> countPerStateType = new StatusLogEntryFinder().listCountPerStateType();
    	Assert.assertEquals(2, countPerStateType.size());
    	Assert.assertEquals(2, countPerStateType.get(indicator1).get(StateType.FAILURE).intValue());
    	Assert.assertEquals(0, countPerStateType.get(indicator1).get(StateType.WARNING).intValue());
    	Assert.assertEquals(0, countPerStateType.get(indicator1).get(StateType.SUCCESS).intValue()); //SUCCESS is not logged anymore
    	Assert.assertEquals(0, countPerStateType.get(indicator2).get(StateType.FAILURE).intValue());
    	Assert.assertEquals(1, countPerStateType.get(indicator2).get(StateType.WARNING).intValue());
    	Assert.assertEquals(0, countPerStateType.get(indicator2).get(StateType.SUCCESS).intValue()); //SUCCESS is not logged anymore
    	
    	//test sortering på status-indikator-ID
        StatusIndicator anIndicator = new StatusIndicator("anIndicator", null).save();
    	Globals.getStatusLoggerRepository().lookupStatusLogger("anIndicator").failure("først");
    	Assert.assertEquals(anIndicator, new StatusLogEntryFinder().listCountPerStateType().firstKey());
    }

	/**
	 * Test for listStatusIndicatorWithLogEntry()
	 */
	@Test
	public void listStatusIndicatorWithLogEntry() {
		StatusIndicator indicator1 = new StatusIndicator("indicator1", null).save();
		StatusIndicator indicator2 = new StatusIndicator("indicator2", null).save();
		final IStatusLogger logger1 = Globals.getStatusLoggerRepository().lookupStatusLogger("indicator1");
		final IStatusLogger logger2 = Globals.getStatusLoggerRepository().lookupStatusLogger("indicator2");
		logger1.failure("urgh");
		logger1.failure("urgh2");
		logger1.success("juhu");
		logger2.success("juhu");
		logger2.warning("urgh3");

		final List<StatusIndicatorLogEntryDTO> statusIndicatorLogEntryDTOS = new StatusLogEntryFinder().listStatusIndicatorWithLogEntry();
		Assert.assertEquals(2, statusIndicatorLogEntryDTOS.size());
		Assert.assertEquals(2, statusIndicatorLogEntryDTOS.get(0).getFails().intValue());
		Assert.assertEquals(0, statusIndicatorLogEntryDTOS.get(0).getWarnings().intValue());
		Assert.assertEquals(0, statusIndicatorLogEntryDTOS.get(0).getSuccesses().intValue()); //SUCCESS is not logged anymore
		Assert.assertEquals(0, statusIndicatorLogEntryDTOS.get(1).getFails().intValue());
		Assert.assertEquals(1, statusIndicatorLogEntryDTOS.get(1).getWarnings().intValue());
		Assert.assertEquals(0, statusIndicatorLogEntryDTOS.get(1).getSuccesses().intValue()); //SUCCESS is not logged anymore

		//test sortering på status-indikator-ID
		StatusIndicator anIndicator = new StatusIndicator("anIndicator", null).save();
		Globals.getStatusLoggerRepository().lookupStatusLogger("anIndicator").failure("først");
		Assert.assertEquals(anIndicator, new StatusLogEntryFinder().listStatusIndicatorWithLogEntry().get(0).getStatusIndicator());
	}

}
