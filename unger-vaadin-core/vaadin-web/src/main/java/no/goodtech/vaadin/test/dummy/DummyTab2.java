package no.goodtech.vaadin.test.dummy;

import no.goodtech.vaadin.tabs.IConfigurablePanel;

public class DummyTab2 extends GenericDummyTab implements IConfigurablePanel<IConfigurableTabSheetLayoutView, Person> {

	public DummyTab2(@SuppressWarnings("UnusedParameters") final IConfigurableTabSheetLayoutView configurableTabSheetLayoutView) {
		super("DummyTab2");
	}

}
