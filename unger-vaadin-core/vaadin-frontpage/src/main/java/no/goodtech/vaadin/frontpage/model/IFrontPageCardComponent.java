package no.goodtech.vaadin.frontpage.model;

import com.vaadin.ui.Component;
import no.goodtech.vaadin.frontpage.ui.FrontPageCardInput;
import no.goodtech.vaadin.login.User;

import java.util.Map;

/**
 * Implement this interface to add the component as a front page card. Additionally, annotate the component with @FrontPageCard
 * @see FrontPageCardComponent
 */
public interface IFrontPageCardComponent extends Component {

	/**
	 * Optional.
	 * Create an input parameter panel. The inputs from this panel define the arguments of the component.
	 * This panel will typically look similar to some of the filter panels.
	 * Remember to set id to the components of this panel as it acts as a connector to the parameter for the card argument.
	 */
	default FrontPageCardInput getInputPanel() {return null;}

	/**
	 * Optional.
	 * Get arguments special for this front page card. Typically id, group or pk.
	 */
	default Map<String, String> getArguments(){
		return null;
	}

	/**
	 * Optional.
	 * This is called whenever the card is instantiated in the front-page panel. Therefore, the arguments for the component is set.
	 */
	default void setArguments(Map<String, String> args){
	}

	/**
	 * Optional.
	 * This is called whenever the user enters/refreshes the panel.
	 */
	default void refresh(){
	}

	/**
	 * The name of the component that will be shown to the user
	 */
	String getViewName();

	/**
	 * If parent view id is set, clicking the front page card header will navigate to the parent view (no support for url-parameters yet...)
	 */
	default String getParentViewId(){return null;}

	/**
	 * Make sure the current user have access to view the component. True = have access
	 */
	boolean isAuthorized(User user, String arg);
}
