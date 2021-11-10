package no.goodtech.vaadin.lists;

public interface RowCountProvider {

	/**
	 * Will be called when a query has been run
	 * @param count last query row count
	 */
	void newCountReceived(int count);
}