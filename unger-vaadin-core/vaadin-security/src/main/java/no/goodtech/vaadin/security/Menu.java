package no.goodtech.vaadin.security;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.ui.Tree;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.layoutDefinition.GroupType;
import no.goodtech.vaadin.layoutDefinition.LayoutDefinition;
import no.goodtech.vaadin.layoutDefinition.MenuItemType;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.Constants;
import no.goodtech.vaadin.tabs.IMenuView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"serial", "unchecked"})
public final class Menu extends CustomComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(Menu.class);

	private static final Object PROPERTY_NAME = "NAME";
	private static final Object PROPERTY_ICON = "ICON";
	public static final String ID = "dashboard-menu";
	public static final String TREE_ID = "menuTree";
	private final Navigator navigator;
	private final SpringViewProvider viewProvider;
	private final Tree menuTree;

	Menu(final LayoutDefinition layoutDefinition, final Navigator navigator, final SpringViewProvider viewProvider) {
		setPrimaryStyleName("valo-menu");
		setId(ID);
		setSizeUndefined();

		menuTree = new Tree();

		this.navigator = navigator;
		this.viewProvider = viewProvider;

		setCompositionRoot(buildContent(layoutDefinition));
	}

	private Component buildContent(final LayoutDefinition layoutDefinition) {
		final CssLayout menuContent = new CssLayout();
		menuContent.addStyleName("sidebar");
		menuContent.addStyleName(ValoTheme.MENU_PART);
		menuContent.addStyleName("no-vertical-drag-hints");
		menuContent.addStyleName("no-horizontal-drag-hints");
		menuContent.setWidth(null);
		menuContent.setHeight("100%");

		menuContent.addComponents(buildCustomerLogo(), buildMenuItems(layoutDefinition));

		return menuContent;
	}

	private Component buildCustomerLogo() {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSpacing(false);
		verticalLayout.setMargin(false);
		verticalLayout.setWidth(100, Unit.PERCENTAGE);

		final String customerLogoPath = no.goodtech.vaadin.global.Globals.getCustomerLogo();
		if (customerLogoPath != null) {
			Image image = new Image(null, new ClassResource(customerLogoPath));
			verticalLayout.addComponent(image);
			verticalLayout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
			return verticalLayout;
		}

		return verticalLayout;
	}

	private Component buildMenuItems(final LayoutDefinition layoutDefinition) {
		CssLayout menuItemsLayout = new CssLayout();
		menuItemsLayout.addStyleName("valo-menuitems");

		// Set up the menu tree
		menuTree.setId(TREE_ID);
		menuTree.setImmediate(true);
		menuTree.setNullSelectionAllowed(false);
		menuTree.addContainerProperty(PROPERTY_NAME, String.class, null);
		menuTree.addContainerProperty(PROPERTY_ICON, Resource.class, null);
		menuTree.setItemCaptionPropertyId(PROPERTY_NAME);
		menuTree.setItemIconPropertyId(PROPERTY_ICON);
		menuTree.addItemClickListener((ItemClickEvent.ItemClickListener) event -> {
			if (event.getItemId() instanceof MenuItemType) {
				handleMenuSelection((MenuItemType) event.getItemId());
			} else if (event.getItemId() instanceof GroupType) {
				GroupType groupType = (GroupType) event.getItemId();
				if (menuTree.isExpanded(groupType)) {
					menuTree.collapseItem(groupType);
				} else {
					menuTree.expandItem(groupType);
				}
			}
		});

		// Needed a value change listener instead of a click listener for programmable menu clicks
		menuTree.addValueChangeListener((Property.ValueChangeListener) event -> {
			Object selected = event.getProperty().getValue();
			if (selected instanceof MenuItemType) {
				handleMenuSelection((MenuItemType) selected);
			}
		});

		// Add menu
		menuItemsLayout.addComponent(menuTree);

		// Initiate menu tree
		initiateMenuTree(layoutDefinition, menuTree);

		return menuItemsLayout;

	}

	public void navigateInMenu(String viewId, String index, Object ... parameters) {
		if (index != null) {
			for (Object itemId : menuTree.getItemIds()) {
				// Check if menu item is the view we are looking for
				if (itemId instanceof MenuItemType && Objects.equals(((MenuItemType) itemId).getViewId(), viewId)
						&& Objects.equals(((MenuItemType) itemId).getIndex(), index)) {
					selectAndExpandItem(itemId, viewId, parameters);
					return;
				}
			}
		} else {
			MenuItemType firstView = null;
			for (Object itemId : menuTree.getItemIds()) {
				// Check if menu item is the view we are looking for
				if (itemId instanceof MenuItemType && Objects.equals(((MenuItemType) itemId).getViewId(), viewId)) {
					if (((MenuItemType) itemId).getIndex() == null) {
						// Navigates to default view
						selectAndExpandItem(itemId, viewId, parameters);
						return;
					}

					if (firstView == null) {
						firstView = (MenuItemType) itemId;
					}
				}
			}

			if (firstView != null) {
				// If no default view is found, navigate to the first view name with matching viewName
				selectAndExpandItem(firstView, viewId, parameters);
				return;
			}
		}
		LOGGER.warn("Did not find any view to navigate to called '" + viewId + "' with index '" + index + "'");
	}

	private void selectAndExpandItem(Object itemId, String viewId, Object ... parameters) {
		// Expand all parent groups
		Object parent = menuTree.getParent(itemId);
		while (parent != null) {
			menuTree.expandItem(parent);
			parent = menuTree.getParent(parent);
		}

		// Select item
		menuTree.select(itemId);

		if (parameters != null && parameters.length > 0){
			navigator.navigateTo(new UrlUtils().createUrl(viewId, parameters));
		}
	}

	private void handleMenuSelection(final MenuItemType menuItem) {
		String queryParameters = menuItem.getParameters();
		navigator.navigateTo(menuItem.getViewId() + "/menu=true" + ((queryParameters != null) ? ("/" + queryParameters) : ""));
	}

	private void initiateMenuTree(final LayoutDefinition layoutDefinition, final Tree menuTree) {
		final User user = (User) VaadinSession.getCurrent().getAttribute(Constants.USER);

		// Initiate menu (optional)
		if (layoutDefinition.getMenu() != null) {
			for (Object menuItemOrGroup : layoutDefinition.getMenu().getMenuItemOrGroup()) {
				if (menuItemOrGroup instanceof MenuItemType) {
					addMenuItem(user, menuTree, null, (MenuItemType) menuItemOrGroup);
				} else {
					addMenuGroup(user, menuTree, null, (GroupType) menuItemOrGroup);
				}
			}
			removeEmptyGroups(menuTree); //remove groups that doesn't contain any menu items (probably because user doesn't have access to them)
		}
	}

	private void removeEmptyGroups(final Tree menuTree) {
		Set<GroupType> groupsToRemove = new HashSet<>();
		for (Object item : menuTree.getItemIds()) {
			if (item instanceof GroupType) {
				Collection<?> children = menuTree.getChildren(item);
				if (children == null || children.size() == 0) {
					GroupType group = (GroupType) item;
					groupsToRemove.add(group);
				}
			}
		}
		for (GroupType group : groupsToRemove) {
			menuTree.removeItem(group);
		}
	}

	private void addMenuItem(final User user, final Tree menuTree, final Object parent, final MenuItemType menuItem) {
		// Lookup view
		View view = viewProvider.getView(menuItem.getViewId());

		// Only add menu item if user is authorized to access view (optionally with the specified parameters)
		if ((view != null) && (view instanceof IMenuView) && ((IMenuView) view).isAuthorized(user, menuItem.getParameters())) {
			Item item = menuTree.addItem(menuItem);
			item.getItemProperty(PROPERTY_NAME).setValue((menuItem.getName() != null) ? get(menuItem.getName()) : ((IMenuView) view).getViewName());
			if (menuItem.getIcon() != null) {
				try {
					item.getItemProperty(PROPERTY_ICON).setValue(FontAwesome.valueOf(menuItem.getIcon()));
				} catch (IllegalArgumentException ignored) {
				}
			}
			menuTree.setChildrenAllowed(menuItem, false);
			menuTree.setParent(menuItem, parent);
		}
	}

	private void addMenuGroup(final User user, final Tree menuTree, final Object parent, final GroupType group) {
		Item item = menuTree.addItem(group);
		item.getItemProperty(PROPERTY_NAME).setValue(get(group.getName()));
		if (group.getIcon() != null) {
			try {
				item.getItemProperty(PROPERTY_ICON).setValue(FontAwesome.valueOf(group.getIcon()));
			} catch (IllegalArgumentException ignored) {
			}
		}
		menuTree.setParent(group, parent);

		for (Object menuItemOrGroup : group.getMenuItemOrGroup()) {
			if (menuItemOrGroup instanceof MenuItemType) {
				addMenuItem(user, menuTree, group, (MenuItemType) menuItemOrGroup);
			} else {
				addMenuGroup(user, menuTree, group, (GroupType) menuItemOrGroup);
			}
		}

		// Expand group if required
		if (group.getExpand()) {
			menuTree.expandItemsRecursively(group);
		}
	}

	/**
	 * Gets text to your application menu.
	 *
	 * @param key ID for the resource
	 * @return the text, or the same key if the resource is not found
	 */
	public static String get(String key) {
		if (Globals.isRequireMenuTranslation()) {
			ApplicationResourceBundle bundle = ApplicationResourceBundle.getInstance("layout-definition-texts");
			if (bundle == null)
				return key;
			String translation = bundle.getString(key);
			if (translation == null || "PropertyNotMapped".equals(translation))
				return key;
			return translation;
		} else {
			return key;
		}
	}

}
