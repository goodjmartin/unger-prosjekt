package no.goodtech.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.UIEvents;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.ui.UI;

/**
 * Simple utility to create an automatic refresh for a panel
 */
public class Poller {
	
	public interface IPollablePanel extends ClientConnector, PollListener {}

	private static Logger LOGGER = LoggerFactory.getLogger(Poller.class);

	/**
	 * Poll panel at the given interval through {@link PollListener#poll(PollEvent)}
	 * To stop polling, use UI.getCurrent().setPollInterval(-1);
	 * @param panel this panel to reveive a call
	 * @param interval poll interval in ms
	 */
	public static void poll(final IPollablePanel panel, final int interval) {

        final PollListener pollListener = new PollListener() {
            public void poll(PollEvent event) {
                LOGGER.debug("Polling triggered: Accessing runnable, updating panel");
                long before = System.currentTimeMillis();
                panel.poll(event);
                long after = System.currentTimeMillis();
                LOGGER.debug("Poll took: {}ms", after - before);
                LOGGER.debug("Number of pollListeners: " + UI.getCurrent().getListeners(UIEvents.PollEvent.class).size());
            }
        };

        //On attach, add pollListener and enable polling
		panel.addAttachListener(new AttachListener() {
			@Override
			public void attach(AttachEvent event) {
				LOGGER.debug("Attached, adding pollListener and setting pollInterval to {}ms", interval);
				UI.getCurrent().setPollInterval(interval);
				UI.getCurrent().addPollListener(pollListener);
			}
		});

		//On detach, disable polling and remove pollListener
		panel.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				UI.getCurrent().setPollInterval(-1);
				UI.getCurrent().removePollListener(pollListener);
				LOGGER.debug("Detached, disabling polling and removing listener");
			}
		});
	}
}
