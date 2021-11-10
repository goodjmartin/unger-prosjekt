package no.goodtech.vaadin.ui;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.vaadin.utils.EntityTabPanel;
import no.goodtech.vaadin.utils.IEntityTabPanel;
import no.goodtech.vaadin.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic TabSheet that includes all components marked with {@link EntityTabPanel}.
 * These panels are typically Attachment, Category, Comment, etc.
 * The generic panels are typically loosely connected to other main entities like MaterialDefinition, Equipment, ...
 * You can also add custom tabs, see {@link #setCustomTabs(Component...)}
 * You can control the order and visibility of the tabs, see {@link #setVisibleTabs(String...)}
 * TODO styling to make the collapse/expand button aligned to the right
 */
public class EntityTabs extends TabSheet {

	private final Logger LOGGER = LoggerFactory.getLogger(EntityTabs.class);
	//private final List<Component> defaultTabs = Utils.getComponentByClassAnnotation(EntityTabPanel.class);
	private final List<Component> defaultTabs = null;
	private List<Component> customTabs = new ArrayList<>();
	private final Set<String> visibleTabIds = new LinkedHashSet<>();
	private Registration tabSelectionListener;

	public EntityTabs() {
		setSizeFull();
	}

	/**
	 * Provide which tabs to show and in what order.
	 * You can only show tabs that is annotated with @EntityTab or any of the custom tabs on this tab sheet
	 * @param tabIds ID of each tab to show, in appearing order
	 */
	public void setVisibleTabs(String... tabIds) {
		if (tabIds != null && tabIds.length > 0) {
			visibleTabIds.clear();
			visibleTabIds.addAll(Arrays.asList(tabIds));
		}
	}

	public List<Component> getTabs(){
		List<Component> tabs = new ArrayList<>(defaultTabs);
		tabs.addAll(customTabs);
		return tabs;
	}

	/**
	 * @return the ID of the given tab
	 */
	private String getId(Component tab) {
		String id = null;
		if (tab instanceof IEntityTabPanel) {
			IEntityTabPanel entityTabPanel = (IEntityTabPanel) tab;
			id = entityTabPanel.getId();
		}
		if (id == null) {
			final Class<? extends Component> clazz = tab.getClass();
			final EntityTabPanel annotation = clazz.getAnnotation(EntityTabPanel.class);
			if (annotation != null) {
				id = annotation.id();
				LOGGER.debug("Found tab ID from annotation");
			} else {
				id = clazz.getSimpleName();
				LOGGER.debug("Found tab ID from class");
			}
		} else {
			LOGGER.debug("Found tab ID from IEntityTabPanel.getId()");
		}
		LOGGER.debug("Tab ID of {} with caption {} is {}", tab.getClass(), tab.getCaption(), id);
		return id;
	}

	/**
	 * Refreshes the different tabs that are shown in the tab panel.
	 * NB: This does not refresh all panels within each tabs,
	 *  but creates a SelectedTabChangeListener to refresh the panel at selection time
	 *
	 * @param entity         the entity (e.g. MaterialDefinition, Equipment, PurchaseOrder, ...)
	 */
	public void refresh(EntityStub entity) {
		long start = System.currentTimeMillis();
		Component selectedTab= getSelectedTab(); // save this
		if (tabSelectionListener != null) {
			tabSelectionListener.remove();
		}
		removeAllComponents();

		Map<String, Component> tabs = new LinkedHashMap<>();
		for (Component tab : defaultTabs) {
			tabs.put(getId(tab), tab);
		}
		for (Component tab : customTabs) {
			tabs.put(getId(tab), tab);
		}
		if (visibleTabIds.size() > 0) {
			//we have a custom tab config, so add only specified tabs and in custom order
			for (String tabId : visibleTabIds) {
				final Component tab = tabs.get(tabId);
				if (tab != null) {
					addTab(tab, entity);
				} else {
					LOGGER.warn("Couldn't find tab with id = '{}'. Available tabs: {}", tabId, tabs.keySet());
				}
			}
		} else {
			for (Component tab : tabs.values()) {
				addTab(tab, entity);
			}
		}

		if (getTab(0) != null) {
			refreshTab(getTab(0).getComponent(), entity); // Refresh the first tab
		}

		// Refresh the selected tab if it is selected
		tabSelectionListener = addSelectedTabChangeListener(event -> refreshTab(event.getTabSheet().getSelectedTab(), entity));
		setSelectedTab(selectedTab); // and restore selected
		LOGGER.debug("refresh() took {} ms", System.currentTimeMillis() - start);
	}

	private void addTab(Component tab, EntityStub entity) {
		String caption = tab.getCaption();
		if (caption == null) {
			caption = tab.getClass().getSimpleName();
		}
		addTab(tab, caption);

		if (entity.isNew() && tab instanceof IEntityTabPanel)
			((IEntityTabPanel) tab).setReadOnly(true);

		if (tab instanceof IEntityTabPanel) {
			IEntityTabPanel entityTabPanel = (IEntityTabPanel) tab;
			entityTabPanel.addListener(updatedCaption -> {
				if (this.getTab(tab) != null)
					this.getTab(tab).setCaption(updatedCaption);
			});
			entityTabPanel.initCaption(entity);
		}
	}

	private void refreshTab(Component selectedTab, EntityStub entity){
		long ms= System.currentTimeMillis();
		if (entity != null && selectedTab instanceof IEntityTabPanel){
			((IEntityTabPanel) selectedTab).refresh(entity);
		}
		LOGGER.debug("refreshTab Took: " + (System.currentTimeMillis() - ms));
	}

	/**
	 * Set a list of custom tabs to be shown in the tab panel
	 * @see #setCustomTabs(Component...)
	 */
	public void setCustomTabs(List<Component> customTabs) {
		this.customTabs = customTabs;
	}

	/**
	 * Set a list of custom tabs to be shown in the tab panel
	 * If the tabs implement {@link IEntityTabPanel},
	 * you will can dynamically refresh the caption and do even more fancy stuff
	 */
	public void setCustomTabs(Component... customTabs) {
		setCustomTabs(Arrays.asList(customTabs));
	}

	public void setReadOnly(boolean readOnly) {
		for (Component component : this) {
			if (component instanceof IEntityTabPanel) {
				((IEntityTabPanel) component).setReadOnly(readOnly);
			}
		}
	}

}
