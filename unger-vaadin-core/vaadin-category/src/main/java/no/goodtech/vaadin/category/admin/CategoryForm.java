package no.goodtech.vaadin.category.admin;

import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.server.Page;
import com.vaadin.server.Setter;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.*;
import no.goodtech.persistence.entity.AbstractSimpleEntityImpl;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.category.Category.Fields;
import no.goodtech.vaadin.category.CategoryFinder;
import no.goodtech.vaadin.category.Texts;
import no.goodtech.vaadin.layout.FontIconComboBox;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.utils.UniqueIdValidator;

public class CategoryForm extends VerticalLayout implements SimpleInputBox.IinputBoxContent {

	private final TextField pk = createTextField(Fields.PK);
	private final TextField id = createTextField(Fields.ID);
	private final TextField name = createTextField(Fields.NAME);
	private final TextArea description = createTextArea(Fields.DESCRIPTION);
	private final FontIconComboBox iconName = new FontIconComboBox();
	private ColorPicker colorPicker = new ColorPicker();

	private final Binder<Category> binder;

	CategoryForm(Category category) {
		binder = new BeanValidationBinder<>(Category.class);

		binder.forField(pk).withConverter(new StringToLongConverter("Må være heltall")).withNullRepresentation(-1L).bind((ValueProvider<Category, Long>) AbstractSimpleEntityImpl::getPk, (Setter<Category, Long>) (category1, aLong) -> { }).setReadOnly(true);
		binder.forField(id).asRequired(Texts.get("category.idMustBeSet")).withNullRepresentation("").withValidator(new UniqueIdValidator<>(new CategoryFinder().setOwner((category != null) ? category.getOwner() : null), binder)).bind("id");
		binder.forField(name).asRequired(Texts.get("category.nameMustBeSet")).withNullRepresentation("").bind("name");
		binder.forField(description).withNullRepresentation("").bind("description");
		binder.bind(iconName, "iconName");
		binder.forField(colorPicker).bind((ValueProvider<Category, Color>) category13 -> (category13.getColor() != null) ? new Color(category13.getColor()) : Color.BLACK, (Setter<Category, Color>) (category12, color) -> category12.setColor((color != null) ? color.getRGB() : null));

		if (category.isNew()) {
			setCaption(Texts.get("category.editingForm.caption.new"));
		}
		else {
			setCaption(Texts.get("category.editingForm.caption.prefix") + " " + category.getName());
		}

		binder.setBean(category);

		HorizontalLayout icon = new HorizontalLayout(iconName, colorPicker);
		icon.setComponentAlignment(iconName, Alignment.BOTTOM_LEFT);
		icon.setComponentAlignment(colorPicker, Alignment.BOTTOM_LEFT);
		icon.setMargin(false);
		setSizeFull();
		setMargin(false);
		addComponents(pk, id, name, description, icon);

		description.setSizeFull();
		setExpandRatio(description, 1);

		colorPicker.setCaption(Texts.get("category.color"));
		colorPicker.setModal(true);
		colorPicker.setPosition(
				Page.getCurrent().getBrowserWindowWidth() / 2 - 246 / 2,
				Page.getCurrent().getBrowserWindowHeight() / 2 - 507 / 2);
	}

	public Component getComponent() {
		return this;
	}

	public boolean commit() {
		binder.validate();
		try {
			if (binder.isValid()) {
				binder.writeBean(binder.getBean());
				binder.getBean().save();
				return true;
			}
		}catch (ValidationException e) {
			Notification.show(Texts.get("category.editingForm.validation.failed"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}

	private TextField createTextField(String captionKey) {
		TextField field = new TextField(Texts.get("category." + captionKey));
		field.setWidth("100%");
		return field;
	}

	private TextArea createTextArea(String captionKey) {
		TextArea area = new TextArea(Texts.get("category." + captionKey));
		area.setWidth("100%");
		return area;
	}
}
