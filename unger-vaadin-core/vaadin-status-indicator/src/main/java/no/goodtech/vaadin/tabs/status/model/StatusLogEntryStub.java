package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.tabs.status.common.StateType;

import java.time.LocalDateTime;

public interface StatusLogEntryStub extends EntityStub<StatusLogEntry> {
    
	String getMessage();

    StateType getStateType();

    LocalDateTime getCreated();

    StatusIndicator getStatusIndicator();

	String getDetails();

}
