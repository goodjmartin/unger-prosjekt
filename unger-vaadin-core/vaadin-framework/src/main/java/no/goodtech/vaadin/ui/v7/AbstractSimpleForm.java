package no.goodtech.vaadin.ui.v7;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Buffered.SourceException;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import no.cronus.common.utils.ReflectionUtils;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.vaadin.main.SimpleInputBox.IinputBoxContent;
import no.goodtech.vaadin.ui.Texts;

import java.util.Arrays;
import java.util.ListIterator;

/**
 * A generic form to show and edit entities
 * @param <ENTITY> the entity type you like to show / edit
 * You have to call binder.bindMemberFields(this) if you have your own fields that you like to bind
 * Please call {@link #validate()} to validate (and eventually save if validation went ok)
 */
@Deprecated
public abstract class AbstractSimpleForm<ENTITY extends AbstractEntityImpl<?>> extends VerticalLayout implements IinputBoxContent {

	protected final Class<ENTITY> entityType;
	protected final BeanFieldGroup<ENTITY> binder;

	protected final TextField id = createTextField(Texts.get("simpleForm.field.id.caption"));
	protected final TextField name = createTextField(Texts.get("simpleForm.field.name.caption"));
	protected final TextArea description = createTextArea(Texts.get("simpleForm.field.description.caption"));
	protected final CheckBox disabled = new CheckBox(Texts.get("simpleForm.field.disabled.caption"));

	@SuppressWarnings("unchecked")
	public AbstractSimpleForm(ENTITY entity) {
		
		entityType = (Class<ENTITY>) ReflectionUtils.getParameterizedType(this, 0);

		binder = new BeanFieldGroup<>(entityType);
		binder.bindMemberFields(this);
		binder.setItemDataSource(entity);	
		
		setSizeFull();
		setMargin(false);
		
		addComponents(id, name, description, disabled);
		setExpandRatio(description, 1);
	}

	protected TextField createTextField(String caption) {
		TextField field = new TextField(caption);
		field.setWidth("100%");
		field.setNullRepresentation("");
		field.setImmediate(true);
		return field;
	}
	
	protected TextArea createTextArea(String caption) {
		TextArea field = new TextArea(caption);
		field.setSizeFull();
		field.setNullRepresentation("");
		field.setImmediate(true);
		return field;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Save the entity if validation went ok
	 * @return true if validation and save went ok, false if validation failed
	 */
	public boolean commit() {
		try {
			binder.commit();
			binder.getItemDataSource().getBean().save();
			return true;
		} catch (CommitException|SourceException|InvalidValueException e) {
			Notification.show(Texts.get("simpleForm.validation.failed"), Type.WARNING_MESSAGE);
		}
		return false;
	}
	
	public void setReadOnly(boolean readOnly) {
		binder.setReadOnly(readOnly);		
	}

	/**
	 * Add components before a specific component
	 * @param components the components to add
	 */
	public void addComponentsBefore(Component component, Component... components) {
		final ListIterator<Component> iterator = Arrays.asList(components).listIterator();
		while (iterator.hasNext()) {
			final int componentIndex = getComponentIndex(component);
			if (componentIndex < 0) //we didn't find the component
				addComponent(iterator.next());
			else
				addComponent(iterator.next(), componentIndex);
		}
	}

	/**
	 * Add components before disabled-checkbox
	 * @param components the components to add
	 */
	@Override
	public void addComponents(Component... components) {
		addComponentsBefore(disabled, components);
	}
}
