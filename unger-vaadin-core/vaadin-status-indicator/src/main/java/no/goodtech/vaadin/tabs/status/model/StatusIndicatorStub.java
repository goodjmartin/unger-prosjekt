package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.entity.EntityStub;

import java.time.LocalDateTime;

public interface StatusIndicatorStub extends EntityStub<StatusIndicator> {

	String getId();

	String getName();
	
	/**
	 * @return true = I am ok now and satisfied. False = last time I did something it went wrong. Null = I don't know yet.
	 */
	Boolean isOk();


	/**
	 * Set heartbeat status
	 * @param ok true = I am ok now and satisfied. False = Last time I did something it went wrong. 
	 * @see #isOk()
	 */
	void setOk(Boolean ok);
	
	/**
	 * @return time when I last updated the ok-flag or message. If ok is null, return null 
	 */
	LocalDateTime getLastHeartbeatAt();
	
	/**
	 * @return num failures happened since last time I was muted
	 */
	long getNumFailuresSinceMute();
	
	/**
	 * @return last time I had to shut up. Shows server start time if I haven't been muted
	 */
	LocalDateTime getLastMuteAt();

	/**
	 * Clear failure count
	 */
	void mute();

	/**
	 * @return last status message
	 */
	String getMessage();
	
	/**
	 * Set new status message
	 */
	void setMessage(String message);
}
