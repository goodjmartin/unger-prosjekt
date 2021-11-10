package no.goodtech.vaadin.search;

public interface IOwnerClassChangeListener {
	/**
	 * Is called each time a new ownerClass is selected from the OwnerClassComboBox.
	 * It is then possible to populate the OwnerComboBox by calling the refreshOwnerComboBox() in OwnerFilterPanel
	 *
	 * @param ownerClass the new ownerClass selected
	 */
	void ownerClassChanged(Class<?> ownerClass);
}
