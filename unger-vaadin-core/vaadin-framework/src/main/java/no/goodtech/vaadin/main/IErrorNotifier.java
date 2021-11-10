package no.goodtech.vaadin.main;

/**
 * Handle showing of error messages
 *
 * @author bakke
 */
public interface IErrorNotifier {

	/**
	 * This method should be called to show the failure window to the user
	 * @param exception the error that happened
	 */
	public void showUnhandledExceptions(Throwable exception);

	/**
	 * This method should be called to hide the failure window to the user
	 */
	public void hideUnhandledExceptions();

}
