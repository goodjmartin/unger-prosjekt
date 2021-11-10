package no.goodtech.vaadin.help.model;

import no.goodtech.persistence.jpa.AbstractFinder;

public class HelpTextFinder extends AbstractFinder<HelpText, HelpText, HelpTextFinder>{

	public HelpTextFinder() {
		super("select h from HelpText h", "h");
	}
}
