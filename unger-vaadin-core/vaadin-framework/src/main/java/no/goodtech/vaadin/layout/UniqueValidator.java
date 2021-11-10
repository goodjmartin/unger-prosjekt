package no.goodtech.vaadin.layout;

/**
 * Created with IntelliJ IDEA.
 * User: rsan
 * Date: 29.01.13
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public interface UniqueValidator {

	/**
	 * @param value verdien som skal være unik
	 * @return true om verdien er unik, false hvis ikke
	 */
	boolean isUnique(String value);
}
