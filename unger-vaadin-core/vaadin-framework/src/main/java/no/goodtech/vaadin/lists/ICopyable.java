package no.goodtech.vaadin.lists;

/**
 * Use this on objects that have a copy function
 * @param <T> the type of the object
 * @todo move this to persistence-core
 */
public interface ICopyable<T> {

	/**
	 * @return a copy of this (in memory). You have to save the copy yourself
	 */
	T copy();
}
