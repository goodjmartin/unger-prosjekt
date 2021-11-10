package no.goodtech.vaadin.category.admin;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.category.Category;
import no.goodtech.vaadin.category.CategoryFinder;
import no.goodtech.vaadin.category.Texts;
import no.goodtech.vaadin.main.SimpleInputBox;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.ui.SimpleCrudAdminPanel;

import javax.persistence.PersistenceException;

/**
 * Panel som viser en liste av for visning av egenskaper, inneholder knapper for opprettelse og sletting
 */
@UIScope
@SpringView(name = CategoryAdminPanel.VIEW_ID)
public class CategoryAdminPanel extends SimpleCrudAdminPanel<Category, Category, CategoryFinder> {

	public static final String VIEW_ID = "CategoryAdminPanel";
	public static final String VIEW_NAME = Texts.get("categoryPanel.viewName");

	private static final String ACCESS_VIEW = "categoryPanel.view";
	private static final String ACCESS_EDIT = "categoryPanel.edit";

	public CategoryAdminPanel() {
		super(new CategoryFilterPanel(), new CategoryGrid());
	}


	protected SimpleInputBox.IinputBoxContent createDetailForm(Category category) {
		return new CategoryForm(category);
	}

	protected Category createEntity() {
		return new Category(null, ((CategoryFilterPanel)filterPanel).getSelectedOwnerClass());
	}

	protected AccessFunction getAccessFunctionView() {
		return new AccessFunction(ACCESS_VIEW, Texts.get("accessFunction." + ACCESS_VIEW));
	}

	protected AccessFunction getAccessFunctionEdit() {
		return new AccessFunction(ACCESS_EDIT, Texts.get("accessFunction." + ACCESS_EDIT));
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	@Override
	protected void delete() {
		try {
			super.delete();
		} catch (PersistenceException e) {
			Notification.show(Texts.get("category.cannot.delete"), Notification.Type.ERROR_MESSAGE);
		}
	}

	/**
	 * Use url to preselect value in combobox so that table is filtered.
	 * It is also possible so set the combobox to readonly by using disabled=true in url param
	 */
	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		UrlUtils url = new UrlUtils();
		String ownerClass = url.getParameter("ownerClass");
		boolean disabled = Boolean.parseBoolean(url.getParameter("disabled"));
		CategoryFilterPanel fPanel = (CategoryFilterPanel) filterPanel;
		fPanel.setSelectedOwnerClass(ownerClass);
		fPanel.setOwnerClassComboBoxEnabled(disabled);
		fPanel.selectFirstItem();
	}

	@Override
	protected Category copy(Category entityToCopy) {
		return entityToCopy.load().copy();
	}
}
