package no.goodtech.vaadin.search;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.UrlUtils;
import no.goodtech.vaadin.buttons.RefreshButton;
import no.goodtech.vaadin.lists.RowCountProvider;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.search.FilterPanel.IMaxRowsAware;

import java.util.Arrays;
import java.util.ListIterator;

/**
 * A standard filter panel with field for: Max rows, show disabled objects and a search/refresh button.
 * The panel contains a search/refresh button and fields for max rows and show disabled objects
 * The user may use ENTER as a shortcut key to search.
 *
 * @param <FINDER> the finder you use to search for data
 */
public abstract class FilterPanel<FINDER extends IMaxRowsAware> extends Panel {

	protected FilterActionListenerForLazyDataProviders<FINDER> actionListenerForLazyDataProviders;

	protected MaxRowsTextField maxRowsTextField=null;
	protected final CheckBox disabledCheckBox;
	protected final CountLabel countLabel;
	protected HorizontalLayout layout=null;
	protected final RefreshButton searchButton;
	protected FilterActionListener<FINDER> actionListener;
	private HorizontalLayout advancedSearchFilterLayout;


	// Hack: Set this to false whenever setSelectFromClause is overridden in a filter - see MaterialLotFilterPanel->setLocationId()
	protected boolean showFinderCount = true;

	public interface FilterActionListenerForLazyDataProviders<FINDER> {
		/**
		 * The panel wants to execute search
		 * @param finder use this to run the query
		 * @param rowCountProvider provide row count through this
		 * @return row count. Null if you don't want to show row count in panel
		 */
		void pleaseSearch(FINDER finder, final RowCountProvider rowCountProvider);
	}

	public interface IMaxRowsAware {
		void setMaxResults(Integer maxResults);

		IMaxRowsAware setIncludeDisabled();
	}

	/**
	 * You will get a message through this when the user clicks the search button
	 *
	 * @param <FINDER> the finder will be decorated with max rows according to the content of the {@link MaxRowsTextField}
	 * @return row count. Null if you don't want to show row count in panel 
	 */
	public interface FilterActionListener<FINDER> {
		Integer pleaseSearch(FINDER finder);
	}

	/**
	 * Create the panel with default values for the maxRows field
	 * If you like to get notified when the user trigger a search,
	 * use {@link #setActionListener(FilterActionListener)} later
	 */
	public FilterPanel() {
		this(null);
	}

	/**
	 * Create the panel with default values for the maxRows field
	 *
	 * @param listener you will get a message through this when the user clicks the search button
	 * @see MaxRowsTextField
	 */
	public FilterPanel(final FilterActionListener<FINDER> listener) {
		this(listener, null, null);
	}

	/**
	 * Create the panel
	 * @param listener you will get a message through this when the user clicks the search button
	 */
	public FilterPanel(final FilterActionListener<FINDER> listener, boolean isNewLayout) {
		this(listener, null, isNewLayout, false);
	}

	public FilterPanel(final FilterActionListener<FINDER> listener, boolean isNewLayout, boolean isSingleRow) {
		this(listener, null, isNewLayout, isSingleRow);
	}

	private FilterPanel(final FilterActionListener<FINDER> actionListenerForEagerDataProviders,
						FilterActionListenerForLazyDataProviders<FINDER> actionListenerForLazyDataProviders,
						boolean isNewLayout, boolean isSingleRow) {



		this.actionListener = actionListenerForEagerDataProviders;
		this.actionListenerForLazyDataProviders = actionListenerForLazyDataProviders;

		setSizeUndefined();

		//Remove borders and background
		addStyleName(ValoTheme.PANEL_BORDERLESS);

		disabledCheckBox = new CheckBox(getText("filterPanel.disabled.caption"));
		countLabel = new CountLabel();
		searchButton = new RefreshButton(this::search);

		if (isNewLayout) {
			setContent(buildSimpleLayout(isSingleRow));
		} else {
			setContent(buildClassicLayout());
		}

		hideDisabledCheckbox();
	}

