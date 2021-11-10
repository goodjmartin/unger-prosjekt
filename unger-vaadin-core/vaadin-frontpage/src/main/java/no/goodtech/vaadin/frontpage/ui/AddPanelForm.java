package no.goodtech.vaadin.frontpage.ui;

import com.vaadin.data.HasValue;
import com.vaadin.ui.*;
import no.goodtech.vaadin.buttons.RefreshButton;
import no.goodtech.vaadin.frontpage.model.*;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.security.Globals;
import no.goodtech.vaadin.security.model.User;

import java.util.Map;

/**
 * View with a help text on top
 */
class AddPanelForm extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	private static final int PREVIEW_SIZE = 450; // Default size in pixels

	private ComboBox<IFrontPageCardComponent> panels;
	private HorizontalLayout argumentPanelLayout;
	private VerticalLayout preview;
	private RefreshButton refreshButton;
	private final User user;
	private RefreshButton.IRefreshListener refreshListener;

	public AddPanelForm(User user) {
		if (user == null && !Globals.isRequireLogin()) {
			this.user = new User("anonymous", "anonymous", null).save();
		} else {
			this.user = user;
		}

		setSizeFull();

		preview = new VerticalLayout();
		preview.setHeight(PREVIEW_SIZE, Unit.PIXELS);
		preview.setWidth("100%");

		argumentPanelLayout = new HorizontalLayout();

		HorizontalLayout selectionLayout = new HorizontalLayout();

		refreshButton = new RefreshButton(this::updatePreview);
		refreshButton.setVisible(false);

		panels = new ComboBox<>(getCaption("components"));
		panels.setEmptySelectionAllowed(false);
		panels.setWidth("100%");
		panels.setItemCaptionGenerator((ItemCaptionGenerator<IFrontPageCardComponent>) IFrontPageCardComponent::getViewName);
		panels.addValueChangeListener((HasValue.ValueChangeListener<IFrontPageCardComponent>) valueChangeEvent -> {
			if (valueChangeEvent.getValue() != null) {
				IFrontPageCardComponent component = valueChangeEvent.getValue();
				FrontPageFactory.getService().refreshComponent(component);
				if (component != null) {
					FrontPageCardInput argumentPanel = component.getInputPanel();
					argumentPanelLayout.removeAllComponents();
					if (argumentPanel == null) {
						updatePreview();
						refreshButton.setVisible(false);
					}else{
						argumentPanelLayout.addComponent(argumentPanel);
						preview.removeAllComponents();
						refreshButton.setVisible(true);
					}
				}

				if (refreshListener != null)
					refreshListener.refreshClicked();
			}
		});

		panels.setItems(FrontPageFactory.getService().getClasses());

		selectionLayout.addComponents(panels, argumentPanelLayout, refreshButton);
		selectionLayout.setComponentAlignment(refreshButton, Alignment.BOTTOM_CENTER);

		addComponents(selectionLayout, preview);
	}

	private void updatePreview(){
		preview.removeAllComponents();
		IFrontPageCardComponent component = panels.getValue();
		if (component != null) {
			if (argumentPanelLayout.getComponentCount() > 0) {
				Component argumentInputComponent = argumentPanelLayout.getComponent(0);
				if (argumentInputComponent != null && argumentInputComponent instanceof FrontPageCardInput)
					component.setArguments(((FrontPageCardInput) argumentInputComponent).getArguments());
			}
			component.setSizeFull();
			FrontPageFactory.getService().refreshComponent(component);
			preview.addComponent(component);
		}
	}

	public void setRefreshListener(RefreshButton.IRefreshListener refreshListener){
		this.refreshListener = refreshListener;
	}

	private static String getText(final String key) {
		return ApplicationResourceBundle.getInstance("vaadin-frontpage").getString(key);
	}

	@Override
	public Component getComponent() {
		return this;
	}


	@Override
	public boolean commit() {
		if (user != null && panels.getValue() != null && preview.getComponentCount() > 0) {
			int lastIndexNo = new FrontPageCardFinder().setUserId(user.getId()).getLastIndexNo();
			Component component = preview.getComponent(0);

			// Fetch arguments for component
			String args = null;
			if (component instanceof IFrontPageCardComponent) {
				Map<String, String> argsMap = ((IFrontPageCardComponent) component).getArguments();
				if (argsMap != null) {
					StringBuilder sb = new StringBuilder();
					for (String key : argsMap.keySet()) {
						if (sb.length() > 0)
							sb.append(FrontPageCard.ARGS_SEPARATOR);
						sb.append(key);
						sb.append(FrontPageCard.VALUE_SEPARATOR);
						sb.append(argsMap.get(key));
					}
					args = sb.toString();
				}
			}
			new FrontPageCard(user, panels.getValue().getClass().getName(), args, lastIndexNo + 1).save();
			return true;
		}
		return false;
	}

	private static String getCaption(String key){
		return ApplicationResourceBundle.getInstance("vaadin-frontpage").getString("addPanelForm." + key);
	}
}
