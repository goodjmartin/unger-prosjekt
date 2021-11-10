package no.goodtech.vaadin.security.model;

import no.goodtech.persistence.entity.EntityStub;

public interface UserStub extends EntityStub<User> {
	String getId();
	String getName();
	String[] getRoles();
}
