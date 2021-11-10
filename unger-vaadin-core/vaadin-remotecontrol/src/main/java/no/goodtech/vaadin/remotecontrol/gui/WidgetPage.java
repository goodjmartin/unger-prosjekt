package no.goodtech.vaadin.remotecontrol.gui;


import com.vaadin.ui.FormLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.remotecontrol.metamodel.Widget;
import org.vaadin.touchkit.ui.NavigationView;


public class WidgetPage extends NavigationView {
	public WidgetPage(Widget widget, ScreenPage screenPage) {
		setSizeFull();
		setCaption(widget.getCaption());
		setPreviousComponent(screenPage);
		BeanItem<Widget> beanItem = new BeanItem<Widget>(widget);
		FieldGroup fieldGroup = new FieldGroup();
		fieldGroup.setItemDataSource(beanItem);
		FormLayout formLayout = new FormLayout();
		formLayout.addComponent(fieldGroup.buildAndBind("caption"));
		formLayout.addComponent(fieldGroup.buildAndBind("Editerbar", "readOnly"));
		formLayout.addComponent(fieldGroup.buildAndBind("tag"));
		formLayout.addComponent(fieldGroup.buildAndBind("dataType"));
		for (Field<?> s : fieldGroup.getFields()) {
			s.setSizeFull();
			if (s instanceof TextField) {
				((TextField) s).setNullRepresentation("");
			}
		}

		setContent(formLayout);
	}
}
