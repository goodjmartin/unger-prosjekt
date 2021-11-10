package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

import no.goodtech.vaadin.layout.FontIconComboBox;
import no.goodtech.vaadin.tabs.status.common.StateType;

/**
 * Ikon som representerer alvorlighetsgraden for en status-indikator eller status-logge-hendelse
 * @author oystein
 */
public class StateTypeIcon {
    
    private static final ThemeResource SUCCESS_IMAGE = new ThemeResource("images/blue.png");
    private static final ThemeResource WARNING_IMAGE = new ThemeResource("images/yellow.png");
    private static final ThemeResource FAILURE_IMAGE = new ThemeResource("images/red.png");

	/**
	 * Gir deg et lampe-ikon med farge som passer med angitt alvorlighetsgrad
	 * @param stateType alvorlighetsgrad
	 * @return lampe-ikon med farge som passer med angitt alvorlighetsgrad
	 * @deprecated use {@link #getIcon(StateType)}
	 */
	public static ThemeResource getResource(final StateType stateType) {
        if (stateType.equals(StateType.SUCCESS)) {
        	return SUCCESS_IMAGE;
        } else if (stateType.equals(StateType.WARNING)) {
            return WARNING_IMAGE;
        } else {
            return FAILURE_IMAGE;
        }
	}
	
	public static Resource getIcon(final StateType stateType) {
		switch(stateType) {
		case SUCCESS:
			return VaadinIcons.SMILEY_O;
		case WARNING:
			return VaadinIcons.EXCLAMATION_CIRCLE_O;
		default:
			return VaadinIcons.WARNING;
		}
	}

	public static String getIconAsHtml(final StateType stateType) {
		switch(stateType) {
			case SUCCESS:
				return VaadinIcons.SMILEY_O.getHtml();
			case WARNING:
				return VaadinIcons.EXCLAMATION_CIRCLE_O.getHtml();
			default:
				return VaadinIcons.WARNING.getHtml();
		}
	}

}
