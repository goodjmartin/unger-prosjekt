package no.goodtech.vaadin.security.tabs.user;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TextField;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.security.model.UserFinder;
import no.goodtech.vaadin.security.ui.Texts;

public class UserFilterPanel extends FilterPanel<UserFinder>{
	
	private final TextField freeTextField = createTextField("user.filter.freeText.caption");
	private final AccessRolesOptionGroup accessRolesOptionGroup = new AccessRolesOptionGroup();

	public UserFilterPanel() {
		super();
		accessRolesOptionGroup.setStyleName("horizontal");
		addComponents(freeTextField);
		VerticalLayout verticalLayout = new VerticalLayout(accessRolesOptionGroup, getContent());
		verticalLayout.setMargin(false);
		setContent(verticalLayout);
	}
	
	@Override
	public void refresh(String url) {
		super.refresh(url);
		accessRolesOptionGroup.refresh();
	}

	@Override
	public UserFinder getFinder() {
		UserFinder finder = new UserFinder().orderById(true);
		if (accessRolesOptionGroup.getValue().size() > 0)
			finder.setAccessRoles(accessRolesOptionGroup.getValue());
		if (freeTextField.getValue() != null)
			finder.setIdOrNameOrEmail(freeTextField.getValue());
		return finder;
	}
	
    private TextField createTextField(String captionKey) {
        TextField field = new TextField(Texts.get(captionKey));
        field.setSizeFull();

		return field;
	}
}
