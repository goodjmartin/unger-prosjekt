package no.goodtech.vaadin.search;

import com.vaadin.ui.ComboBox;

import java.util.List;

import static no.goodtech.vaadin.utils.Utils.getEntityCaption;

/**
 * ComboBox for printing simpleNames from a list of Classes
 */
public class OwnerClassComboBox extends ComboBox<Class<?>> {
	public OwnerClassComboBox(String caption) {
		super(caption);
	}

	public void refresh(List<Class<?>> ownerClasses) {

		setItems(ownerClasses);
		setItemCaptionGenerator(clazz -> getEntityCaption(clazz));

		if (ownerClasses.size() > 0) {
			setValue(ownerClasses.get(0));
		}
	}
}
