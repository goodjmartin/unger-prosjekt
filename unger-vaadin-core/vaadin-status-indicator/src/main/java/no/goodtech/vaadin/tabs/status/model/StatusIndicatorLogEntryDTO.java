package no.goodtech.vaadin.tabs.status.model;

public class StatusIndicatorLogEntryDTO implements Comparable {

	private final StatusIndicator statusIndicator;
	private final Long successes, warnings, fails;

	public StatusIndicatorLogEntryDTO(StatusIndicator statusIndicator, Long successes, Long warnings, Long fails) {
		this.statusIndicator = statusIndicator;
		this.successes = successes;
		this.warnings = warnings;
		this.fails = fails;
	}

	public StatusIndicator getStatusIndicator() {
		return statusIndicator;
	}

	public Long getSuccesses() {
		return successes;
	}

	public Long getWarnings() {
		return warnings;
	}

	public Long getFails() {
		return fails;
	}

	@Override
	public int compareTo(Object o) {
		if (statusIndicator != null && o instanceof StatusIndicatorLogEntryDTO && ((StatusIndicatorLogEntryDTO)o).statusIndicator != null)
			return ((StatusIndicatorLogEntryDTO)o).getStatusIndicator().getId().compareTo(statusIndicator.getId());
		return 0;
	}
}
