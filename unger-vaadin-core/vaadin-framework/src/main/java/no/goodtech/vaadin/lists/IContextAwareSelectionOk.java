package no.goodtech.vaadin.lists;

import java.util.Set;

@FunctionalInterface
public interface IContextAwareSelectionOk<T> {
	boolean ok(Set<T> selected);
}