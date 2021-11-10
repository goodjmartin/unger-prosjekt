package no.goodtech.vaadin.layout;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;

/**
 * ContentWrapper for a component, adds a horizontalLayout on top
 * of the component displaying the components caption and a toolbar
 */
public class ContentWrapper extends CssLayout {
	private final CssLayout panelComponentContainer;
	private final MenuBar tools;

	/**
	 * Builds the componentWrapper
	 *
	 * @param componentContainer   the container which will contain the wrapped component,
	 *                             needed to hide/display other components when resize button is clicked
	 */
	public ContentWrapper(final CssLayout componentContainer) {
		this.panelComponentContainer = componentContainer;

		//Set style options
		setWidth("100%");
		addStyleName("dashboard-panel-slot");

		//Create menuBar
		tools = new MenuBar();
		tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);

		//Create button for expanding component
		MenuBar.MenuItem maxButton = tools.addItem("", FontAwesome.EXPAND, (MenuBar.Command) selectedItem -> {
			if (!getStyleName().contains("max")) {
				selectedItem.setIcon(FontAwesome.COMPRESS);
				selectedItem.setDescription("Minimer");
				toggleMaximized(this, true);
			} else {
				this.removeStyleName("max");
				selectedItem.setIcon(FontAwesome.EXPAND);
				selectedItem.setDescription("Utvid");
				toggleMaximized(this, false);
			}
		});
		maxButton.setDescription("Utvid");
		maxButton.setStyleName("icon-only");
	}

	public void wrapComponent(final Component componentToBeWrapped) {
		//Create caption label
		Label caption = new Label(componentToBeWrapped.getCaption());
		caption.setId("caption-label");
		caption.addStyleName("caption-label");
		caption.addStyleName(ValoTheme.LABEL_H4);
		caption.addStyleName(ValoTheme.LABEL_COLORED);
		caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
		componentToBeWrapped.setCaption(null);

		//Create toolbar containing caption and tools
		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.setSpacing(false);
		toolbar.setMargin(false);
		toolbar.addStyleName("dashboard-panel-toolbar");
		toolbar.setWidth("100%");
		toolbar.addComponents(caption, tools);
		toolbar.setExpandRatio(caption, 1);
		toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

		//Create the layout containing the toolbar and component to be wrapped
		CssLayout card = new CssLayout();
		card.setWidth("100%");
		card.addStyleName(ValoTheme.LAYOUT_CARD);
		card.addComponents(toolbar, componentToBeWrapped);
		componentToBeWrapped.setWidth("100%");
		addComponent(card);
	}

	/**
	 * Toggles maximized style on/off for a component
	 *
	 * @param panel     the component to set maximized or not
	 * @param maximized true to set maximized, false otherwize
	 */
	private void toggleMaximized(final Component panel, final boolean maximized) {
		panelComponentContainer.setVisible(true);
		for (Component c : panelComponentContainer) {
			c.setVisible(!maximized);
		}
		if (maximized) {
			panel.setVisible(true);
			panel.addStyleName("max");
		} else {
			panel.removeStyleName("max");
		}
	}

	public MenuBar getTools() {
		return tools;
	}

}
