package no.goodtech.vaadin.remotecontrol.gui;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;
import no.goodtech.opc.NodeValue;
import no.goodtech.vaadin.buttons.RefreshButton;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.remotecontrol.metamodel.Screen;
import no.goodtech.vaadin.remotecontrol.metamodel.Widget;
import no.gooodtech.vaadin.remotecontrol.data.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.touchkit.ui.NavigationView;
import org.vaadin.touchkit.ui.NumberField;
import org.vaadin.touchkit.ui.VerticalComponentGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.vaadin.addon.touchkit.ui.NavigationView;
//import com.vaadin.addon.touchkit.ui.NumberField;
//import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;

public class TablePanel extends NavigationView {
	private static Logger LOGGER = LoggerFactory.getLogger(TablePanel.class);
	
	private final VerticalComponentGroup layout;
	private final SaveButton saveButton;
	private final Label messageLabel;

	private Map<Widget, Field> fields = new HashMap<Widget, Field>();

	public TablePanel(final Screen screen, IndexPage previousPage) {
		setCaption(screen.getTitle());
		layout = new VerticalComponentGroup();
		saveButton = new SaveButton(new SaveButton.ISaveListener() {
			@Override
			public void saveClicked() {
				final String errorMessage = DataSource.writeValues(getDirtyValues());
				if (errorMessage != null) {
					LOGGER.warn(errorMessage);
					messageLabel.setValue(errorMessage);
				}
				refresh(screen);
				saveButton.setEnabled(false);
			}
		});
		saveButton.setWidth("100%");

		setPreviousComponent(previousPage);
		RefreshButton refreshButton = new RefreshButton(new RefreshButton.IRefreshListener() {
			@Override
			public void refreshClicked() {
				refresh(screen);
			}
		});
		setRightComponent(refreshButton);

		saveButton.setEnabled(false);
		messageLabel = new Label();
		messageLabel.setContentMode(ContentMode.HTML);
		layout.addComponent(messageLabel);
		setContent(layout);
		refresh(screen);
	}

	private void refresh(Screen screen) {
		final List<Widget> widgets = screen.getWidgets();
		final Map<Widget, NodeValue> valuesPerWidget = DataSource.readValues(widgets);
		layout.removeAllComponents();
		List<String> errorMessages = new ArrayList<String>();
		messageLabel.setValue(null);
		GridLayout gridLayout = new GridLayout();
		int rowIndex = 0;
		int headerIndex = 1;
		for (String column : screen.getColumns()) {
			gridLayout.setRows(1);
			gridLayout.setColumns(headerIndex + 1);
			final Label columnHeader = new Label(column);
			columnHeader.addStyleName("columnHeader");
			gridLayout.addComponent(columnHeader, headerIndex, 0);
			headerIndex++;
			rowIndex = 1; //vi har lagt til en overskrifts-rad
		}
		final List<String> captions = screen.getCaptions();
		gridLayout.setRows(rowIndex + captions.size());
		gridLayout.setWidth("100%");
		for (String caption : captions) {
			Label label = new Label(caption);
			gridLayout.addComponent(label, 0, rowIndex);
			int columnIndex = 1;
			for (final Widget widget : screen.getWidgets(caption)) {
				final NodeValue nodeValue = valuesPerWidget.get(widget);
				final Field<?> field = createField(widget, nodeValue.getValue());
				//field.setSizeUndefined();
	
				final HorizontalLayout cell = new HorizontalLayout(field);
				cell.setSpacing(false);
				cell.setMargin(false);
				cell.setComponentAlignment(field, Alignment.MIDDLE_RIGHT);
				cell.setExpandRatio(field, 1.0F);
				cell.setWidth("100%");
				if (!nodeValue.isOk()) {
					if (cell.getComponentCount() == 1)
						cell.addComponent(new Image(null, new ThemeResource("images/red.png")));
					errorMessages.add(widget.getCaption() + ": " + widget.getTag() + ": " + nodeValue.getErrorMessage());
					LOGGER.warn("{}: nodeValue.getErrorMessage()", widget);
				}

				field.addValueChangeListener(new ValueChangeListener() {
					
					public void valueChange(ValueChangeEvent event) {
						refreshFieldDirtyStatus(widget, field, cell, event.getProperty().getValue());
					}
				});
				if (columnIndex >= gridLayout.getColumns())
					gridLayout.setColumns(columnIndex + 1);
				
				gridLayout.addComponent(cell, columnIndex, rowIndex);
				columnIndex++;
				fields.put(widget, field);
			}
			rowIndex++;
		}
		layout.addComponent(gridLayout);
		layout.addComponent(saveButton);
		messageLabel.setValue(formatMessage(errorMessages));
		layout.addComponent(messageLabel);
	}
	
