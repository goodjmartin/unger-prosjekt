package no.goodtech.vaadin.security.tabs.accessrole;

import com.vaadin.server.SizeWithUnit;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.security.model.AccessRole;
import no.goodtech.vaadin.security.model.AccessRoleFinder;
import no.goodtech.vaadin.ui.SimpleForm;
import no.goodtech.vaadin.utils.UniqueIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessRoleForm extends SimpleForm<AccessRole> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessRoleForm.class);

	private TextField id = new TextField(getText("accessRoleTable.header.id"));
	private TextField description = new TextField(getText("accessRoleTable.header.description"));

	private final TwinColSelect<AccessFunction> accessFunctions;

	public AccessRoleForm(EntityStub<AccessRole> accessRoleStub) {
		super(accessRoleStub);
		setCaption(getCaption() + " " + getText("accessRoleDetails.title"));

		// Add the access function component
		accessFunctions = new TwinColSelect(getText("accessRoleDetails.optionGroup.accessFunctions"));
		accessFunctions.setLeftColumnCaption(getText("accessRoleDetails.optionGroup.accessFunctions.notSelected"));
		accessFunctions.setRightColumnCaption(getText("accessRoleDetails.optionGroup.accessFunctions.selected"));
		accessFunctions.setItemCaptionGenerator(AccessFunction::getDescription);
		List<AccessFunction> aFs = AccessFunctionManager.getAccessFunctions();
		for (AccessFunction f : aFs) {
			if (f.getId() == null || f.getId().isEmpty() || f.getDescription() == null || f.getDescription().isEmpty()) {
				Notification.show("Mangler id/beskrivelse på tilgang! " + f.getId() + " - " + f.getDescription(), Notification.Type.ERROR_MESSAGE);
				LOGGER.error("Mangler id/beskrivelse på tilgang! " + f.getId() + " - " + f.getDescription());
			}
		}
		accessFunctions.setItems(aFs.stream().filter(Objects::nonNull).sorted(Comparator.comparing(af -> af.getDescription() != null ? af.getDescription() : "")));
		//accessFunctions.setSizeFull();
		accessFunctions.setWidth(100, Sizeable.Unit.PERCENTAGE);
		accessFunctions.setHeight(100, Sizeable.Unit.PERCENTAGE);

		binder.forField(id)
				.withValidator(new UniqueIdValidator<>(new AccessRoleFinder(), binder))
				.withValidator(e -> !e.trim().isEmpty(), getText("cannotBeEmpty"))
				.bind("id");
		binder.bind(description, "description");

		id.setWidth(200, Sizeable.Unit.PIXELS);
		description.setWidth(100, Sizeable.Unit.PERCENTAGE);
		HorizontalLayout upper = new HorizontalLayout(id, description);
		upper.setWidth(100, Sizeable.Unit.PERCENTAGE);
		upper.setExpandRatio(description, 1f);
		addComponent(upper);
		addComponentsAndExpand(accessFunctions);

		AccessRole accessRole = entity;
		// Already assigned access functions should be marked as such
		for (AccessFunction accessFunction : AccessFunctionManager.getAccessFunctions()) {
			if (accessRole.getAccessFunctionIds().contains(accessFunction.getId())) {
				accessFunctions.select(accessFunction);
			} else {
				accessFunctions.deselect(accessFunction);
			}
		}
	}

	private String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-security").getString(key);
	}


	@Override
	public void refresh(AccessRole accessRole) {
		super.refresh(accessRole);
	}

	@Override
	public boolean commit() {
		// Find the selected access functions
		Set<String> selectedAccessFunctions = AccessFunctionManager.getAccessFunctions().stream().filter(accessFunction -> accessFunctions.isSelected(accessFunction)).map(AccessFunction::getId).collect(Collectors.toSet());
		binder.getBean().setAccessFunctionIds(selectedAccessFunctions);
		return super.commit();
	}

	@Override
	public SizeWithUnit getComponentHeight() {
		return new SizeWithUnit(650, Sizeable.Unit.PIXELS);
	}


	@Override
	public SizeWithUnit getComponentWidth() {
		return new SizeWithUnit(800, Sizeable.Unit.PIXELS);
	}
}
