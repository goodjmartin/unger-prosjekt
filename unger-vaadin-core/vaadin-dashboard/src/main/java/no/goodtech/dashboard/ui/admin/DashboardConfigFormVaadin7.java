package no.goodtech.dashboard.ui.admin;

import com.vaadin.ui.*;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.TextField;
import no.goodtech.dashboard.config.ui.DashboardConfig;
import no.goodtech.dashboard.config.ui.DashboardConfigFinder;
import no.goodtech.dashboard.config.ui.DashboardConfigStub;
import no.goodtech.dashboard.ui.Texts;
import no.goodtech.vaadin.category.ui.CategoryComboBox;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.properties.ui.form.PropertyValueForm;
import no.goodtech.vaadin.ui.v7.FieldCopyListener;
import no.goodtech.vaadin.utils.UniqueValidator;

import java.util.Arrays;

public class DashboardConfigFormVaadin7 extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	public interface CoordinatesProvider {
		int getNumRows();
		int getNumColumns();
	}

	private final BeanFieldGroup<DashboardConfig> binder;
	private final Label statusLabel = new Label();
	private final TextField id, title, numRows, numColumns, refreshIntervalInSeconds;
	private final PanelConfigCrudPanel panelConfigs;
	private final CategoryComboBox area;
	private final PropertyValueForm propertyValueForm;


	public DashboardConfigFormVaadin7(DashboardConfigStub dashboardConfig) {
		setCaption(Texts.get("dashboardConfig.form.caption"));
		id = new TextField(Texts.get("dashboardConfig.form.id.caption"));
		title = new TextField(Texts.get("dashboardConfig.form.title.caption"));
		area = new CategoryComboBox(Texts.get("dashboardConfig.form.area.caption"), DashboardConfig.class);
		numRows = new TextField(Texts.get("dashboardConfig.form.numRows.caption"));
		numColumns = new TextField(Texts.get("dashboardConfig.form.numColumns.caption"));
		refreshIntervalInSeconds = new TextField(Texts.get("dashboardConfig.form.refreshIntervalInSeconds.caption"));

		propertyValueForm = new PropertyValueForm();
		propertyValueForm.refresh((DashboardConfig) dashboardConfig);

		binder = new BeanFieldGroup<>(DashboardConfig.class);
		binder.bindMemberFields(this);
		for (TextField field : Arrays.asList(id, title, numRows, numColumns, refreshIntervalInSeconds)) {
			field.setNullRepresentation("");
			field.setImmediate(true);
		}
		area.setNullSelectionAllowed(true);
		area.refresh();

		if (dashboardConfig.isNew()) {
			binder.setItemDataSource((DashboardConfig) dashboardConfig);
		} else {
			binder.setItemDataSource(dashboardConfig.load());
		}

		panelConfigs = new PanelConfigCrudPanel(binder.getItemDataSource().getBean(), new CoordinatesProvider() {
			@Override
			public int getNumRows() {
				return getNumericValue(numRows);
			}

			@Override
			public int getNumColumns() {
				return getNumericValue(numColumns);
			}
		});
		panelConfigs.setSizeFull();

		final Label space = new Label("");
		addComponents(new HorizontalLayout(id, title), new HorizontalLayout(numRows, numColumns),
				new HorizontalLayout(area, refreshIntervalInSeconds), statusLabel, propertyValueForm, space, panelConfigs);
		setSizeFull();

		createValidators();
		id.addValueChangeListener(new FieldCopyListener<>(title));
	}

	private void createValidators() {
		id.addValidator(new UniqueValidator(binder, new UniqueValidator.IDuplicateCandidateProvider() {
			@Override
			public Object findObjectWithSameCharacteristics() {
				return new DashboardConfigFinder().setId(id.getValue()).find();
			}
		}));
		numRows.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value != null) {
					int numRows = (int) value;
					if (binder.getItemDataSource().getBean().getMinimumNumRowsNeeded() > numRows) {
						throw new InvalidValueException(Texts.get("dashboardConfig.form.numRows.validation.tosmall"));
					}
				}
			}
		});
		numColumns.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value != null) {
					int numColumns = (int) value;
					if (binder.getItemDataSource().getBean().getMinimumNumColumnsNeeded() > numColumns) {
						throw new InvalidValueException(Texts.get("dashboardConfig.form.numColumns.validation.tosmall"));
					}
				}
			}
		});
	}

	private int getNumericValue(TextField field) {
		final String value = field.getValue();
		if (value == null || value.trim().isEmpty()) {
			return 0;
		} else {
			return Integer.valueOf(value.trim());
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		try {
			//TODO: Validation of panels
			binder.commit();
			propertyValueForm.commit();
			final DashboardConfig bean = binder.getItemDataSource().getBean();
			bean.save();
			propertyValueForm.save();
			return true;
		} catch (FieldGroup.CommitException e) {
		}
		Notification.show(Texts.get("dashboardConfig.form.validation.failed"), Notification.Type.WARNING_MESSAGE);
		return false;
	}
}
