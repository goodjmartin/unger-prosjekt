package no.goodtech.vaadin.lists;

import java.util.Set;

public interface IMessyGrid<T> {

	default void editingAccepted() {
	}

	default void editingCanceled() {
	}

	default void objectSelected(T object) {
	}

	default void objectsSelected(Set<T> objects) {
	}

	/**
	 * Noen har trykket på DELETE-tasten
	 * For at dette skal virke må du bruke {@link MessyTable#createShortcutKeyAwarePanelWrapper(IMessyTableActionListener)}
	 *
	 * @param object objektet som var valgt
	 */
	default void pleaseDelete(T object) {

	}

	/**
	 * Noen har dobbeltklikket i tabellen
	 *
	 * @param object objektet som ble klikket på
	 */
	void doubleClick(T object);

}
