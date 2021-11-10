package no.goodtech.vaadin.utils;

public interface IFormChangedListener {
	/**
	 * @param enabled set to true if the components are set to enabled, false if disabled
	 */
	void formHasChanged(boolean enabled);
}
