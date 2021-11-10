package no.goodtech.dashboard.config.ui;

import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.category.Category;

public interface DashboardConfigStub extends EntityStub<DashboardConfig> {

	String getId();

	String getTitle();
	
	Integer getRefreshIntervalInSeconds();

	int getNumRows();
	
	int getNumColumns();

	Category getArea();
}
