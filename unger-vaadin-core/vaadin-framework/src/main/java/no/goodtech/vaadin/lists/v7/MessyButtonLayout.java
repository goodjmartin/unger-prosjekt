package no.goodtech.vaadin.lists.v7;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.CopyButton;
import no.goodtech.vaadin.buttons.DetailButton;
import no.goodtech.vaadin.buttons.EditButton;
import no.goodtech.vaadin.buttons.RemoveButton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default customizable button layout. Use this whenever you want a row of buttons.
 */
public class MessyButtonLayout extends HorizontalLayout {
	protected Button addButton, copyButton, detailButton, removeButton, editButton;

	public MessyButtonLayout(AddButton addButton, RemoveButton removeButton, CopyButton copyButton, EditButton editButton, DetailButton detailButton) {
		this();
		this.addButton = addButton;
		this.removeButton = removeButton;
		this.copyButton = copyButton;
		this.editButton = editButton;
		this.detailButton = detailButton;

		refresh();
	}

	public MessyButtonLayout() {
		setMargin(false);
	}

	private void refresh() {
		List<Button> buttons = getButtons();
		removeAllComponents();
		if (this.addButton != null) {
			addComponent(this.addButton);
			buttons.remove(this.addButton);
		}
		if (this.removeButton != null) {
			addComponent(this.removeButton);
			buttons.remove(this.removeButton);
		}
		if (this.copyButton != null) {
			addComponent(this.copyButton);
			buttons.remove(this.copyButton);
		}
		if (this.editButton != null) {
			addComponent(this.editButton);
			buttons.remove(this.editButton);
		}
		if (this.detailButton != null) {
			addComponent(this.detailButton);
			buttons.remove(detailButton);
		}

		// Adds the rest of the buttons
		buttons.stream().filter(button -> button != null).forEach(this::addComponent);
	}

	public Button getAddButton() {
		return addButton;
	}

	public void setAddButton(AddButton addButton) {
		this.addButton = addButton;
		refresh();
	}

	public Button getCopyButton() {
		return copyButton;
	}

	public void setCopyButton(CopyButton copyButton) {
		this.copyButton = copyButton;
		refresh();
	}

	public Button getDetailButton() {
		return detailButton;
	}

	public void setDetailButton(DetailButton detailButton) {
		this.detailButton = detailButton;
		refresh();
	}

	public Button getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(RemoveButton removeButton) {
		this.removeButton = removeButton;
		refresh();
	}

	public Button getEditButton() {
		return editButton;
	}

	public void setEditButton(EditButton editButton) {
		this.editButton = editButton;
		refresh();
	}

	/**
	 * Use this to add another button to the panel
	 */
	public void addButton(Button button) {
		if (button != null) {
			super.addComponent(button);
			refresh();
		}
	}

	/**
	 * Enable/disable removeButton, editButton, detailButton and copyButton (all buttons that usually depends on a selected item)
	 */
	public void enableTableSelectionButtons(boolean enable){
		if(removeButton != null){
			removeButton.setEnabled(enable);
		}
		if(editButton != null){
			editButton.setEnabled(enable);
		}
		if(detailButton != null){
			detailButton.setEnabled(enable);
		}
		if(copyButton != null){
			copyButton.setEnabled(enable);
		}
	}

	public void enableAddButton(boolean enable){
		if(addButton != null){
			addButton.setEnabled(enable);
		}
	}

	public void enableAllButtons(boolean enable){
		for (Button button : getButtons()){
			button.setEnabled(enable);
		}
	}

	/**
	 * @return all buttons in this button layout
	 */
	public List<Button> getButtons() {
		return components.stream().filter(component -> component != null).map(component -> (Button) component).collect(Collectors.toList());
	}
}
