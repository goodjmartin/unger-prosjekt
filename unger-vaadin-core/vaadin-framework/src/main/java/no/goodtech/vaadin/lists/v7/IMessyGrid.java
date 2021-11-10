package no.goodtech.vaadin.lists.v7;

import java.util.Set;

@Deprecated
public interface IMessyGrid<T> {

	void editingAccepted();

	void editingCanceled();

	void objectSelected(T object);

	void objectsSelected(Set<T> objects);

}
