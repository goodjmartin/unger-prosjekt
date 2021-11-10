package no.goodtech.vaadin.event.ui;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.event.model.EventFinder;
import no.goodtech.vaadin.event.model.EventType;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import java.util.Date;
import java.util.Set;

public class EventTableLayout extends VerticalLayout {
	private static final String CREATED_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("eventTable.column.created.caption");
	private static final String CHANGED_BY_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("eventTable.column.changedBy.caption");
	private static final String TYPE_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("eventTable.column.eventTypeId.caption");
	private static final String DETAIL_CAPTION = ApplicationResourceBundle.getInstance("vaadin-event").getString("eventTable.column.eventDetail.caption");

	private final BeanItemContainer<no.goodtech.vaadin.event.model.Event> eventBeanItemContainer;
	private Set<EventType> eventTypes;
	private Set<String> surcesOfChange;
	private Date fromDate;
	private Date toDate;

	public EventTableLayout() {
		setSizeFull();
		setSpacing(false);
		setMargin(false);

		//Initialize the beanItemContainer
		eventBeanItemContainer = new BeanItemContainer<no.goodtech.vaadin.event.model.Event>(no.goodtech.vaadin.event.model.Event.class);
		eventBeanItemContainer.addNestedContainerProperty("eventType.id");

		//Create table with empty caption and set the datasource
		Table eventTable = new Table("", eventBeanItemContainer);

		//Set visible columns
		eventTable.setVisibleColumns("created", "changedBy", "eventType.id", "eventDetail");

		//Set column header text
		eventTable.setColumnHeaders(CREATED_CAPTION, CHANGED_BY_CAPTION, TYPE_CAPTION, DETAIL_CAPTION);

		//Set the table to be unselectable and immediate
		eventTable.setSelectable(false);
		eventTable.setImmediate(true);

		eventTable.setSizeFull();
		addComponent(eventTable);
	}

	/**
	 * Remove all items from the container and repopulate it with events from the entity.
	 *
	 * @param entity the owner of the events
	 */
	public void refreshEventTable(final EntityStub entity) {
		eventBeanItemContainer.removeAllItems();
		if (entity != null) {
			EventFinder eventFinder = new EventFinder();
			eventFinder.setOwner(entity);
			eventFinder.orderByCreated(false);
			if (eventTypes != null) {
				eventFinder.setEventTypes(eventTypes.toArray());
			}
			if (surcesOfChange != null) {
				eventFinder.setSources(surcesOfChange.toArray());
			}
			if (fromDate != null) {
				eventFinder.setCreatedAfter(fromDate);
			}
			if (toDate != null) {
				eventFinder.setCreatedBefore(toDate);
			}
			for (no.goodtech.vaadin.event.model.Event event : eventFinder.list()) {
				eventBeanItemContainer.addBean(event);
			}
		}
	}


	public void setEventTypes(final Set<EventType> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public void setSurcesOfChange(final Set<String> sourcesOfChange) {
		this.surcesOfChange = sourcesOfChange;
	}

	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}
}
