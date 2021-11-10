package no.goodtech.vaadin.tabs;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.TabSheet;
import no.cronus.common.file.FileReader;
import no.cronus.common.file.IFileReader;
import no.goodtech.vaadin.layout.TabSheetLayoutMarshaller;
import no.goodtech.vaadin.tabSheetLayout.PanelGroupType;
import no.goodtech.vaadin.tabSheetLayout.TabSheetLayout;
import no.goodtech.vaadin.tabSheetLayout.PanelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a configurable tab sheet layout. The layout configuration is read from the
 * 'tabSheetDefinitionFile'.
 *
 * @param <LISTENER> The listener provided to the underlying tab sheet panels
 * @param <DTO> The data transfer object provided to the underlying tab sheet panels
 */
public class ConfigurableTabSheetLayout<LISTENER, DTO> {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurableTabSheetLayout.class);
	private volatile DTO dto;
	private final AbstractComponentContainer contentArea;
	private final Map<String, TabSheet> tabSheetMap = new HashMap<String, TabSheet>();
	private final LISTENER listener;

	/**
	 * Creates a new ConfigurableTabSheetLayout by reading the tabSheetDefinitionFile provided as parameter
	 * and instantiating new tabs with the listener(or no-args constructor if this fails).
	 * The tabSheetLayout is added to the contentArea provided as a parameter
	 *
	 * @param contentArea            the area containing the TabSheetLayout
	 * @param listener               implementation of the interface provided in the constructor of the tabs
	 * @param tabSheetDefinitionFile xml-file used for looking up tab-classes.
	 */
	public ConfigurableTabSheetLayout(final AbstractComponentContainer contentArea,
									  final LISTENER listener,
									  final String tabSheetDefinitionFile) {
		this.listener = listener;
		this.contentArea = contentArea;
		IFileReader fileReader = new FileReader(tabSheetDefinitionFile);
		TabSheetLayoutMarshaller tabSheetLayoutMarshaller = new TabSheetLayoutMarshaller();
		TabSheetLayout tabSheetLayout = tabSheetLayoutMarshaller.fromXML(fileReader.read());

		// Check if file was read successfully
		if (tabSheetLayout != null) {
			if ((tabSheetLayout.getPanelGroups() != null) && tabSheetLayout.getPanelGroups().getPanelGroup() != null) {
				for (PanelGroupType panelGroup : tabSheetLayout.getPanelGroups().getPanelGroup()) {
					// Create new tab sheet
					TabSheet tabSheet = new TabSheet();
					tabSheet.setSizeFull();

					// Add each panel to tab sheet
					for (PanelType panel : panelGroup.getPanel()) {
						createAndAddTabs(tabSheet, panel);
					}
					// Add tab sheet listener to capture tab selections
					tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
						@Override
						public void selectedTabChange(final TabSheet.SelectedTabChangeEvent event) {
							if (event.getTabSheet().getSelectedTab() instanceof IConfigurablePanel) {
								IConfigurablePanel configurablePanel = (IConfigurablePanel) event.getTabSheet().getSelectedTab();
								// Refresh selected tab
								//noinspection unchecked
								configurablePanel.refresh(dto);
							}
						}
					});
					// Add tab sheet to map
					tabSheetMap.put(panelGroup.getId(), tabSheet);
				}
			}
		}
	}

	/**
	 * Create tabs and add them to the tabSheet, throws a runtimeException if the tab does not implement
	 * an interface having a type matching the parameterizedType
	 *
	 * @param tabSheet tabSheet containing the tabs
	 * @param panel    panelType used for retrieving the panel class
	 */
	private void createAndAddTabs(final TabSheet tabSheet, final PanelType panel) {
		AbstractComponentContainer componentContainer;
		ParameterizedType parameterizedType = null;
		try {
			//Get the panel class object
			Class<?> panelClass = Class.forName(panel.getClazz());
			//Get the interfaces directly implemented by the class
			for (Type type : panelClass.getGenericInterfaces()) {
				//Check if the interface is instanceof ParameterizedType
				if (type instanceof ParameterizedType) {
					parameterizedType = (ParameterizedType) type;
					break;
				}
			}
			//If the panel does not implement an interface matching the ParameterizedType, throw exception(since componentContainer would be null)
			if (parameterizedType != null) {
				componentContainer = instantiatePanelWithListener(parameterizedType, panelClass);
			} else {
				throw new RuntimeException("Failed initializing panel: " + panel.getClazz() +
						"\n panel has no type argument matching the type argument of the defined IConfigurablePanel");
			}
			// Add panel to tab sheet
			tabSheet.addTab(componentContainer);
		} catch (InstantiationException e) {
			logger.error("InstantiationException - " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException - " + e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException - " + e.getMessage());
		}
	}

	/**
	 * @param type       generic interface of the panel
	 * @param panelClass class of the panel
	 * @return Panel instantiated with constructor having listener as parameter, or no-args constructor if this fails.
	 * @throws IllegalAccessException
	 * @throws InstantiationException if creating an instance of panelClass fails
	 */
	public AbstractComponentContainer instantiatePanelWithListener(final Type type, final Class<?> panelClass)
			throws IllegalAccessException, InstantiationException {
		try {
			//Get the actual interface(the first ParameterizedType)
			Class<?> actualType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
			/**
			 * Get the declared constructor(if any) for the interface that 'listener' is an instance of,
			 * and instantiate the class with it
			 */
			return (AbstractComponentContainer) panelClass.getDeclaredConstructor(actualType).newInstance(listener);
		}
		//if this fails instantiate the class with a default constructor
		catch (NoSuchMethodException e) {
			logger.trace("No matching constructor found, using no-arg constructor - " + e.getMessage());
			return (AbstractComponentContainer) panelClass.newInstance();
		} catch (InvocationTargetException e) {
			logger.error("InvocationTargetException - " + e.getMessage());
		}
		return null;
	}

	public void refresh(final DTO dto, final String panelGroupId) {
		this.dto = dto;

		// Lookup tab sheet in map
		TabSheet tabSheet = tabSheetMap.get(panelGroupId);

		if (tabSheet != null) {
			// Switch to selected tab sheet
			contentArea.removeAllComponents();
			contentArea.addComponent(tabSheet);

			// Refresh selected tab
			if ((tabSheet.getSelectedTab() != null) && (tabSheet.getSelectedTab() instanceof IConfigurablePanel)) {
				//noinspection unchecked
				((IConfigurablePanel)tabSheet.getSelectedTab()).refresh(dto);
			}
		}
	}
}