	private Layout buildClassicLayout() {

		addAction(new ShortcutListener("FilterPanel-ENTER", KeyCode.ENTER, null) {
			public void handleAction(Object sender, Object target) {
				search();
			}
		});

		advancedSearchFilterLayout = new HorizontalLayout(disabledCheckBox, searchButton, countLabel);
		for (Component aLayout : advancedSearchFilterLayout) {
			advancedSearchFilterLayout.setComponentAlignment(aLayout, Alignment.BOTTOM_RIGHT);
		}
		advancedSearchFilterLayout.setMargin(false);
		advancedSearchFilterLayout.setSizeFull();
		advancedSearchFilterLayout.setWidth(100, Unit.PERCENTAGE);
		//layout.setHeight(70,Unit.PIXELS);
		advancedSearchFilterLayout.setComponentAlignment(countLabel, Alignment.BOTTOM_LEFT);
		advancedSearchFilterLayout.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
		//Style used to increase padding so that comboBoxes are not cut off at the bottom
		advancedSearchFilterLayout.addStyleName("filterPanelContent");
		advancedSearchFilterLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		return advancedSearchFilterLayout;
	}

	private Layout buildSimpleLayout(boolean isSingleRow) {
		// Build simple search panel
		simpleSearchFilterComponent = new SimpleSearchComponent();
		simpleSearchFilterComponent.getSearchButton().addClickListener(e -> search());
		simpleSearchFilterComponent.getClearButton().addClickListener(e -> {
			simpleSearchFilterComponent.clear();
			search();
		});
		simpleSearchFilterComponent.addTextValueChangeListener(e -> search());

		advancedSearchFilterLayout = new HorizontalLayout(disabledCheckBox);
		for (Component aLayout : advancedSearchFilterLayout) {
			advancedSearchFilterLayout.setComponentAlignment(aLayout, Alignment.BOTTOM_RIGHT);
		}
		advancedSearchFilterLayout.setMargin(false);

		//Style used to increase padding so that comboBoxes are not cut off at the bottom
		advancedSearchFilterLayout.addStyleName("filterPanelContent");
		advancedSearchFilterLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

		if(isSingleRow){
			return createSingleRowFilter();
		} else {
			return createMultiFilterRow();
		}
	}

	private VerticalLayout createMultiFilterRow(){
		final VerticalLayout contentPanel = new VerticalLayout(simpleSearchFilterComponent, advancedSearchFilterLayout);
		contentPanel.setMargin(false);
		contentPanel.setMargin(new MarginInfo(false, false, true, false));
		return contentPanel;
	}

	private HorizontalLayout createSingleRowFilter(){

		//this does not work, might be because the components of advancedSearch is not instantiated ye
		/*
		for (Component component : advancedSearchFilterLayout){
			simpleSearchFilterComponent.addComponents(component);
		}
		*/

		simpleSearchFilterComponent.addComponents(advancedSearchFilterLayout);
		final HorizontalLayout contentPanel = new HorizontalLayout(simpleSearchFilterComponent);

		contentPanel.setMargin(false);
		contentPanel.setMargin(new MarginInfo(false, false, true, false));
		return contentPanel;
	}

	/**
	 * Create the panel
	 *
	 * @param listener       you will get a message through this when the user clicks the search button
	 * @param maxRowsDefault initial value for max rows
	 * @param maxRowsLimit   upper limit for max rows (the user can't set more than this)
	 */
	public FilterPanel(final FilterActionListener<FINDER> listener, Integer maxRowsDefault, Integer maxRowsLimit) {
		actionListener = listener;

		setSizeUndefined();

		//Remove borders and background
		addStyleName(ValoTheme.PANEL_BORDERLESS);

		maxRowsTextField = new MaxRowsTextField(maxRowsDefault);
		maxRowsTextField.setUpperLimit(maxRowsLimit);
		maxRowsTextField.setSizeUndefined();
		maxRowsTextField.setCaption(getText("filterPanel.maxRows.caption"));

		disabledCheckBox = new CheckBox(getText("filterPanel.disabled.caption"));

		countLabel = new CountLabel();
		
		searchButton = new RefreshButton(this::search);
		addAction(new ShortcutListener("FilterPanel-ENTER", KeyCode.ENTER, null) {
			public void handleAction(Object sender, Object target) {
				search();
			}
		});

		layout = new HorizontalLayout(maxRowsTextField, disabledCheckBox, searchButton, countLabel);
		for (Component aLayout : layout) {
			layout.setComponentAlignment(aLayout, Alignment.BOTTOM_RIGHT);
		}
		layout.setMargin(false);
		layout.setSizeFull();
		layout.setComponentAlignment(countLabel, Alignment.BOTTOM_LEFT);
		//Style used to increase padding so that comboBoxes are not cut off at the bottom
		layout.addStyleName("filterPanelContent");
		layout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		setContent(layout);
	}

