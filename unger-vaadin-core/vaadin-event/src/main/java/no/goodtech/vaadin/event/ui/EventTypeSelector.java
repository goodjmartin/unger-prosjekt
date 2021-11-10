package no.goodtech.vaadin.event.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ListSelect;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.event.model.EventType;
import no.goodtech.vaadin.event.model.EventTypeFinder;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.Set;

public class EventTypeSelector extends ListSelect {
	private static final String SELECTOR_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.type.selector.caption");
	private static final String SELECTOR_DESCRIPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.type.selector.description");


	public EventTypeSelector(final IEventListener eventListener) {
		setCaption(SELECTOR_CAPTION);
		//setDescription(SELECTOR_DESCRIPTION);
		setImmediate(true);
		setMultiSelect(true);
		addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final Property.ValueChangeEvent event) {
				if (event.getProperty().getValue() != null) {
					Set<EventType> eventTypes = (Set<EventType>) event.getProperty().getValue();
					eventListener.eventTypesSelected(eventTypes);
				}

			}
		});
	}

	public void refreshEventTypeSelector(final EntityStub abstractEntity) {
		removeAllItems();
		if (abstractEntity != null) {
			for (EventType eventType : new EventTypeFinder().setOwnerClass(abstractEntity.getClass()).list()) {
				addItem(eventType);
				setItemCaption(eventType, eventType.getId());
			}
			//Hackish måte å begrense høyden på, se https://vaadin.com/forum/#!/thread/471355
			setHeight(((size() > 5) ? 5 : size()) + 1.5F, Sizeable.Unit.EM);
		}
	}

}
