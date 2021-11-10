package no.goodtech.vaadin.login;

import java.util.Set;

/**
 * This class holds the information the application needs to know about the logged in user.
 *
 * @author bakke
 */
public class User {
    private final Long pk;
    private final String id;
    private final String fullName;
    private final Set<String> accessFunctionIds;

    public User(final Long pk, final String id, final String fullName, final Set<String> accessFunctionIds) {
        this.pk = pk;
        this.id = id;
        this.fullName = fullName;
        this.accessFunctionIds = accessFunctionIds;
    }

    public Long getPk() {
        return pk;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Set<String> getAccessFunctionIds() {
        return accessFunctionIds;
    }

}
