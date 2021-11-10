package no.goodtech.vaadin.search;

import com.vaadin.ui.ComboBox;
import no.goodtech.persistence.jpa.AbstractFinder;

import java.util.List;

/**
 * ComboBox for printing entity-toStrings based on its Class and pk
 */
public class OwnerComboBox extends ComboBox {
	public OwnerComboBox(String caption) {
		super(caption);
		setEmptySelectionAllowed(true);

	}

	public void refresh(Class<?> ownerClass, List<Long> pks) {
		if (ownerClass == null || pks == null || pks.size() == 0) {
			clear();
			return;
		}

		try {
			String finderName = ownerClass.getName() + "Finder";
			Class<?> finderClass = Class.forName(finderName);
			// TODO look for more reliable way?
			if (finderClass != null) {
				AbstractFinder<?, ?, ?> finder = (AbstractFinder<?, ?, ?>) finderClass.newInstance();
				finder.setPk(pks);
				setItems(finder.list());
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