	/**
	 * Trigger search
	 */
	public void search() {
		final FINDER finder = getFinder();
		finder.setMaxResults(getMaxRows());
		if (disabledCheckBox.getValue() != null && disabledCheckBox.getValue().booleanValue()) {
			finder.setIncludeDisabled();
		}
		Integer finderCount = null;
		if (finder instanceof AbstractFinder && showFinderCount){
			finderCount = ((AbstractFinder)finder).count();
		}
		Integer showCount = actionListener.pleaseSearch(finder);
		countLabel.refresh(showCount, finderCount);
		maxRowsTextField.refresh(finderCount);
	}

	/**
	 * @return content of the 'max rows' field
	 */
	public Integer getMaxRows() {
		return maxRowsTextField.getMaxRows();
	}

	/**
	 * Provide default 'max rows' and upper limit for max rows
	 *
	 * @param current will limit the search to return max this number of rows
	 * @param limit   the user cannot get more rows than this
	 */
	public void setMaxRows(int current, int limit) {
		maxRowsTextField.setValue(String.valueOf(current));
		maxRowsTextField.setUpperLimit(limit);
	}

	/**
	 * Add components before maxRows field and search button
	 *
	 * @param components the components to add
	 */
	protected void addComponents(Component... components) {
		final ListIterator<Component> iterator = Arrays.asList(components).listIterator(components.length);
		while (iterator.hasPrevious()) {
			layout.addComponentAsFirst(iterator.previous());
		}
	}

	private String getText(String key) {
		return ApplicationResourceBundle.getInstance("vaadin-core").getString(key);
	}

	/**
	 * @return the finder you use to search for data. Please decorate it with your specific filters
	 */
	public abstract FINDER getFinder();

	/**
	 * Use this if you like to control the filters through url parameters
	 * Use the following parameters: maxRows, disabled
	 *
	 * @param url url that contains the parameters
	 */
	public void refresh(String url) {
		if (url != null) {
			final UrlUtils urlUtils = new UrlUtils(url);
			final String maxRows = urlUtils.getParameter("maxRows");
			if (maxRows != null) {
				maxRowsTextField.setValue(maxRows);
			}

			final String disabled = urlUtils.getParameter("disabled");
			if (disabled != null) {
				disabledCheckBox.setValue(Boolean.valueOf(disabled));
			}
		}
	}

	/**
	 * For testing
	 */
	void triggerSearch() {
		search();
	}

	public FilterActionListener<FINDER> getActionListener() {
		return actionListener;
	}

	public void setActionListener(FilterActionListener<FINDER> actionListener) {
		this.actionListener = actionListener;
	}

	public boolean isShowFinderCount() {
		return showFinderCount;
	}

	public void setShowFinderCount(boolean showFinderCount) {
		this.showFinderCount = showFinderCount;
	}

	public void hideDisabledCheckbox() {
		disabledCheckBox.setVisible(false);
	}

	private SimpleSearchComponent simpleSearchFilterComponent;

	public String getFilterString() {
		if (simpleSearchFilterComponent.isEnabled() && simpleSearchFilterComponent.isVisible()) {
			return simpleSearchFilterComponent.getFilterString();
		}
		return null;
	}

}
