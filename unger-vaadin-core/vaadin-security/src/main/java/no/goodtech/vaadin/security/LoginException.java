package no.goodtech.vaadin.security;

import no.goodtech.vaadin.main.ApplicationResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Brukes for å angi hva som har gått galt ved et innloggingsforsøk
 * @author oystein
 */
public class LoginException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger(LoginException.class);
	private static final String MESSAGE_UNKNOWN_USERNAME = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.failure.unknownUserName");
	private static final String MESSAGE_WRONG_PASSWORD = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.failure.wrongPassword");
	private static final String MESSAGE_UNKNOWN_NO_MORE_ATTEMPTS = ApplicationResourceBundle.getInstance("vaadin-security").getString("loginPanel.failure.noMoreAttempts");

    /**
	 * Årsak til hvorfor du ikke fikk logget deg inn
	 */
	public enum ErrorCode {
		
		/**
		 * Fant ingen bruker med dette navnet 
		 */
		UNKNOWN_USERNAME,
		
		/**
		 * Feil passord
		 */
		WRONG_PASSWORD,
		
		/**
		 * Du har sluppet opp for flere innloggingsforsøk
		 */
		NO_MORE_ATTEMPTS
	}
	
	private ErrorCode errorCode;
	private Integer numRemainingAttempts = null;
	
	/**
	 * Opprett en feilmelding
	 * @param errorCode årsak til feilen
	 * @param numRemainingAttempts antall forsøk brukeren har igjen
	 * @param username brukernavn til den som prøvde å logge seg på
	 */
	public LoginException(ErrorCode errorCode, Integer numRemainingAttempts, String username) {
		super();
		this.errorCode = errorCode;
		this.numRemainingAttempts = numRemainingAttempts;
		if (logger.isInfoEnabled())
			logger.info("Innlogging mislyktes for " + username + ", pga.: " + errorCode.toString() 
					+ ", antall forsøk igjen: " + numRemainingAttempts);
	}

	/**
	 * Gir deg årsaken til hvorfor du ikke ble logget inn
	 * @return årsaken til feilen
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * Gir deg antall innloggings-forsøk du har igjen for denne brukeren. 
	 * Vil være null hvis Cause = {@link ErrorCode#UNKNOWN_USERNAME}
	 * @return antall innloggings-forsøk du har igjen for denne brukeren.
	 */
	public Integer getNumRemainingAttempts() {
		return numRemainingAttempts;
	}

	@Override
	public String getMessage() {
		if (errorCode.equals(ErrorCode.UNKNOWN_USERNAME)) {
			return MESSAGE_UNKNOWN_USERNAME;
		} if (errorCode.equals(ErrorCode.WRONG_PASSWORD)) {
			return String.format(MESSAGE_WRONG_PASSWORD, numRemainingAttempts);
		}

		return MESSAGE_UNKNOWN_NO_MORE_ATTEMPTS;
	}
}
