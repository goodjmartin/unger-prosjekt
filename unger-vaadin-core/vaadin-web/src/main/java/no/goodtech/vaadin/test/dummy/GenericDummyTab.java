package no.goodtech.vaadin.test.dummy;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Label;
import no.goodtech.vaadin.tabs.IConfigurablePanel;

public class GenericDummyTab extends VerticalLayout implements IConfigurablePanel<IConfigurableTabSheetLayoutView, Person> {

	private final Label label = new Label();
	private volatile int refreshCounter = 0;

	public GenericDummyTab(final String tabName) {
		setCaption(tabName);
		setMargin(false);
		setSpacing(false);
		addComponents(label);
	}

	@Override
	public void refresh(Person person) {
		if(person!=null){
			label.setValue(String.format("Person: firstName=%s, lastName=%s, refreshCounter=%d", person.getFirstName(), person.getLastName(), refreshCounter++));
		}
	}

}
