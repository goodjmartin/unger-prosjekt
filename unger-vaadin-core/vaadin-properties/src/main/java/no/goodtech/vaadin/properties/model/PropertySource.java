package no.goodtech.vaadin.properties.model;

import java.util.Collection;

/**
 * Du må imlementere dette om du ønsker å tillate at andre objekter kan arve egenskapene dine.
 * @see Property
 * @author oystein
 */
public interface PropertySource {

	/**
	 * @return en liste over primærnøkler til alle objekter som arver egenskaper fra dette objektet
	 */
	public Collection<Long> listPropertyHeirPks();
}
