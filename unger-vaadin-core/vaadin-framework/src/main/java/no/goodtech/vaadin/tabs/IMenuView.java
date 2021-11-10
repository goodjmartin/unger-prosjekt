package no.goodtech.vaadin.tabs;

import com.vaadin.navigator.View;
import no.goodtech.vaadin.login.User;

public interface IMenuView extends View {

    /**
     * This method should be called to check if the user is allowed to see the menu item
     *
     * @param user The user object
     * @param argument Value provided from menu system
     *
     * @return True if authorized to view the menu item, otherwise false
     */
    boolean isAuthorized(final User user, final String argument);

    String getViewName();

}
