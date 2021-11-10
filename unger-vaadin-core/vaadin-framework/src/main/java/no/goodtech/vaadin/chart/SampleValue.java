package no.goodtech.vaadin.chart;

import java.util.Date;

public class SampleValue {

	private final Date time;
	private final Double value;

	public SampleValue(Date time, Double value) {
		this.time = time;
		this.value = value;
	}

	public Date getTime() {
		return time;
	}

	public Double getValue() {
		return value;
	}
}
