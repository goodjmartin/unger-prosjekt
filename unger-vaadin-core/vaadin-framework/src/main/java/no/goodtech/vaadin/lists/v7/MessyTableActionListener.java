package no.goodtech.vaadin.lists.v7;

import java.util.Set;

/**
 * Default implementation of {@link IMessyTableActionListener} for lazy programmers.
 * You may override the methods you like.
 * @param <T> the data type of the bean in the table
 */
@Deprecated
public class MessyTableActionListener<T> implements IMessyTableActionListener<T> {

	public void objectSelected(T object) {}

	public void objectsSelected(Set<T> objects) {}

	public void doubleClick(T object) {}

	public void pleaseDelete(T object) {}
}
