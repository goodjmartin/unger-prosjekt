package no.goodtech.vaadin.ui;

import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import no.cronus.common.utils.ReflectionUtils;
import no.goodtech.persistence.entity.AbstractEntityImpl;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.formatting.StringToDoubleConverter;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.utils.UniqueIdValidator;
import no.goodtech.vaadin.utils.Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple form layout to create bindings in Vaadin 8. Works as both side-edit-panel and pop-up edit window.
 */
public class SimpleForm<ENTITY extends Entity> extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	protected final Binder<ENTITY> binder;
	protected ENTITY entity;
	protected final Class<ENTITY> entityClass;
	private final EntityTabs entityTabs = new EntityTabs();
	private final CheckBox disabledCheckBox = new CheckBox(ApplicationResourceBundle.getInstance("vaadin-core").getString("simpleForm.field.disabled.caption"));
	private boolean showEntityTabs = false;
	private boolean isReadOnly = false;

	private StringToDoubleConverter stringToDoubleConverter;
	private StringToLongConverter stringToLongConverter;
	private StringToIntegerConverter stringToIntegerConverter;

	public SimpleForm(EntityStub<? extends ENTITY> entity) {
		if (entity != null) {
			if (entity.isNew())
				//noinspection unchecked
				this.entity = (ENTITY) entity;
			else
				this.entity = entity.load();
			//noinspection unchecked
			entityClass = (Class<ENTITY>) this.entity.getClass();
			this.binder = new BeanValidationBinder<>(entityClass);
			if (entity.isNew()) {
				setCaption(ApplicationResourceBundle.getInstance("vaadin-core").getString("simpleForm.binder.new"));
			} else {
				setCaption(ApplicationResourceBundle.getInstance("vaadin-core").getString("simpleForm.binder.edit"));
			}
			refresh(this.entity);
		} else if (ReflectionUtils.hasParameterizedType(this, 0)) {
			//noinspection unchecked
			entityClass = (Class<ENTITY>) ReflectionUtils.getParameterizedType(this, 0);
			this.binder = new BeanValidationBinder<>(entityClass);
		} else {
			entityClass = null;
			this.binder = new Binder<>();
		}

		init();
	}

	public SimpleForm() {
		if (ReflectionUtils.hasParameterizedType(this, 0)) {
			//noinspection unchecked
			entityClass = (Class<ENTITY>) ReflectionUtils.getParameterizedType(this, 0);
			this.binder = new BeanValidationBinder<>(entityClass);
		} else {
			this.entityClass = null;
			this.binder = new Binder<>();
		}

		init();
	}

	public SimpleForm(Class<ENTITY> clazz) {
		this.entityClass = clazz;
		this.binder = new BeanValidationBinder<>(entityClass);

		init();
	}

	private void init() {
		stringToDoubleConverter = new StringToDoubleConverter(Texts.get("simpleForm.validation.double"));
		stringToIntegerConverter = new StringToIntegerConverter(Texts.get("simpleForm.validation.integer"));
		stringToLongConverter = new StringToLongConverter(Texts.get("simpleForm.validation.long"));

		// Add binding to disabled checkbox
		if (entityClass.isAssignableFrom(AbstractSimpleEntityImpl.class) || entity instanceof AbstractSimpleEntityImpl)
			binder.forField(disabledCheckBox).bind(item -> ((AbstractSimpleEntityImpl) item).isDisabled(),
					(item, dis) -> ((AbstractSimpleEntityImpl) item).setDisabled(dis));

		setSizeFull();
		setMargin(false);
	}

	public void refresh(ENTITY entity) {
		if (showEntityTabs) {
			entityTabs.setCustomTabs(getCustomEntityPanels());
			entityTabs.refresh((EntityStub) entity);
			hideEntityTabsIfEntityIsNew();
		}
		this.entity = entity;
		binder.setBean(entity);
	}

	/**
	 * For custom entity tabs, override this and return a list of the custom tabs you want.
	 */
	public List<Component> getCustomEntityPanels() {
		return new ArrayList<>();
	}

	/**
	 * Displays the tab panel at the bottom of the form
	 */
	public void showEntityTabs() {
		showEntityTabs(getComponentCount());
	}

	/**
	 * Show specific generic tabs
	 * @param tabIds the id of the tab from @
	 */
	public void setVisibleTabs(String... tabIds) {
		entityTabs.setVisibleTabs(tabIds);
		showEntityTabs(getComponentCount());
	}

	public void showEntityTabs(int index) {
		hideEntityTabs();
		showEntityTabs = true;
		addComponent(entityTabs, index);
		setExpandRatio(entityTabs, 1F);
		entityTabs.setCustomTabs(getCustomEntityPanels());
		entityTabs.refresh((EntityStub) entity);
		hideEntityTabsIfEntityIsNew();
	}

	private void hideEntityTabsIfEntityIsNew(){
		if (entity != null && entity.isNew()){
//			entityTabs.collapseTabs(true);
			entityTabs.setReadOnly(true);
			entityTabs.setEnabled(false);
			entityTabs.setVisible(false);
		}
	}

	public void hideEntityTabs() {
		showEntityTabs = false;
		if (components.contains(entityTabs)) {
			removeComponent(entityTabs);
		}
	}

	public void showDisabledCheckBox() {
		showDisabledCheckBox(getComponentCount());
	}

	public void showDisabledCheckBox(int index) {
		hideDisabledCheckBox();
		addComponent(disabledCheckBox, index);
	}

	public void hideDisabledCheckBox() {
		if (components.contains(disabledCheckBox)) {
			removeComponent(disabledCheckBox);
		}
	}

	public void setBean(ENTITY entity) {
		binder.setBean(entity);
	}

