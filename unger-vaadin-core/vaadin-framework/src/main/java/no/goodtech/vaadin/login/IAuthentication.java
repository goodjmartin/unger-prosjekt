package no.goodtech.vaadin.login;

/**
 * This interface defines the operations
 *
 * @author bakke
 */
public interface IAuthentication {

    /**
     * This class should be called to authenticate a user upon login.
     *
     * @param id The user identity (i.e. login name)
     * @param password The entered password
     * @return If correct credentials are provided, a new User object is returned (otherwise null is returned)
     */
    public User isAuthenticated(final String id, final String password);

}
