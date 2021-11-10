package no.goodtech.vaadin.lists.v7;

import com.vaadin.v7.ui.ComboBox;

import java.util.ArrayList;
import java.util.List;

/**
 * A combobox that remembers selected item after a refresh
 * @param <T>
 */
@Deprecated
public abstract class MessyComboBox<T> extends ComboBox {

	private final List<ValueChangeListener> valueChangeListeners = new ArrayList<>();

	public MessyComboBox(String caption) {
		super(caption);
	}

	/**
	 * @return the unique ID of the given item
	 */
	protected abstract String getId(T item);

	/**
	 * @return the caption of the given item
	 */
	protected abstract String getName(T item);

	public void refresh(List<? extends T> items) {
		T currentValue = getValue();

		// Temporary disable value change listeners, since removeAllItems() triggers value change event
		for (ValueChangeListener valueChangeListener : valueChangeListeners) {
			this.removeValueChangeListener(valueChangeListener);
		}

		setValue(null); // Forget what we have previously selected
		removeAllItems();
		for (T item : items) {
			addItem(item);
			setItemCaption(item, getName(item));
		}

		// Enable value change listeners again
		for (ValueChangeListener valueChangeListener : valueChangeListeners) {
			this.addValueChangeListener(valueChangeListener);
		}

		// select previously selected item again if not removed from list
		if (currentValue != null && containsId(currentValue)) {
			setValue(currentValue);
			fireValueChange(false);
		}
	}

	public T getValue() {
		return (T) super.getValue();
	}

	/**
	 * Select the item with given ID
	 * @param id the ID of the item you want to select, if found. If not found nothing happens
	 */
	public void select(String id) {
		if (id == null) {
			setValue(null);
		} else {
			for (Object item : getItemIds()) {
				T t = (T) item;
				if (id.equals(getId(t))) {
					setValue(item);
					return;
				}
			}
		}
	}

	public void addValueChangeListener(final ValueChangeListener valueChangeListener) {
		super.addValueChangeListener(valueChangeListener);
		if (!valueChangeListeners.contains(valueChangeListener)) {
			valueChangeListeners.add(valueChangeListener);
		}
	}
}
