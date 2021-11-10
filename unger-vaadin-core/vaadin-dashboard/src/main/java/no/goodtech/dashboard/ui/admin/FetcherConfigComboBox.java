package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.config.fetcher.FetcherConfigFinder;
import no.goodtech.vaadin.lists.v7.MessyComboBox;

public class FetcherConfigComboBox extends MessyComboBox<FetcherConfig> {

	public FetcherConfigComboBox(String caption) {
		super(caption);
	}

	@Override
	protected String getId(FetcherConfig item) {
		return item.getId();
	}

	@Override
	protected String getName(FetcherConfig item) {
		return getId(item);
	}

	public void refresh() {
		super.refresh(new FetcherConfigFinder().orderById().list());
	}
}
