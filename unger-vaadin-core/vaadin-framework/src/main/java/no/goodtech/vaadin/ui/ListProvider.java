package no.goodtech.vaadin.ui;

import java.util.List;

public interface ListProvider<LISTITEM> {
	List<LISTITEM> getItems();
}
