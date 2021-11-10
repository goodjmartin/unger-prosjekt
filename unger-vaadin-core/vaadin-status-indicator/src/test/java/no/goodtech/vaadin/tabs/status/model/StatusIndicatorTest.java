package no.goodtech.vaadin.tabs.status.model;

import no.cronus.common.date.DateTimeFactory;
import no.cronus.common.date.DateTimeUtils;
import no.goodtech.persistence.MainConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@Transactional
@ActiveProfiles("test") // Using IncrementalDateTimeService to control time
@ContextConfiguration(classes = MainConfig.class, locations = {"classpath*:goodtech-server.xml"})
public class StatusIndicatorTest {

	@Test
	public void testSave() {
		// Create the status indicator
		StatusIndicator statusIndicator = new StatusIndicator("id1", "name1");
		statusIndicator = statusIndicator.save();
		Assert.assertNotNull(statusIndicator);
		System.out.println(DateTimeFactory.getService());
	}

	@Test
	public void testDelete() {
		// Create the status indicator
		StatusIndicator statusIndicator = new StatusIndicator("id2", "name2");
		statusIndicator = statusIndicator.save();
		Assert.assertNotNull(statusIndicator);

		// Deleting the first time should succeed
		Assert.assertTrue(statusIndicator.delete());

		// Deleting a second time should fail
		Assert.assertFalse(statusIndicator.delete());
	}

	@Test
	public void checkRunningStatus() {
		StatusIndicator indicator = new StatusIndicator();
		indicator.setLastMuteAt(1L);
		Assert.assertNull(indicator.isOk()); //we don't know yet
		Assert.assertNull(indicator.getLastHeartbeatAt()); //we don't know it's alive yet
		Assert.assertEquals(0, indicator.getNumFailuresSinceMute());
		Assert.assertEquals(1L, DateTimeUtils.toMillis(indicator.getLastMuteAt()));

		indicator.setOk(true); //wake-up-call
		Assert.assertNotNull(indicator.getLastHeartbeatAt()); //now it's alive!
		Assert.assertTrue(indicator.isOk()); //and fine
		Assert.assertEquals(0, indicator.getNumFailuresSinceMute()); //still no failures
		Assert.assertEquals(1L, DateTimeUtils.toMillis(indicator.getLastMuteAt()));

		indicator.setOk(false);
		Assert.assertNotNull(indicator.getLastHeartbeatAt()); //still alive
		Assert.assertFalse(indicator.isOk()); //but not perfect
		Assert.assertEquals(1, indicator.getNumFailuresSinceMute());
		indicator.mute();
		Assert.assertFalse(indicator.isOk()); //last status is still the same
		Assert.assertEquals(0, indicator.getNumFailuresSinceMute()); //error count is reset
		Assert.assertTrue(DateTimeUtils.toMillis(indicator.getLastMuteAt()) > 1L); //mute time has changed

		indicator.setLastHeartbeatAt(2L);
		Assert.assertEquals(2L, DateTimeUtils.toMillis(indicator.getLastHeartbeatAt()));
		indicator.setOk(true);
		Assert.assertTrue(indicator.isOk());
		Assert.assertTrue(DateTimeUtils.toMillis(indicator.getLastHeartbeatAt()) > 2L); //last heartbeat time has changed
	}

}
