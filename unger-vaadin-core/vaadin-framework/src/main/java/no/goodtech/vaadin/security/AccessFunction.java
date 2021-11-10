package no.goodtech.vaadin.security;

/**
 * This class holds the identity and description for a particular access function. An access function is used to protect
 * various parts of the functionality provided by this application. Access roles may be assigned none, one or many such
 * access functions.
 *
 * @author bakke
 */
public class AccessFunction {
    private final String id;
    private final String description;

    public AccessFunction(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

}