//	protected void setEntityTabsHeight(String height){
//		entityTabs.setDefaultHeightSetting(height);
//	}

	protected EntityTabs getEntityTabs() {
		return entityTabs;
	}

	protected CheckBox getDisabledCheckBox() {
		return disabledCheckBox;
	}

	/**
	 * Create a unique id binding builder. Typically used for id fields.
	 *
	 * @param field  a field with string values (the id field)
	 * @param finder the finder to validate the uniqueness against the DB
	 */
	public <ENTITYSTUB extends EntityStub<ENTITY>, FINDER extends AbstractFinder> Binder.BindingBuilder<ENTITY, String> createUniqueIdBindingBuilder(
			HasValue<String> field, final AbstractFinder<ENTITY, ENTITYSTUB, FINDER> finder) {
		return binder.forField(field).withValidator(new UniqueIdValidator<>(finder, binder));
	}

	/**
	 * A default date binding builder
	 */
	public Binder.BindingBuilder<ENTITY, Date> createDateBinding(HasValue<LocalDateTime> field) {
		return binder.forField(field).withNullRepresentation(null)
				.withConverter(new LocalDateTimeToDateConverter(ZoneId.systemDefault()));
	}

	/**
	 * A default double binding builder
	 */
	public Binder.BindingBuilder<ENTITY, Double> createDoubleBinding(HasValue<String> field) {
		return binder.forField(field).withNullRepresentation("").withConverter(stringToDoubleConverter);
	}

	/**
	 * A default integer binding builder
	 */
	public Binder.BindingBuilder<ENTITY, Integer> createIntegerBinding(HasValue<String> field) {
		return binder.forField(field).withNullRepresentation("").withConverter(stringToIntegerConverter);
	}

	/**
	 * A default long binding builder
	 */
	public Binder.BindingBuilder<ENTITY, Long> createLongBinding(HasValue<String> field) {
		return binder.forField(field).withNullRepresentation("").withConverter(stringToLongConverter);
	}

	public StringToDoubleConverter getStringToDoubleConverter() {
		return stringToDoubleConverter;
	}

	/**
	 * Set a special double converter
	 */
	public void setStringToDoubleConverter(StringToDoubleConverter stringToDoubleConverter) {
		this.stringToDoubleConverter = stringToDoubleConverter;
	}

	public StringToLongConverter getStringToLongConverter() {
		return stringToLongConverter;
	}

	/**
	 * Set a special long converter
	 */
	public void setStringToLongConverter(StringToLongConverter stringToLongConverter) {
		this.stringToLongConverter = stringToLongConverter;
	}

	public StringToIntegerConverter getStringToIntegerConverter() {
		return stringToIntegerConverter;
	}

	/**
	 * Set a special integer converter
	 */
	public void setStringToIntegerConverter(StringToIntegerConverter stringToIntegerConverter) {
		this.stringToIntegerConverter = stringToIntegerConverter;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	/**
	 * Commit the changes in the form
	 */
	@Override
	public boolean commit() {
		try {
			if (binder.isValid() && !isReadOnly) {
				binder.writeBean(binder.getBean());
				if (binder.getBean() instanceof AbstractEntityImpl)
					((AbstractEntityImpl) binder.getBean()).save();
				else{
					//return Utils.callMethodByClassIfExists(binder.getBean(), "save");
				}
				return true;
			}
		} catch (ValidationException e) {
			Notification.show(ApplicationResourceBundle.getInstance("vaadin-core")
					.getString("simpleForm.binder.failed"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}

	public Binder<ENTITY> getBinder() {
		return binder;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
//		super.setReadOnly(readOnly); // does not work for Vertical/Horizontal layout
		entityTabs.setReadOnly(readOnly);
		binder.setReadOnly(readOnly);
		this.isReadOnly = readOnly;
	}
}

