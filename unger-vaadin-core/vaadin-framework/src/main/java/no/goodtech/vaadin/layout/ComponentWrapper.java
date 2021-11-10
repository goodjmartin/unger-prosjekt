package no.goodtech.vaadin.layout;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class ComponentWrapper extends VerticalLayout {
	private final MenuBar tools;
	private final MenuBar menuBar;

	public ComponentWrapper() {
		setWidth("100%");
		setMargin(false);
		setSpacing(false);
		tools = new MenuBar();
		tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
		menuBar=tools;
	}

	public VerticalLayout wrapComponent(final Component componentToBeWrapped) {
		//Create caption label
		Label caption = new Label(componentToBeWrapped.getCaption());
		caption.addStyleName(ValoTheme.LABEL_H4);
		caption.addStyleName(ValoTheme.LABEL_COLORED);
		caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		componentToBeWrapped.setCaption(null);

		//Create toolbar containing caption and tools
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.addStyleName("dashboard-panel-toolbar");
		toolbar.setWidth("100%");
		toolbar.addComponents(caption, tools);
		toolbar.setExpandRatio(caption, 1);
		toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);
		toolbar.setMargin(new MarginInfo(false, false, false, true));
		toolbar.setSpacing(false);

		//Create the layout containing the toolbar and component to be wrapped
		VerticalLayout card = new VerticalLayout();
		card.setSizeFull();
		card.setSpacing(false);
		card.setMargin(false);
		card.addStyleName(ValoTheme.LAYOUT_CARD);
		card.addComponents(toolbar, componentToBeWrapped);
		componentToBeWrapped.setSizeFull();
		card.setExpandRatio(componentToBeWrapped, 1.0F);
		addComponent(card);
		return card;
	}

	public MenuBar getTools() {
		return tools;
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

}
