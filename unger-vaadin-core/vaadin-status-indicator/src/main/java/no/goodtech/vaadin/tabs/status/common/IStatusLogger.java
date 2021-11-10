package no.goodtech.vaadin.tabs.status.common;

import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;
import no.goodtech.vaadin.tabs.status.model.StatusLogEntry;

/**
 * Denne klassen definerer grensesnittet til StatusIndicator klassen
 * <p/>
 * User: bakke
 */
public interface IStatusLogger {

	/**
	 * Send heartbeat signal to indicator to tell you are alive and satisfied
	 */
	void setOk();
	
	/**
	 * Denne metoden benyttes hvis man ønsker å rapportere om en normal-tilstand
	 * The message will be stored in memory until next message arrives 
	 * {@link #setOk()} will be fired automatically
	 * @param message Melding til operatør
	 */
	public void success(final String message);

	/**
	 * Denne metoden benyttes hvis man ønsker å rapportere om en situasjon som bør sees nærmere på
	 * @param message Melding til operatør
     * @return beskjeden som ble logget
	 */
	public StatusLogEntry warning(final String message);

    /**
     * Denne metoden benyttes hvis man ønsker å rapportere om en situasjon som bør sees nærmere på
     *
     * @param message Melding til operatør
     * @param throwable Throwable
     * @return beskjeden som ble logget
     */
    public StatusLogEntry warning(final String message, final Throwable throwable);

	/**
	 * Denne metoden benyttes hvis man ønsker å rapportere om en feilsituasjon
	 *
	 * @param message Melding til operatør
     * @return beskjeden som ble logget
	 */
	public StatusLogEntry failure(final String message);

    /**
     * Denne metoden benyttes hvis man ønsker å rapportere om en feilsituasjon
     *
     * @param message Melding til operatør
     * @param throwable unntak
     * @return beskjeden som ble logget
     */
    public StatusLogEntry failure(final String message, final Throwable throwable);

    /**
     * @return indikatoren for denne loggeren
     */
    public StatusIndicatorStub getStatusIndicator();

	/**
	 * Denne metoden returnerer siste status-endring
	 *
	 * @return siste status log entry
	 */
	public StatusLogEntry getCurrentStatusLogEntry();

}
