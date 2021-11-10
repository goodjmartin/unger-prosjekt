package no.goodtech.vaadin.tabs;

import com.vaadin.ui.Component;

/**
 * Egner seg til flipper som skal vise detaljer om et domene-objekt
 * @author oystein
 *
 * @param <LISTENER> callback-interface hvis du vil få beskjed hvis det skjer noe inni en flipp
 * @param <DTO> domene-klasse
 */
public interface DetailObjectTab<LISTENER, DTO> extends Component, IConfigurablePanel<LISTENER, DTO> {
}
