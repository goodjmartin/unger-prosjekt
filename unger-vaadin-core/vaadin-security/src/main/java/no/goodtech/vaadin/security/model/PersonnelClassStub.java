package no.goodtech.vaadin.security.model;

import no.goodtech.persistence.entity.EntityStub;

public interface PersonnelClassStub extends EntityStub<PersonnelClass> {
	public String getId();
	public String getDescription();
	public String getName();
}
