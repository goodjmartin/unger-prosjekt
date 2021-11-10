package no.goodtech.vaadin.event.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ListSelect;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.event.model.EventFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.Set;

public class ChangedBySelector extends ListSelect {
	private static final String SELECTOR_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.changedBy.selector.caption");
	private static final String SELECTOR_DESCRIPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.changedBy.selector.description");

	public ChangedBySelector(final IEventListener eventListener) {
		//setDescription(SELECTOR_DESCRIPTION);
		setCaption(SELECTOR_CAPTION);
		setImmediate(true);
		setMultiSelect(true);
		addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				if (event.getProperty().getValue() != null) {
					Set<String> sourcesOfChange = (Set<String>) event.getProperty().getValue();
					eventListener.changedBySelected(sourcesOfChange);
				}
			}
		});
	}

	public void refreshChangedBySelector(final EntityStub abstractEntity) {
		removeAllItems();
		//TODO fant ikke ut hvordan jeg filtrerer på distinct med finderen
		for (no.goodtech.vaadin.event.model.Event event : new EventFinder().setOwnerClass(abstractEntity.getClass()).list()) {
			addItem(event.getChangedBy());
		}
		//Hackish måte å begrense høyden på, se https://vaadin.com/forum/#!/thread/471355
		setHeight(((size() > 5) ? 5 : size()) + 1.5F, Sizeable.Unit.EM);
	}
}

