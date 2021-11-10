package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.MainConfig;
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
public class StatusLogEntryTest {

    @Test
	public void testSave() {
        // Create the status indicator
        StatusIndicator statusIndicator = new StatusIndicator("id1", "name1");
        statusIndicator = statusIndicator.save();
        Assert.assertNotNull(statusIndicator);

        // Create log entry 1
		StatusLogEntry s1 = new StatusLogEntry("Message 1", StateType.SUCCESS, statusIndicator);
		Assert.assertNotNull(s1.save());
        Assert.assertEquals("Message 1", s1.getMessage());
        Assert.assertEquals(StateType.SUCCESS, s1.getStateType());
        Assert.assertEquals(statusIndicator, s1.getStatusIndicator());
        Assert.assertNull(s1.getDetails());

        // Create log entry 2
		s1 = new StatusLogEntry("Message 2", StateType.WARNING, statusIndicator);
		Assert.assertNotNull(s1.save());
        Assert.assertEquals("Message 2", s1.getMessage());
        Assert.assertEquals(StateType.WARNING, s1.getStateType());
        Assert.assertEquals(statusIndicator, s1.getStatusIndicator());
        Assert.assertNull(s1.getDetails());

        // Create log entry 3
		s1 = new StatusLogEntry("Message 3", StateType.FAILURE, statusIndicator);
        Assert.assertNotNull(s1.save());
        Assert.assertEquals("Message 3", s1.getMessage());
        Assert.assertEquals(StateType.FAILURE, s1.getStateType());
        Assert.assertEquals(statusIndicator, s1.getStatusIndicator());
        Assert.assertNull(s1.getDetails());

        // Create log entry 4
		s1 = new StatusLogEntry("Message 4", "Some cause", StateType.FAILURE, statusIndicator);
		Assert.assertNotNull(s1.save());
        Assert.assertEquals("Message 4", s1.getMessage());
        Assert.assertEquals(StateType.FAILURE, s1.getStateType());
        Assert.assertEquals(statusIndicator, s1.getStatusIndicator());
        Assert.assertEquals("Some cause", s1.getDetails());
	}

}