	private String formatMessage(List<String> messages) {
		StringBuilder result = new StringBuilder();
		result.append("<html><ul>");
		for (String message : messages) {
			result.append("<li>");
			result.append(message);
			result.append("</li>");
		}
		result.append("</ul></html>");
		return result.toString();
	}

	protected boolean isAnyFieldsDirty() {
		for (Widget widget : fields.keySet())
			if (widget.isDirty())
				return true;
		return false;
	}

	private Map<Widget, Object> getDirtyValues() {
		Map<Widget, Object> values = new HashMap<Widget, Object>();
		for (Map.Entry<Widget, Field> entry : fields.entrySet()) {
			Widget widget = entry.getKey();
			if (widget.isDirty()) {
				final Field<?> field = entry.getValue();
				Object value = field.getValue();
				values.put(widget, value);
			}
		}
		return values;
	}

	private <T extends Field<?>> T createField(Widget widget, Object value) {
		Class<?> dataType = widget.getDataType();
		widget.setInitialValue(value);
		boolean hasOptions = widget.getOptions().size() > 0;
		T field = null;
		if (hasOptions)
			field = createNativeSelectField(value, widget.getOptions());
		else if (Boolean.class.isAssignableFrom(dataType))
			field = createSwitchField(value);
		else if (dataType.equals(Short.class) || dataType.equals(Integer.class))
			field = createNumberField(value);
		else if (dataType.equals(String.class))
			field = createTextField(value);
		else
			throw new IllegalArgumentException(widget.getCaption() + ": Ukjent datatype: '" + dataType + "'");	

		field.setReadOnly(widget.isReadOnly());
		field.addStyleName("remotecontrol-field");
		return field;
	}

	private <T extends Field> T createNativeSelectField(Object value, List<Object> options) {
		NativeSelect select = new NativeSelect();
		select.setHeight("30px");
		select.setNullSelectionAllowed(false);
		for (Object option : options)
			select.addItem(option);
		select.select(value);
		select.setImmediate(true);
		return (T) select;
	}

	private <T extends Field> T createNumberField(Object value) {
		final NumberField field = new NumberField();
		field.setImmediate(true);
		field.setValue(String.valueOf(value));
		field.addFocusListener(new FocusListener() {
			public void focus(FocusEvent event) {
				field.selectAll();
			}
		});
		return (T) field;
	}

	private <T extends Field> T createSwitchField(Object value) {
		CheckBox aSwitch = new CheckBox();
		aSwitch.setImmediate(true);
		aSwitch.setValue((Boolean) value);
		return (T) aSwitch;
	}
	
	private <T extends Field<?>> T createTextField(Object value) {
		final TextField field = new TextField(String.valueOf(value));
		field.setValue(String.valueOf(value));
		field.addFocusListener(new FocusListener() {
			public void focus(FocusEvent event) {
				field.selectAll();
			}
		});
		return (T) field;
	}

	private void refreshFieldDirtyStatus(final Widget widget, final Field<?> field, final HorizontalLayout cell, Object newValue) {
		widget.setValue(newValue);
		final Image dirtyIcon = new Image(null, new ThemeResource("images/yellow.png"));
		if (widget.isDirty()) {
			if (cell.getComponentCount() == 1)
				cell.addComponent(dirtyIcon);
			//dirtyIcon.setWidth(null);
			field.setStyleName("dirty");
			saveButton.setEnabled(true);
		} else {
			if (cell.getComponentCount() > 1) { 
				cell.removeAllComponents();
				cell.addComponent(field);
				cell.setExpandRatio(field, 1.0F);
				cell.setComponentAlignment(field, Alignment.MIDDLE_RIGHT);
			}
			saveButton.setEnabled(isAnyFieldsDirty());
		}
//		field.markAsDirty();
	}

}
