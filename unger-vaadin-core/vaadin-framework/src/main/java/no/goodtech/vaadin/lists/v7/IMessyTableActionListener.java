package no.goodtech.vaadin.lists.v7;

import java.util.Set;

/**
 * Formidler ting som skjer i {@link MessyTable}
 */
public interface IMessyTableActionListener<T> {

	/**
	 * EN rad er valgt eller valgt bort
	 * Blir ikke kalt hvis tabellen er i flervalgs-modus, se {@link #objectsSelected(Set)}
	 * @param object objektet som er valgt, eller null om en rad er "valgt bort"
	 */
	void objectSelected(T object);
	
	/**
	 * FLERE rader er valgt
	 * Blir bare kalt hvis tabellen er i flervalgs-modus
	 * @param object objektene som er valgt
	 */
	void objectsSelected(Set<T> objects);
	
	/**
	 * Noen har dobbeltklikket i tabellen
	 * @param object objektet som ble klikket på
	 */
	void doubleClick(T object);
	
	/**
	 * Noen har trykket på DELETE-tasten
	 * For at dette skal virke må du bruke {@link MessyTable#createShortcutKeyAwarePanelWrapper(IMessyTableActionListener)}
	 * @param object objektet som var valgt
	 */
	void pleaseDelete(T object);
}
