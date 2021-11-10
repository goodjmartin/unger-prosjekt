package no.goodtech.vaadin.breadcrumb;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;


public class ViewTitle extends HorizontalLayout {

	private final HorizontalLayout horizontalLayout;

	public ViewTitle() {
		setWidth(100, Unit.PERCENTAGE);
		addStyleName("viewTitle");
		Label label = new Label();
		addComponents(horizontalLayout = new HorizontalLayout(), label);
		//horizontalLayout.setMargin(false);
		setExpandRatio(label, 1.0f);
		setSpacing(true);
		setMargin(true);
	}

	public void insertTrail(final String trailName) {
		// Add bread crumb separator
		if (horizontalLayout.getComponentCount() > 0) {
			Label label = new Label();
			label.setWidth(1, Unit.PIXELS);
			label.setStyleName("viewTitleSeparator");
			horizontalLayout.addComponent(label, 0);
		}

		// Add new bread crumb entry
		horizontalLayout.addComponent(new Label(trailName), 0);
	}

	public void removeTrails() {
		horizontalLayout.removeAllComponents();
	}

	public boolean isComponentsAdded() {
		return horizontalLayout.getComponentCount() > 0;
	}

}
