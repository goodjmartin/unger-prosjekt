package no.goodtech.vaadin.search;

import com.vaadin.data.HasValue;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.vaadin.ui.Texts;


public class SimpleSearchComponent extends Composite {
	private TextField filter = new TextField();
	private Button searchButton = new Button(VaadinIcons.SEARCH);
	private Button clearButton = new Button(VaadinIcons.CLOSE_SMALL);
	protected CountLabel countLabel;
	private Label currentFiltersAsTextLabel = new Label();

	private HorizontalLayout headerLayout;
	private CssLayout filterLayout;

	public SimpleSearchComponent() {
		initLayout();
	}

	private void initLayout() {

		filter.setPlaceholder("Søk på nøkkelord...");
		filter.setWidth(400, Unit.PIXELS);
		filter.focus();
		filter.setValueChangeMode(ValueChangeMode.LAZY);

		searchButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		countLabel = new CountLabel();

		filterLayout = new CssLayout(filter, searchButton, clearButton);
		filterLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		headerLayout = new HorizontalLayout(filterLayout, countLabel, currentFiltersAsTextLabel);
		headerLayout.setComponentAlignment(filterLayout, Alignment.BOTTOM_LEFT);
		headerLayout.setComponentAlignment(countLabel, Alignment.BOTTOM_LEFT);

		setCompositionRoot(headerLayout);
	}


	public String getFilterString() {
		return filter.getValue();
	}

	public void clear() {
		filter.clear();
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public void addTextValueChangeListener(HasValue.ValueChangeListener listener) {
		filter.addValueChangeListener(listener);
	}

	public Button getClearButton() {
		return clearButton;
	}

	public void setFilterPrompt(String text) {
		filter.setPlaceholder(text);
	}

	public void setWidth(float width, Unit unit) {
		filter.setWidth(width, unit);
	}

	public void addComponents(Component... components){
		for (int i = 0 ; i < components.length; i++){
			headerLayout.addComponent(components[i], i+1);
		}

		//This is done to get the filterLayout and the other fields to be aligned. With the caption on the other fields and the fact that filterLayout is a CssLayout component which doesnt have a way to set padding or
		// margin i cant se another way to do this.
		filterLayout.setCaption(" ");
		headerLayout.setComponentAlignment(filterLayout, Alignment.TOP_RIGHT);
	}

	public void displayCount(Integer count, Integer fullCount) {
		if (count == null && fullCount == null) {
			// No counts are given
			setVisible(false);
		} else if (count == null || fullCount == null) {
			// Either count or full count is null - show simple label
			int singleCount = (count != null) ? count : fullCount;
			if (singleCount == 0) {
				countLabel.setValue(Texts.get("filterPanel.count.value.none"));
			} else {
				countLabel.setValue(String.format(Texts.get("filterPanel.count.value"), singleCount));
			}
		} else {
			// Both counts are given - show extended label
			setVisible(true);
			if (count == 0) {
				countLabel.setValue(Texts.get("filterPanel.count.value.none"));
			} else {
				countLabel.setValue(String.format(Texts.get("filterPanel.count.valueAndFound"), count, fullCount));
			}
		}
	}

	public void addComponent(Component component) {
		headerLayout.addComponent(component, headerLayout.getComponentCount() - 1);
		headerLayout.setComponentAlignment(component, Alignment.BOTTOM_LEFT);
	}

	public void setCurrentFiltersAsText(String currentFiltersAsText) {
		currentFiltersAsTextLabel.setValue(currentFiltersAsText);
	}
}
