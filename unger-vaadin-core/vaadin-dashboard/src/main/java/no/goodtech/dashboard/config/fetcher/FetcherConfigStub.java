package no.goodtech.dashboard.config.fetcher;

import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;

public interface FetcherConfigStub<ENTITY extends Entity>
		extends EntityStub<ENTITY> {

	String getId();

	Integer getRefreshIntervalInSeconds();

	boolean isFullFetch();
}
