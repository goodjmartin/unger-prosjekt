package no.goodtech.vaadin.tabs.status.model;

import no.cronus.common.date.DateTimeFactory;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = "StatusIndicator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StatusIndicator extends AbstractEntityImpl<StatusIndicator> implements StatusIndicatorStub, Comparable<StatusIndicator> {

	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = LoggerFactory.getLogger(StatusIndicator.class);

	// Status indicator ID
	@Column(name = "id", nullable = false, unique = true)
	private String id;

	// Status indicator name
	@Column(name = "name")
	private String name;

	@Transient
	private transient Boolean ok;

	@Transient
	private transient LocalDateTime lastHeartbeatAt, lastMuteAt = LocalDateTime.now(); // TODO how can we use the DateService for this?

	@Transient
	private transient long numFailuresSinceMute;

	@Transient
	private transient String message;

	public StatusIndicator() {
		LOGGER.debug("===========> i constructor");
	}

	public StatusIndicator(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getNiceClassName() {
		return "Status Indicator";
	}


	@Override
	public int compareTo(StatusIndicator o) {
		if (id == null)
			return 0;
		return id.compareTo(o.getId());
	}

	/**
	 * Set heartbeat status
	 * @param ok true = I am ok now and satisfied. False = Last time I did something it went wrong.
	 * @see #isOk()
	 */
	public void setOk(Boolean ok) {
		this.ok = ok;
		lastHeartbeatAt = DateTimeFactory.dateTime();
		if (ok != null && !ok)
			numFailuresSinceMute++;
	}

	public Boolean isOk() {
		return ok;
	}

	public boolean isOkBool() {
		return ok != null && ok;
	}

	public void setLastHeartbeatAt(LocalDateTime lastHeartbeatAt) {
		this.lastHeartbeatAt = lastHeartbeatAt;
	}

	public LocalDateTime getLastHeartbeatAt() {
		return lastHeartbeatAt;
	}

	public long getNumFailuresSinceMute() {
		return numFailuresSinceMute;
	}

	public LocalDateTime getLastMuteAt() {
		return lastMuteAt;
	}

	public void mute() {
		numFailuresSinceMute = 0;
		lastMuteAt = DateTimeFactory.dateTime();
	}

	/**
	 * For unit testing
	 */
	void setLastHeartbeatAt(long lastHeartbeatAt) {
		this.lastHeartbeatAt = DateTimeFactory.dateTime(lastHeartbeatAt);
	}

	/**
	 * For unit testing
	 */
	void setLastMuteAt(long lastMuteAt) {
		this.lastMuteAt = DateTimeFactory.dateTime(lastMuteAt);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		lastHeartbeatAt = DateTimeFactory.dateTime();
	}
}
