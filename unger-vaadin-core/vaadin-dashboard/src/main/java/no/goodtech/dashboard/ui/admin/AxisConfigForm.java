package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.*;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;
import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.dashboard.config.ui.ChartConfig;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.formatting.PlainDoubleToStringConverter;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.utils.UniqueValidator;

import java.util.Arrays;

public class AxisConfigForm extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	private final BeanFieldGroup<AxisConfig> binder;
	private final Label statusLabel = new Label();
	private final TextField name, minValue, maxValue, tickInterval, lineWidth, tickWidth, gridLineWidth;
	private final CheckBox opposite, hideAlternateGrid;

	public AxisConfigForm(AxisConfig axisConfig, ChartConfig chartConfig) {
		setCaption(Texts.get("axisConfig.form.caption"));

		name = new TextField(Texts.get("axisConfig.form.name.caption"));
		minValue = new TextField(Texts.get("axisConfig.form.minValue.caption"));
		maxValue = new TextField(Texts.get("axisConfig.form.maxValue.caption"));
		tickInterval = new TextField(Texts.get("axisConfig.form.tickInterval.caption"));
		lineWidth = new TextField(Texts.get("axisConfig.form.lineWidth.caption"));
		tickWidth = new TextField(Texts.get("axisConfig.form.tickWidth.caption"));
		gridLineWidth = new TextField(Texts.get("axisConfig.form.gridLineWidth.caption"));
		opposite = new CheckBox(Texts.get("axisConfig.form.opposite.caption"));
		hideAlternateGrid = new CheckBox(Texts.get("axisConfig.form.hideAlternateGrid.caption"));

		addComponents(new HorizontalLayout(name, minValue, maxValue),
				new HorizontalLayout(tickInterval, lineWidth, tickWidth, gridLineWidth), opposite, hideAlternateGrid,
				statusLabel);
		setSizeFull();

		binder = new BeanFieldGroup<>(AxisConfig.class);
		binder.bindMemberFields(this);

		name.addValidator(new UniqueValidator(binder, new UniqueValidator.IDuplicateCandidateProvider() {
			@Override
			public Object findObjectWithSameCharacteristics() {
				for (AxisConfig config : chartConfig.getyAxes()) {
					if (config.getName().equals(name.getValue())) {
						return config; //found an axis with same name in the list; axis name must be unique inside same chart
					}
				}
				return null;
			}
		}));
		for (TextField field : Arrays.asList(name, minValue, maxValue, tickInterval, lineWidth, tickWidth, gridLineWidth)) {
			field.setNullRepresentation("");
			field.setImmediate(true);
		}
		for (TextField field : Arrays.asList(minValue, maxValue, tickInterval, lineWidth, tickWidth)) {
			field.setConverter(new PlainDoubleToStringConverter());
		}

		binder.setItemDataSource((AxisConfig) axisConfig);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		try {
			binder.commit();
			final AxisConfig axisConfig = binder.getItemDataSource().getBean();
			return true;
		} catch (FieldGroup.CommitException e) {
			Notification.show(Texts.get("axisConfig.form.validation.failed"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}
}
