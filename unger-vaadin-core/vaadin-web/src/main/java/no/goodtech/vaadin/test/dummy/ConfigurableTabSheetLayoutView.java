package no.goodtech.vaadin.test.dummy;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.tabs.ConfigurableTabSheetLayout;
import no.goodtech.vaadin.tabs.IMenuView;

@UIScope
@SpringView(name = ConfigurableTabSheetLayoutView.VIEW_ID)
public class ConfigurableTabSheetLayoutView extends VerticalLayout implements IMenuView, IConfigurableTabSheetLayoutView {

	public static final String VIEW_ID = "ConfigurableTabSheetLayoutView";

	private final Table table;
	private final ConfigurableTabSheetLayout configurableTabSheetLayout;
	private volatile String selectedTabGroupId;

	public ConfigurableTabSheetLayoutView() {
		table = new Table();
		table.setWidth(100, Unit.PERCENTAGE);
		BeanItemContainer<Person> personBeanItemContainer = new BeanItemContainer<>(Person.class);
		personBeanItemContainer.addBean(new Person("Rune", "Sandersen"));
		personBeanItemContainer.addBean(new Person("Per-Ivar", "Bakke"));
		table.setContainerDataSource(personBeanItemContainer);
		table.setSelectable(true);

		table.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				//noinspection unchecked
				configurableTabSheetLayout.refresh(table.getValue(), selectedTabGroupId);
			}
		});

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSpacing(false);
		verticalLayout.setMargin(false);
		verticalLayout.setSizeFull();
		configurableTabSheetLayout = new ConfigurableTabSheetLayout<IConfigurableTabSheetLayoutView, Person>(verticalLayout, this, "classpath://tabSheetLayout.xml");

		addComponents(table, verticalLayout);
	}

	@Override
	public void enter(final ViewChangeListener.ViewChangeEvent event) {
		UrlUtils urlUtils = new UrlUtils();
		selectedTabGroupId = urlUtils.getParameter("tabGroupId", event.getParameters());

		if (selectedTabGroupId != null) {
			//noinspection unchecked
			configurableTabSheetLayout.refresh(table.getValue(), selectedTabGroupId);
		}
	}

	@Override
	public boolean isAuthorized(final User user, final String argument) {
		return true;
	}

	@Override
	public String getViewName() {
		return "ConfigurableTabSheetLayoutView";
	}

}
