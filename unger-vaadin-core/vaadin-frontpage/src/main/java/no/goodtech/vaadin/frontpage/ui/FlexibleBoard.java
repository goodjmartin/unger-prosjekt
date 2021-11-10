package no.goodtech.vaadin.frontpage.ui;

import com.vaadin.event.LayoutEvents;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.frontpage.model.FrontPageFactory;
import no.goodtech.vaadin.frontpage.model.FrontPageCard;
import no.goodtech.vaadin.frontpage.model.FrontPageCardFinder;
import no.goodtech.vaadin.layout.ContentWrapper;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.ConfirmWindow;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.security.model.User;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.ui.Texts;
import no.goodtech.vaadin.frontpage.model.IFrontPageCardComponent;

import java.util.List;

public class FlexibleBoard extends Panel {

	private CssLayout mainLayout;
	private AddButton addButton;
	private Button hideAddButtonButton;

	public FlexibleBoard() {
		mainLayout = new CssLayout();
		mainLayout.addStyleName("dashboard-panels");
		mainLayout.setSizeFull();

		hideAddButtonButton = new Button(getCaption("hideAddButton.hide"), (Button.ClickListener) event -> {
			if (addButton != null){
				addButton.setVisible(!addButton.isVisible());
				changeHideAddButtonButton();
			}
		});

		VerticalLayout root = new VerticalLayout();
		root.addStyleName("flexible-board-view");
		root.setSizeFull();
		root.addComponent(mainLayout);
		root.addComponent(hideAddButtonButton,0);
		root.setExpandRatio(mainLayout, 1);
		root.setComponentAlignment(hideAddButtonButton, Alignment.TOP_RIGHT);

		Responsive.makeResponsive(mainLayout);
		Responsive.makeResponsive(root);
		Responsive.makeResponsive(this);

		addStyleName(ValoTheme.PANEL_BORDERLESS);
		setSizeFull();
		setContent(root);
	}

	private void changeHideAddButtonButton(){
		if (!addButton.isVisible()){
			hideAddButtonButton.setCaption(getCaption("hideAddButton.show"));
			hideAddButtonButton.setIcon(VaadinIcons.EXPAND);
		}else{
			hideAddButtonButton.setCaption(getCaption("hideAddButton.hide"));
			hideAddButtonButton.setIcon(VaadinIcons.COMPRESS);
		}
	}

	private void addAddButton() {
		boolean isVisible = (addButton == null) || addButton.isVisible();
		addButton = new AddButton(this::openAddPanel);
		addButton.setSizeFull();
		addButton.setVisible(isVisible);
		addButton.setIconAlternateText(addButton.getCaption());
		addButton.setDescription(addButton.getCaption());
		addButton.setCaption("");
		addButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		addButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		addButton.addStyleName(ValoTheme.BUTTON_HUGE);
		addButton.addStyleName("front-page-card-add-button");
		mainLayout.addComponent(addButton);
	}

	private void openAddPanel() {
		no.goodtech.vaadin.login.User user = (no.goodtech.vaadin.login.User) VaadinSession.getCurrent().getAttribute(Constants.USER);
		User dbUser = new UserFinder().setId(user.getId()).find();
		SimpleInputBox.IinputBoxContent form = new AddPanelForm(dbUser);
		final String okCaption = Texts.get("simpleCrudAdminPanel.popup.save.caption");
		final String cancelCaption = Texts.get("simpleCrudAdminPanel.popup.cancel.caption");
		final SimpleInputBox popup = new SimpleInputBox(form, okCaption, cancelCaption, this::refresh);
		((AddPanelForm) form).setRefreshListener(popup::center);
		UI.getCurrent().addWindow(popup);
		popup.setWidth(70, Unit.PERCENTAGE);
		popup.focus();
	}

	public void refresh() {
		mainLayout.removeAllComponents();
		no.goodtech.vaadin.login.User user = (no.goodtech.vaadin.login.User) VaadinSession.getCurrent().getAttribute(Constants.USER);
		List<FrontPageCard> frontPageCardList = new FrontPageCardFinder().setUserId(user.getId()).list();
		for (FrontPageCard card : frontPageCardList) {
			IFrontPageCardComponent component = (IFrontPageCardComponent) FrontPageFactory.getService().getComponentFromCard(card);
			if (component != null) {
				component.setSizeFull();
				component.setCaption(component.getViewName());
				mainLayout.addComponent(getWrapperFromComponent(card, component));
			}
		}

		addAddButton();
		changeHideAddButtonButton();
	}

	private ContentWrapper getWrapperFromComponent(FrontPageCard card, IFrontPageCardComponent component) {
		ContentWrapper wrapper = new ContentWrapper(mainLayout);
		wrapper.setStyleName("front-page-card");
		wrapper.wrapComponent(component);
		wrapper.getTools().addItem("", VaadinIcons.TRASH, (MenuBar.Command) menuItem -> {
			menuItem.setDescription(getCaption("button.remove"));
			ConfirmWindow confirmWindow = new ConfirmWindow(getCaption("confirm.message"), new ConfirmWindow.IConfirmWindowListener() {
				@Override
				public void onAccept() {
					card.delete();
					refresh();
				}

				@Override
				public void onCancel() {
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
			confirmWindow.focus();
		});

		// Add click listener if parent view id is set
		if (component.getParentViewId() != null) {
			wrapper.addLayoutClickListener((LayoutEvents.LayoutClickListener) event -> {
				if (event.getClickedComponent() != null && "caption-label".equals(event.getClickedComponent().getId())) {
					Object[] args = FrontPageFactory.getService().argumentMapToStringArray(component.getArguments());
					UI.getCurrent().getNavigator().navigateTo(new UrlUtils().createUrl(component.getParentViewId(), args));
				}
			});
		}
		return wrapper;
	}

	private static String getCaption(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-frontpage").getString("flexibleBoard." + key);
	}
}
