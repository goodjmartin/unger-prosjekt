package no.goodtech.vaadin.search;

import com.vaadin.ui.HorizontalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * This panel contains a ComboBox for filtering on ownerClass.
 * To make this panel work smoothly, the ownerClass needs a finder called <ownerClassName>Finder and a toString()
 * to make the print pretty.
 */
public class OwnerFilterPanel extends HorizontalLayout {
	private OwnerClassComboBox ownerClassComboBox;
	private final String ownerClassCaption;
	private final Class<?> clazz;

	public OwnerFilterPanel(Class<?> clazz, String ownerClassCaption) {
		this.clazz = clazz;
		this.ownerClassCaption = ownerClassCaption;
		setMargin(false);
	}

	/**
	 * Refreshes the ownerClass combobox and adds a valueChangeListener for populating the owner combobox
	 */
	public void refresh() {
		List<String> classesS = new OwnerFinder(clazz).listOwnerClasses();
		List<Class<?>> classes = new ArrayList<>();
		for (String c : classesS) {
			try {
				classes.add(Class.forName(c));
			} catch (ClassNotFoundException ignored) {
			}
		}
		refresh(classes);
	}

	private void refresh(List<Class<?>> classes) {
		removeAllComponents();

		ownerClassComboBox = new OwnerClassComboBox(ownerClassCaption);

		addComponents(ownerClassComboBox);

		if (classes != null && classes.size() > 0) {
			ownerClassComboBox.refresh(classes);
		}
	}

	public Class<?> getSelectedOwnerClass() {
		if (ownerClassComboBox != null) {
			return (Class<?>) ownerClassComboBox.getValue();
		}
		return null;
	}
}
