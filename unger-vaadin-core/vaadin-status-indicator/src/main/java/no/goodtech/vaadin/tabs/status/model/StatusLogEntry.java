package no.goodtech.vaadin.tabs.status.model;

import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.tabs.status.common.StateType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "StatusLogEntry")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class StatusLogEntry extends AbstractEntityImpl<StatusLogEntry> implements StatusLogEntryStub {

	private static final long serialVersionUID = 1L;

    @Column(name = "message", length = 255)
    private String message;

    @Column(name = "details", length = 6144)
    private String details = null;

    @Column(name = "stateType")
    private StateType stateType;

    @Column
    private LocalDateTime created = LocalDateTime.now(); // TODO should this be a timestamp (Instant)? TODO2 how can we use the DateService for this?

    @ManyToOne
    private StatusIndicator statusIndicator;

	public StatusLogEntry() {
	}

    public StatusLogEntry(final String message, final StateType stateType, final StatusIndicatorStub statusIndicator) {
        this(message, null, stateType, statusIndicator);
    }

    public StatusLogEntry(final String message, final String details, final StateType stateType, final StatusIndicatorStub statusIndicator) {
        this.message = message;
        this.details = details;
        this.stateType = stateType;
        this.statusIndicator = (StatusIndicator) statusIndicator;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public StateType getStateType() {
        return stateType;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public StatusIndicator getStatusIndicator() {
        return statusIndicator;
    }

    @Override
    public String getNiceClassName() {
        return "Status Log Entry";
    }


}
