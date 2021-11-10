package no.goodtech.dashboard.model;

import no.goodtech.vaadin.utils.Utils;

import java.util.Date;

public class SampleDTO {

	private final Date created;
	private final Double value;

	public SampleDTO(final Date created, final Double value) {
		this.created = created;
		this.value = value;
	}

	public Date getCreated() {
		return created;
	}

	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value + " @ " + Utils.DATEDETAILTIME_FORMATTER.format(created);
	}
}
