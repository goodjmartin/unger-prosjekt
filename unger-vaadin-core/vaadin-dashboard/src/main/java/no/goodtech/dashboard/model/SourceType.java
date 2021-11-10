package no.goodtech.dashboard.model;

import no.goodtech.dashboard.model.IAdHocFetcher;

public class SourceType implements ISourceType {

	private final int id;
	private final String name;

	public SourceType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String toString() {
		if (name != null)
			return name;
		return String.valueOf(id);
	}
}
