package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.*;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractLegacyComponent;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextField;
import no.goodtech.dashboard.config.fetcher.FetcherConfig;
import no.goodtech.dashboard.config.ui.AxisConfig;
import no.goodtech.dashboard.config.ui.SeriesConfig;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.chart.ColorUtils;
import no.goodtech.vaadin.formatting.PlainDoubleToStringConverter;
import no.goodtech.vaadin.layout.OneOfTheseFieldsAreRequiredValidator;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.ui.ListProvider;
import no.goodtech.vaadin.ui.v7.FieldCopyListener;

import java.util.Arrays;

public class SeriesConfigForm extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	private final BeanFieldGroup<SeriesConfig> binder;
	private final Label statusLabel = new Label();
	private final TextField name, fixedValue, lineWidth;
	private final SeriesIdField id;
	private final CheckBox showMarker, plotband;
	private final FetcherConfigComboBox fetcherConfig;
//	private final SeriesSourceTypeComboBox sourceTypeCombo;
	private final AxisConfigComboBox axisConfig;
	private final ColorPicker color;

	public SeriesConfigForm(SeriesConfig seriesConfig, ListProvider<AxisConfig> axes, SeriesConfigCrudPanel.IPalette palette) {
		setCaption(Texts.get("seriesConfig.form.caption"));

		id = new SeriesIdField(Texts.get("seriesConfig.form.id.caption"));
		name = new TextField(Texts.get("seriesConfig.form.name.caption"));
		fixedValue = new TextField(Texts.get("seriesConfig.form.fixedValue.caption"));
		lineWidth = new TextField(Texts.get("seriesConfig.form.lineWidth.caption"));
		showMarker = new CheckBox(Texts.get("seriesConfig.form.showMarker.caption"));
		plotband = new CheckBox(Texts.get("seriesConfig.form.plotband.caption"));

		fixedValue.setConverter(new PlainDoubleToStringConverter());

		fetcherConfig = new FetcherConfigComboBox(Texts.get("seriesConfig.form.fetcherConfig.caption"));
		fetcherConfig.refresh();
		fetcherConfig.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				id.refresh((FetcherConfig) event.getProperty().getValue());
//				sourceTypeCombo.refresh((FetcherConfig) event.getProperty().getValue());
			}
		});

		id.addValueChangeListener(new FieldCopyListener<>(name));

		axisConfig = new AxisConfigComboBox(Texts.get("seriesConfig.form.axisConfig.caption"), axes.getItems());
		axisConfig.refresh();

//		sourceTypeCombo = new SeriesSourceTypeComboBox("Type");
//		sourceTypeCombo.addValueChangeListener(new Property.ValueChangeListener() {
//			@Override
//			public void valueChange(Property.ValueChangeEvent event) {
//				id.refresh(fetcherConfig.getValue(), (SourceType) event.getProperty().getValue());
//			}
//		});

		color = new ColorPicker();
		color.setModal(true);
		color.setHSVVisibility(false);
		color.setRGBVisibility(false);
		color.setPopupStyle(AbstractColorPicker.PopupStyle.POPUP_NORMAL);
		color.setCaption(Texts.get("seriesConfig.form.color.caption"));

		addComponents(fetcherConfig, id, name, axisConfig, color, lineWidth, fixedValue, new HorizontalLayout(showMarker, plotband),
				statusLabel);
		setSizeFull();

		binder = new BeanFieldGroup<>(SeriesConfig.class);
		binder.bindMemberFields(this);
//		if (seriesConfig.getSourceType() != null) {
//			sourceTypeCombo.select(seriesConfig.getSourceType());
//		}

		for (AbstractLegacyComponent field : Arrays.asList(fetcherConfig, id, name, axisConfig, fixedValue)) {
			field.setWidth("100%");
			field.setImmediate(true);
		}

		for (TextField field : Arrays.asList(name, fixedValue, lineWidth)) {
			field.setNullRepresentation("");
		}

		new OneOfTheseFieldsAreRequiredValidator(fetcherConfig, fixedValue);

		binder.setItemDataSource((SeriesConfig) seriesConfig);

		String color = seriesConfig.getColor();
		if (color == null) {
			color = palette.getNextColor(); //automatically select a new color
		}
		//color picker doesn't support automatic binding, so we bind manually
		this.color.setValue(ColorUtils.hexToColorPickerColor(color));
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		try {
			binder.commit();
			final SeriesConfig seriesConfig = binder.getItemDataSource().getBean();
//			if (sourceTypeCombo.getValue() != null) {
//				seriesConfig.setSourceType(sourceTypeCombo.getValue().getId());
//			}
			seriesConfig.setColor(color.getValue().getCSS());
			return true;
		} catch (FieldGroup.CommitException e) {
			Notification.show(Texts.get("seriesConfig.form.validation.failed"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}
}
