package no.goodtech.vaadin.security.tabs.accessrole;

import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.TwinColSelect;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.security.model.AccessRole;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: Rewrite this panel to use binding!
 *
 * @author bakke
 */
public class AccessRoleDetails extends VerticalLayout {

	private final TwinColSelect accessFunctionsTwinColSelect;
	private final TextField accessRoleIdTextField;
	private final TextField accessRoleDescriptionTextField;
	private final SaveButton saveButton;
	private volatile boolean updateAllowed = false;

	public AccessRoleDetails(final IAccessRoleActionListener actionListener) {
		// Add a horizontal layout for the text fields
		HorizontalLayout horizontal = new HorizontalLayout();
		horizontal.setMargin(new MarginInfo(false, false, true, false));
		addComponent(horizontal);

		// Add the access role id
		accessRoleIdTextField = new TextField(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleDetails.textField.accessRoleId"));
		accessRoleIdTextField.addStyleName("accessRoleIdTextField");
		accessRoleIdTextField.setImmediate(true);
		accessRoleIdTextField.setWidth(150, Unit.PIXELS);
		accessRoleIdTextField.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				saveButton.setEnabled(updateAllowed);
			}
		});
		horizontal.addComponent(accessRoleIdTextField);

		// Add the access role name
		accessRoleDescriptionTextField = new TextField(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleDetails.textField.accessRoleDescription"));
		accessRoleDescriptionTextField.addStyleName("accessRoleDescriptionTextField");
		accessRoleDescriptionTextField.setImmediate(true);
		accessRoleDescriptionTextField.setWidth(300, Unit.PIXELS);
		accessRoleDescriptionTextField.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				saveButton.setEnabled(updateAllowed);
			}
		});
		horizontal.addComponent(accessRoleDescriptionTextField);

		// Create the Save button
		saveButton = new SaveButton(new SaveButton.ISaveListener() {
			@Override
			public void saveClicked() {
				saveButton.setEnabled(false);

				// Find the selected access functions
				Set<String> selectedAccessFunctions = AccessFunctionManager.getAccessFunctions().stream().filter(accessFunction -> accessFunctionsTwinColSelect.isSelected(accessFunction)).map(AccessFunction::getId).collect(Collectors.toSet());

				// Notify about the Save operation
				actionListener.addAccessRoleSaveClicked(accessRoleIdTextField.getValue(), accessRoleDescriptionTextField.getValue(), selectedAccessFunctions);
			}
		});

		// Add the access function component
		accessFunctionsTwinColSelect = new TwinColSelect(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleDetails.optionGroup.accessFunctions"));
		accessFunctionsTwinColSelect.setLeftColumnCaption(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleDetails.optionGroup.accessFunctions.notSelected"));
		accessFunctionsTwinColSelect.setRightColumnCaption(ApplicationResourceBundle.getInstance("vaadin-security").getString("accessRoleDetails.optionGroup.accessFunctions.selected"));
		accessFunctionsTwinColSelect.setMultiSelect(true);
		accessFunctionsTwinColSelect.setNullSelectionAllowed(true);
		accessFunctionsTwinColSelect.setImmediate(true);
		accessFunctionsTwinColSelect.addValueChangeListener((Property.ValueChangeListener) event -> saveButton.setEnabled(updateAllowed));
		accessFunctionsTwinColSelect.setSizeFull();
		addComponent(accessFunctionsTwinColSelect);

		addComponent(saveButton);

		setExpandRatio(accessFunctionsTwinColSelect, 1);
		setSizeFull();
	}

	public void refresh(final AccessRole accessRole) {
		// Add options
		accessFunctionsTwinColSelect.removeAllItems();
		AccessFunctionManager.getAccessFunctions().stream().filter(accessFunction -> accessFunction != null && accessFunction.getDescription().equals("PropertyNotMapped ${factory.authorizeAreas}") == false).forEach(accessFunction -> {
			accessFunctionsTwinColSelect.addItem(accessFunction);
			accessFunctionsTwinColSelect.setItemCaption(accessFunction, accessFunction.getDescription());
		});

		// Find logged in user
		User user = (User) VaadinSession.getCurrent().getAttribute(Constants.USER);

		// Check if the user is allowed to perform updates
		if (AccessFunctionManager.isAuthorized(user, AccessRoleTab.ACCESS_ROLE_UPDATE)) {
			updateAllowed = true;
		}

		if (accessRole != null) {
			// Already assigned access functions should be marked as such
			for (AccessFunction accessFunction : AccessFunctionManager.getAccessFunctions()) {
				if (accessRole.getAccessFunctionIds().contains(accessFunction.getId())) {
					accessFunctionsTwinColSelect.select(accessFunction);
				} else {
					accessFunctionsTwinColSelect.unselect(accessFunction);
				}
			}

			// Update the text fields
			accessRoleIdTextField.setValue(accessRole.getId());
			accessRoleDescriptionTextField.setValue(accessRole.getDescription());
		} else {
			// Clear all access functions
			AccessFunctionManager.getAccessFunctions().forEach(accessFunctionsTwinColSelect::unselect);

			// Clear the text fields
			accessRoleIdTextField.setValue("");
			accessRoleDescriptionTextField.setValue("");
		}

		// Disable the save button
		saveButton.setEnabled(false);

		// Set the enabled status
		accessRoleIdTextField.setEnabled(updateAllowed);
		accessRoleDescriptionTextField.setEnabled(updateAllowed);
		accessFunctionsTwinColSelect.setEnabled(updateAllowed);
	}
}
