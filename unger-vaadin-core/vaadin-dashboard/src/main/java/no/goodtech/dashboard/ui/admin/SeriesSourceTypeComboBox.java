package no.goodtech.dashboard.ui.admin;

import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.model.AbstractFetcher;
import no.goodtech.dashboard.model.IAdHocFetcher;
import no.goodtech.dashboard.model.ISourceType;
import no.goodtech.vaadin.lists.v7.MessyComboBox;

import java.util.List;

public class SeriesSourceTypeComboBox extends MessyComboBox<ISourceType> {

	public SeriesSourceTypeComboBox(String caption) {
		super(caption);
	}

	@Override
	protected String getId(ISourceType item) {
		return String.valueOf(item.getId());
	}

	@Override
	protected String getName(ISourceType item) {
		return item.getName();
	}
//
//	public void select(Integer id) {
//		if (id == null)
//			super.select(null);
//		else
//			super.select(String.valueOf(id));
//	}
//
//	public void refresh(FetcherConfig fetcherConfig) {
//		if (fetcherConfig != null) {
//			final AbstractFetcher fetcher = fetcherConfig.createFetcher(0);
//			if (fetcher instanceof IAdHocFetcher) {
//				IAdHocFetcher adHocFetcher = (IAdHocFetcher) fetcher;
//				final List<ISourceType> sourceTypes = adHocFetcher.listSourceTypes();
//				refresh(sourceTypes);
//			}
//		} else {
//			clear();
//		}
//	}
}
