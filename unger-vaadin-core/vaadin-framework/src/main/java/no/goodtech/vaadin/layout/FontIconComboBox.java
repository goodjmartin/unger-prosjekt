package no.goodtech.vaadin.layout;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.IconGenerator;
import no.goodtech.vaadin.utils.Utils;

import java.util.ArrayList;

/**
 * TODO replace FontAwesome with VaadinIcons
 */
public class FontIconComboBox extends ComboBox<String> {
	
	public FontIconComboBox() {
		ArrayList<String> items = new ArrayList<>();
		for (FontAwesome fontAwesome : FontAwesome.values()) {
			items.add(fontAwesome.name());
		}
		setItems(items);
		setItemIconGenerator((IconGenerator<String>) Utils::findIcon);
	}
	
	@Override
	public void setValue(String value) {
		if (value == null) {
			super.setValue(null);
		} else {
			final FontAwesome icon = no.goodtech.vaadin.utils.Utils.findIcon(value);
			if (icon != null)
				super.setValue(icon.name());
			else 
				com.vaadin.ui.Notification.show("TODO: Fant ikke ikon med navn: '" + value + "'");
		}
	}
}
