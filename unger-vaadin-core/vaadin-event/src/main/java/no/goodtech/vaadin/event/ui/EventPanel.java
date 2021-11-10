package no.goodtech.vaadin.event.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.event.model.EventType;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.search.DateField;

import java.util.Date;
import java.util.Set;

public class EventPanel extends VerticalLayout implements IEventListener {
	private static final String DATE_FIELD_PARSE_ERROR_MESSAGE = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.dateField.parseErrorMessage");
	private static final String FROM_DATE_FIELD_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.fromDateField.caption");
	private static final String FROM_DATE_FIELD_DESCRIPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.fromDateField.description");
	private static final String TO_DATE_FIELD_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.toDateField.caption");
	private static final String TO_DATE_FIELD_DESCRIPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("event.toDateField.description");

	private final EventTableLayout eventTableLayout;
	private final EventTypeSelector eventTypeSelector;
	private final ChangedBySelector changedBySelector;

	//The owner of the events
	private volatile EntityStub ownerEntity;

	public EventPanel() {
		setSizeFull();
		eventTableLayout = new EventTableLayout();

		changedBySelector = new ChangedBySelector(this);
		eventTypeSelector = new EventTypeSelector(this);
		DateField fromDateField = new DateField(FROM_DATE_FIELD_CAPTION);
		fromDateField.setParseErrorMessage(DATE_FIELD_PARSE_ERROR_MESSAGE);
		//fromDateField.setDescription(FROM_DATE_FIELD_DESCRIPTION);
		fromDateField.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(final Property.ValueChangeEvent event) {
				eventTableLayout.setFromDate((Date) event.getProperty().getValue());
				eventTableLayout.refreshEventTable(ownerEntity);

			}
		});

		DateField toDateField = new DateField(TO_DATE_FIELD_CAPTION);
		toDateField.setParseErrorMessage(DATE_FIELD_PARSE_ERROR_MESSAGE);
		//toDateField.setDescription(TO_DATE_FIELD_DESCRIPTION);
		toDateField.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(final Property.ValueChangeEvent event) {
				eventTableLayout.setToDate((Date) event.getProperty().getValue());
				eventTableLayout.refreshEventTable(ownerEntity);
			}
		});

		eventTypeSelector.setWidth("100%");
		changedBySelector.setWidth("100%");
		fromDateField.setWidth("100%");
		toDateField.setWidth("100%");

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.addComponents(eventTypeSelector, changedBySelector, fromDateField, toDateField);
		filterLayout.setMargin(false);
		filterLayout.setWidth("100%");
		filterLayout.setExpandRatio(eventTypeSelector, 1.0F);
		filterLayout.setExpandRatio(changedBySelector, 1.0F);
		filterLayout.setExpandRatio(fromDateField, 1.0F);
		filterLayout.setExpandRatio(toDateField,1.0F);
		filterLayout.setComponentAlignment(eventTypeSelector, Alignment.TOP_LEFT);
		filterLayout.setComponentAlignment(changedBySelector, Alignment.TOP_LEFT);
		filterLayout.setComponentAlignment(fromDateField, Alignment.TOP_LEFT);
		filterLayout.setComponentAlignment(toDateField, Alignment.TOP_LEFT);
		addComponents(filterLayout, eventTableLayout);
		setExpandRatio(eventTableLayout, 1.0F);
	}

	@Override
	public void eventTypesSelected(final Set<EventType> eventTypes) {
		eventTableLayout.setEventTypes(eventTypes);
		eventTableLayout.refreshEventTable(ownerEntity);
	}

	@Override
	public void changedBySelected(final Set<String> sourcesOfChange) {
		eventTableLayout.setSurcesOfChange(sourcesOfChange);
		eventTableLayout.refreshEventTable(ownerEntity);
	}

	/**
	 * Refresh the table when a entity is selected
	 *
	 * @param entity the selected entity
	 */
	public void ownerEntitySelected(final EntityStub entity) {
		if (entity != null) {
			setVisible(true);
			this.ownerEntity = entity;
			eventTableLayout.refreshEventTable(ownerEntity);
			eventTypeSelector.refreshEventTypeSelector(ownerEntity);
			changedBySelector.refreshChangedBySelector(ownerEntity);
		} else {
			setVisible(false);
		}
	}
}
