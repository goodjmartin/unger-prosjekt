package no.goodtech.vaadin.utils;

import com.vaadin.ui.Component;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controls a list of fields. Call addListenersToFields and the components given vil be enabled/disabled
 * based upon the validation of the fields each time a field is changed.
 * If all fields is ok and there has been a change to a field the components is enabled.
 */
public class FormChangeUtils {
	private Collection<Field<?>> fields;

	private List<Component> components; // Components to be enabled/disabled

	private IFormChangedListener listener;

	/**
	 *
	 * @param components
	 * @param listener nullable
	 * @param fields
	 */
	@SafeVarargs
	public static void control(List<Component> components, IFormChangedListener listener, Collection<Field<?>> ... fields){
		for (Collection<Field<?>> collection : fields){
			new FormChangeUtils(components, listener, collection);
		}
	}

	/**
	 *
	 * @param component
	 * @param listener
	 * @param fields
	 */
	@SafeVarargs
	public static void control(Component component, IFormChangedListener listener, Collection<Field<?>> ... fields){
		ArrayList<Component> components = new ArrayList<>();
		components.add(component);
		for (Collection<Field<?>> collection : fields){
			new FormChangeUtils(components, listener, collection);
		}
	}

	private FormChangeUtils(List<Component> components, IFormChangedListener listener, Collection<Field<?>> fields){
		this.fields = fields;
		this.components = components;
		this.listener = listener;
		setComponentsEnabled(false);
		addListenersToFields();
	}

	private void setComponentsEnabled(boolean enabled){
		for(Component component : components){
			if(component != null) {
				component.setEnabled(enabled);
			}
		}
		if(listener != null) {
			listener.formHasChanged(enabled);
		}
	}

	/**
	 * Call this to implement listeners to every field
	 */
	private void addListenersToFields(){
		for (Field field : fields){
			addListenersToField(field);
		}
	}

	/**
	 * Adds a ValueChangeListener to the field.
	 * Adds a TextChangeListener as well if the field is instance of TextArea/TextFields.
	 */
	private void addListenersToField(Field field){
		if(field != null) {
			if (field instanceof TextArea) {
				((TextArea) field).addTextChangeListener(textChangeEvent -> formHasChanged());
			} else if (field instanceof TextField) {
				((TextField) field).addTextChangeListener(textChangeEvent -> formHasChanged());
			}
			field.addValueChangeListener(valueChangeEvent -> formHasChanged());
		}
	}

	/**
	 * Edit the components to enabled/disabled based on the boolean value of isValid()
	 */
	private void formHasChanged(){
		if(components != null && components.size()>0){
			setComponentsEnabled(isValid());
		}
	}

	/**
	 * Iterates over all fields and return true if they validates ok.
	 * @return true: all fields ok, false: at least one field is not ok
	 */
	private boolean isValid(){
		try {
			for (Field field : fields) {
				if(field != null) {
					field.validate();
				}
			}
			return true;
		}catch (Validator.InvalidValueException ex){
			return false;
		}
	}
}
