package no.goodtech.dashboard.ui.admin;

import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.*;
import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.DashboardConfigStub;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.main.SimpleInputBox;

/**
 * A try on using vaadin 8 form whic is put on hold because:
 * - bean validation through annotations in entity don't work, it gives RuntimeExceptions after binder has finished
 * - converting of Integer in entity to TextField doesn't work if Integer is null
 * - automatic binding to entity only work for strings
 * @see DashboardConfigFormVaadin7
 */
public class DashboardConfigFormVaadin8 extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	private final BeanValidationBinder<DashboardConfig> binder;
	private final Label statusLabel = new Label();
	private final TextField id, title, numRows, numColumns, refreshIntervalInSeconds;
	private final PanelConfigGrid panelConfigs;


	public DashboardConfigFormVaadin8(DashboardConfigStub dashboardConfig) {
		setCaption(Texts.get("dashboardConfig.form.caption"));
		id = new TextField(Texts.get("dashboardConfig.form.id.caption"));
		title = new TextField(Texts.get("dashboardConfig.form.title.caption"));
		numRows = new TextField(Texts.get("dashboardConfig.form.numRows.caption"));
		numColumns = new TextField(Texts.get("dashboardConfig.form.numColumns.caption"));
		refreshIntervalInSeconds = new TextField(Texts.get("dashboardConfig.form.refreshIntervalInSeconds.caption"));
		panelConfigs = new PanelConfigGrid(null);
		panelConfigs.setCaption(Texts.get("dashboardConfig.form.panelConfigs.caption"));
		panelConfigs.setSizeFull();
		addComponents(new HorizontalLayout(id, title), new HorizontalLayout(numRows, numColumns), refreshIntervalInSeconds, statusLabel, panelConfigs);
		setSizeFull();

		//TODO: ID unique validation
			final StringToIntegerConverter toIntegerConverter = new StringToIntegerConverter(Texts.get("dashboardConfig.form.validation.error.int"));
		binder = new BeanValidationBinder<>(DashboardConfig.class);
		binder.setStatusLabel(statusLabel);
		binder.forField(numRows).withConverter(toIntegerConverter).bind(DashboardConfig::getNumRows, DashboardConfig::setNumRows);
		binder.forField(numColumns).withConverter(toIntegerConverter).bind(DashboardConfig::getNumColumns, DashboardConfig::setNumColumns);;
		binder.forField(refreshIntervalInSeconds).withConverter(toIntegerConverter).withNullRepresentation(0).
				bind(DashboardConfig::getRefreshIntervalInSeconds, DashboardConfig::setRefreshIntervalInSeconds);
		binder.bindInstanceFields(this);
		if (dashboardConfig.isNew()) {
			final DashboardConfig newDashboardConfig = (DashboardConfig) dashboardConfig;
			newDashboardConfig.setRefreshIntervalInSeconds(0); //TODO: Hack because binding crashes if this value is null
			binder.setBean(newDashboardConfig);
		} else {
			binder.setBean(dashboardConfig.load());
		}
		panelConfigs.refresh(binder.getBean().getPanels());
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		//TODO: Bean validation doesnt' work!
		if (binder.validate().isOk()) {
			final DashboardConfig bean = binder.getBean();
			bean.save();
			return true;
		}
		return false;
	}
}
