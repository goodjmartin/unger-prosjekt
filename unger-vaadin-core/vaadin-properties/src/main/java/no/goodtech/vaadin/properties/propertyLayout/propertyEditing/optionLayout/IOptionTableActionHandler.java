package no.goodtech.vaadin.properties.propertyLayout.propertyEditing.optionLayout;


/**
Interface for å kommunisere actionhandler hendelser til tabellen
 */
public interface IOptionTableActionHandler {
	/**
	 * Returnerer true hvis tabellen inneholder items
	 * @return true hvis tabellen inneholder items, false ellers
	 */
	public boolean tableContainsItems();

	/**
	 * Legger til et nytt item etter previousitemid
	 * @param previousItemId itemet det skal legges til et item etter
	 */
	public void actionAddItemAfter(Object previousItemId);

	/**
	 * Fjernet item
	 * @param target item som skal fjernes
	 */
	public void actionDeleteItem(Object target);

	/**
	 * Legger til et nytt item
	 */
	public void actionAddItem();

	/**
	 * Flytter valgt item opp en rad
	 * @param target item som skal flyttes opp
	 */
	public void actionShiftItemUp(Object target);

	/**
	 * Sorterer items i alfabetisk rekkefølge
	 */
	public void actionSortItemsAlphabetically();

	/**
	 * Flytter valgt item ned en rad
	 * @param target item som skal flyttes ned
	 */
	public void actionShiftItemDown(Object target);
}
