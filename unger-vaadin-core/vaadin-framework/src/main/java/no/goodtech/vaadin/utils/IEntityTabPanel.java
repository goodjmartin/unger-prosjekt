package no.goodtech.vaadin.utils;

import no.goodtech.persistence.entity.EntityStub;

/**
 * Your component should implement this if you want to show your component as a tab in {@link EntityTabPanel
 * @see no.goodtech.vaadin.ui.EntityTabs
 */
public interface IEntityTabPanel {

	/**
	 * Use this to tell if you want the caption to be refreshed on the tabsheet
	 */
	interface IEntityTabListener {
		void captionRefreshed(String caption);
	}

	/**
	 * @return an ID used to identify the panel among the other tabs. Must be unique in a tab sheet
	 */
	default String getId() {
		return getClass().getSimpleName();
	}

	/**
	 * Refresh the current "parent" entity.
	 */
	void refresh(EntityStub entity);

	/**
	 * Set panel to read only mode
	 */
	void setReadOnly(boolean readOnly);

	/**
	 * Initializes the caption count on tabs. The class that implements these need to have a Integer and IEntityTabListener as member variables.
	 * This method needs to set the Integers value equal to the length or size of the grid. Usually with getFinder().count(). There are some corner cases.
	 * I also needs to call refreshCaption();
	 * @param entity Some entityTabs use this to limit the list that get get finder searches for.
	 */
	default void initCaption(EntityStub entity) { }

	/**
	 * This sets the listener equal to the member variable listener.
	 * @param listener The entityTab listener
	 */
	default void addListener(IEntityTabListener listener) { }
}
