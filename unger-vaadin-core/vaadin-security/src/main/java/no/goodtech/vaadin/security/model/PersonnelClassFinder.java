package no.goodtech.vaadin.security.model;

import no.goodtech.persistence.jpa.AbstractFinder;

public class PersonnelClassFinder extends AbstractFinder<PersonnelClass, PersonnelClassStub, PersonnelClassFinder> {

	private static final long serialVersionUID = 1L;

	public PersonnelClassFinder() {
		super("select pc from PersonnelClass pc ", "pc");
	}
	
	public PersonnelClassFinder setOrderByName(boolean ascending) {
		addSortOrder(prefixWithAlias("name"), ascending);
		return this;
	}

}
