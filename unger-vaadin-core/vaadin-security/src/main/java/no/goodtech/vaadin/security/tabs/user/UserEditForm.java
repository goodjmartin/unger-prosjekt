package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.Setter;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import no.goodtech.vaadin.global.VaadinSpringContextHelper;
import no.goodtech.vaadin.main.SimpleInputBox.IinputBoxContent;
import no.goodtech.vaadin.security.ForgotPasswordService;
import no.goodtech.vaadin.security.MD5Hash;
import no.goodtech.vaadin.security.SelfRegistration;
import no.goodtech.vaadin.security.model.*;
import no.goodtech.vaadin.security.ui.Texts;
import no.goodtech.vaadin.utils.UniqueIdValidator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Skjema for å redigere bruker
 */
public class UserEditForm extends VerticalLayout implements IinputBoxContent {

	public enum GroupMembershipMode {
		HIDDEN, READONLY, EDITABLE
	}

	private final TextField id = createTextField("user.id", 50);
	private final TextField name = createTextField("user.textField.userName", 50);
	private final TextField email = createTextField("user.textField.email", 255);
	private final PasswordField newPassword = new PasswordField(Texts.get("user.textField.password"));
	private final CheckBoxGroup<PersonnelClassStub> personnelClasses = createPersonnelClassOptions(Texts.get("user.group.membership"));
	private final AccessRolesOptionGroup accessRoles = new AccessRolesOptionGroup();
	private final CheckBox blocked = new CheckBox(Texts.get("user.blocked"));
	private final Binder<User> binder;
	private boolean isReadOnly;

	public UserEditForm() {
		binder = new Binder<>(User.class);

		newPassword.setMaxLength(50);
		newPassword.setWidth("100%");

		binder.forField(id).asRequired("En unik id er påkrevd").withValidator(new UniqueIdValidator<>(new UserFinder(), binder)).bind("id");
		binder.bind(name, "name");
		Binder.BindingBuilder emailBindingBuilder = binder.forField(email).withNullRepresentation("");
		if (VaadinSpringContextHelper.getBean(ForgotPasswordService.class).isAvailable()){
			emailBindingBuilder = emailBindingBuilder.asRequired("Bruker må være tilknyttet en email").withValidator(new EmailValidator("Ikke gyldig email"));
		}
		emailBindingBuilder.bind("email");
		binder.bind(blocked, "blocked");
		binder.bind(accessRoles, "accessRoles");
		binder.bind(personnelClasses, "personnelClasses");

		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);

		accessRoles.refresh();
        addComponents(id, name, email, newPassword, blocked, accessRoles, personnelClasses);
	}

	public void refresh(final User user) {
		if (user.isNew()) {
			newPassword.setRequiredIndicatorVisible(true);
			binder.forField(newPassword).asRequired(Texts.get("user.field.required")).withNullRepresentation("").bind((ValueProvider<User, String>) user12 -> "", (Setter<User, String>) (user1, s) -> user1.setPassword(MD5Hash.convert(newPassword.getValue())));
			newPassword.setCaption(Texts.get("user.new.textField.password"));
		}
		binder.setBean(user);
		binder.setReadOnly(isReadOnly());
	}

	/**
	 * @return true if user data was valid and saved, false if not
	 */
	public boolean save() {
		binder.validate();
		try {
			if (binder.isValid()) {
				final User user = binder.getBean();
				boolean userIsNew = user.isNew();

				if (newPassword.getValue() != null && !newPassword.getValue().isEmpty() && !userIsNew)
					user.setPassword(MD5Hash.convert(newPassword.getValue()));

				if (userIsNew && !accessRoles.isVisible()) {
					//self registration of user
					final AccessRole role = VaadinSpringContextHelper.getBean(SelfRegistration.class).getSelfRegistratationRole();
					if (role != null)
						user.setAccessRoles(new HashSet<>(Collections.singletonList(role)));
					else
						return false; //self-registration not allowed
				}

				binder.writeBean(user);

				refresh(user.save());
				Notification.show(Texts.get("user.saved"), Type.TRAY_NOTIFICATION);
				return true;
			}
		} catch (ValidationException e) {
			Notification.show(Texts.get("user.validation.error"), Type.WARNING_MESSAGE);
		}
		return false;
	}

    private CheckBoxGroup<PersonnelClassStub> createPersonnelClassOptions(String caption) {
    	final List<PersonnelClassStub> allPersonnelClasses = new PersonnelClassFinder().setOrderByName(true).list();
		final CheckBoxGroup<PersonnelClassStub> optionGroup = new CheckBoxGroup<>(Texts.get("user.groups"), allPersonnelClasses);
		optionGroup.setItemCaptionGenerator((ItemCaptionGenerator<PersonnelClassStub>) this::formatClassName);
		optionGroup.setItemDescriptionGenerator((DescriptionGenerator<PersonnelClassStub>) PersonnelClassStub::getDescription);
		optionGroup.setCaption(caption);
		if (allPersonnelClasses.size() == 0)
			optionGroup.setVisible(false);
		return optionGroup;
	}

	private String formatClassName(final PersonnelClassStub personnelClass) {
		final String className = personnelClass.getName();
		if (className == null)
			return personnelClass.getId();
		return className;
	}

    private TextField createTextField(String captionKey, int maxLength) {
        TextField field = new TextField(Texts.get(captionKey));
        field.setSizeFull();
		field.setMaxLength(maxLength);
        return field;
    }

    public void setGroupMembershipMode(GroupMembershipMode mode) {
    	switch(mode) {
    	case HIDDEN:
    		personnelClasses.setVisible(false);
    		personnelClasses.setReadOnly(true);
    		break;
    	case READONLY:
    		personnelClasses.setVisible(true);
    		personnelClasses.setReadOnly(true);
    		break;
    	case EDITABLE:
    		personnelClasses.setVisible(true);
    		personnelClasses.setReadOnly(false);
    		break;
    	}
    }

    /**
     * Use this mode to show form so user can edit her own password etc. (self service)
     */
    public void setPersonalMode() {
    	accessRoles.setVisible(false);
    	blocked.setVisible(false);
    }

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public boolean commit() {
		return save();
	}

	public String getUserId() {
    	if (id.getValue() == null)
    		return null;
		return id.getValue();
	}

	@Override
	protected void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
	}

	@Override
	protected boolean isReadOnly() {
		return isReadOnly;
	}
}
