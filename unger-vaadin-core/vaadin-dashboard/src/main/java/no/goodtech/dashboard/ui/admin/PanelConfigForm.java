package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.*;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.validator.IntegerRangeValidator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.config.ui.PanelConfig;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.validation.CompareNumericFieldsValidator;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class PanelConfigForm extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	private final BeanFieldGroup<ChartConfig> binder; //TODO: Support more panel types
	private final Label statusLabel = new Label();
	private final TextField title, startRow, endRow, startColumn, endColumn, periodLengthInMinutes;
	private final TextField runningAverageMaxValueCount, runningAveragePrecision;
	private final CheckBox alignTicks, timeShift;
	private final SeriesConfigCrudPanel seriesConfigCrudPanel;
	private final AxisConfigCrudPanel axisConfigCrudPanel;
	private final SeriesConfigComboBox runningAverageSeries;

	public PanelConfigForm(ChartConfig panelConfig, DashboardConfigFormVaadin7.CoordinatesProvider coordinatesProvider) {

		setCaption(Texts.get("panelConfig.form.caption"));
		title = new TextField(Texts.get("panelConfig.form.title.caption"));
		startRow = new TextField(Texts.get("panelConfig.form.startRow.caption"));
		endRow = new TextField(Texts.get("panelConfig.form.endRow.caption"));
		startColumn = new TextField(Texts.get("panelConfig.form.startColumn.caption"));
		endColumn = new TextField(Texts.get("panelConfig.form.endColumn.caption"));
		periodLengthInMinutes = new TextField(Texts.get("panelConfig.form.periodLengthInMinutes.caption"));
		alignTicks = new CheckBox(Texts.get("panelConfig.form.alignTicks.caption"));
		timeShift = new CheckBox(Texts.get("panelConfig.form.timeShift.caption"));
		runningAverageSeries = new SeriesConfigComboBox(Texts.get("panelConfig.form.runningAverageSeries.caption"), panelConfig);
		runningAverageMaxValueCount = new TextField(Texts.get("panelConfig.form.runningAverageMaxValueCount.caption"));
		runningAveragePrecision = new TextField(Texts.get("panelConfig.form.runningAveragePrecision.caption"));
		axisConfigCrudPanel = new AxisConfigCrudPanel(panelConfig);
		seriesConfigCrudPanel = new SeriesConfigCrudPanel(panelConfig, axisConfigCrudPanel);

		runningAverageSeries.refresh();

		addComponents(new HorizontalLayout(title), new HorizontalLayout(startRow, endRow, startColumn, endColumn),
				periodLengthInMinutes, alignTicks, timeShift,
				new HorizontalLayout(runningAverageSeries, runningAverageMaxValueCount, runningAveragePrecision),
				axisConfigCrudPanel, seriesConfigCrudPanel, statusLabel);

		binder = new BeanFieldGroup<>(ChartConfig.class);
		binder.bindMemberFields(this);
		for (TextField field : Arrays.asList(title, startRow, endRow, startColumn, endColumn, periodLengthInMinutes,
				runningAverageMaxValueCount, runningAveragePrecision)) {
			field.setNullRepresentation("");
			field.setImmediate(true);
		}

		binder.setItemDataSource(panelConfig);
		createValidators(coordinatesProvider);
		title.setSizeFull();
	}

	private void createValidators(DashboardConfigFormVaadin7.CoordinatesProvider coordinatesProvider) {
		final IntegerRangeValidator numRowsValidator = createRangeValidator(coordinatesProvider.getNumRows());
		final IntegerRangeValidator numColumnsValidator = createRangeValidator(coordinatesProvider.getNumColumns());
		startRow.addValidator(numRowsValidator);
		endRow.addValidator(numRowsValidator);
		startColumn.addValidator(numColumnsValidator);
		endColumn.addValidator(numColumnsValidator);

		CompareNumericFieldsValidator.apply(startRow, endRow, CompareNumericFieldsValidator.Rule.LESS_THAN_OR_EQUAL);
		CompareNumericFieldsValidator.apply(startColumn, endColumn, CompareNumericFieldsValidator.Rule.LESS_THAN_OR_EQUAL);
	}

	private IntegerRangeValidator createRangeValidator(int maxValue) {
		final String message = String.format(Texts.get("panelConfig.form.validation.coordinates"), 1, maxValue);
		return new IntegerRangeValidator(message, 1, maxValue);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		try {
			//TODO: Validation of series and axis
			//TODO: Validation of panels
			binder.commit();
			final ChartConfig panelConfig = binder.getItemDataSource().getBean();
			panelConfig.setSeriesConfigs(new LinkedHashSet<>(seriesConfigCrudPanel.getItems())); //TODO: Automatic binding
			panelConfig.setyAxes(new LinkedHashSet<>(axisConfigCrudPanel.getItems())); //TODO: Automatic binding
			return true;
		} catch (FieldGroup.CommitException e) {
			Notification.show(Texts.get("dashboardConfig.form.validation.failed"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}

	public PanelConfig getPanelConfig() {
		return binder.getItemDataSource().getBean();
	}
}
