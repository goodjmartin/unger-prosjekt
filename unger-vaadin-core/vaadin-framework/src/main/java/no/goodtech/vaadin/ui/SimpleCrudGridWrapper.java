package no.goodtech.vaadin.ui;


import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import no.goodtech.persistence.entity.Entity;
import no.goodtech.persistence.entity.EntityStub;
import no.goodtech.persistence.jpa.AbstractFinder;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.CopyButton;
import no.goodtech.vaadin.buttons.DetailButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.buttons.export.CsvExportButton;
import no.goodtech.vaadin.buttons.export.ExcelExportButton;
import no.goodtech.vaadin.layout.ComponentWrapper;
import no.goodtech.vaadin.lists.*;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.search.FilterPanel;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.ui.ISimpleCrud;
import no.goodtech.vaadin.ui.Texts;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

/**
 * Wrap your grid in a nice CRUD-wrap. Suitable for tables within forms.
 *
 */
public abstract class SimpleCrudGridWrapper<ENTITY extends Entity, ENTITYSTUB extends EntityStub<ENTITY>, FINDER extends AbstractFinder>
		extends ComponentWrapper implements IMenuView, ISimpleCrud<ENTITY, ENTITYSTUB> {
	public final MessyGrid<ENTITYSTUB> grid;
	protected FINDER finder;
	protected MenuBar.MenuItem deleteButton, addButton, copyButton, detailsButton;
	protected ExcelExportButton excelExportButton;
	protected CsvExportButton csvExportButton;
	protected String accessFunctionViewId, accessFunctionEditId;
	public FilterPanel filterPanel;
	protected Registration listener;
	private static final String FILE_DOWLOAD_EXCEL_BUTTON = "FILE_DOWNLOAD_EXCEL_BUTTON";
	private static final String FILE_DOWLOAD_CSV_BUTTON = "FILE_DOWNLOAD_CSV_BUTTON";

	protected boolean readOnly = false;

	public SimpleCrudGridWrapper(MessyGrid grid) {
		this(grid, null, false);
	}

	public SimpleCrudGridWrapper(MessyGrid grid, FilterPanel filterPanel) {
		this(grid, null, filterPanel, false);
	}

	public SimpleCrudGridWrapper(MessyGrid grid, FINDER finder) {
		this(grid, finder, false);
	}

	public SimpleCrudGridWrapper(MessyGrid grid, FINDER finder, boolean readOnly) {
		this(grid, finder, null, readOnly);
	}

	/**
	 * Create CRUD component. You must call {@link #refresh()} to fill it with data afterwords
	 *
	 * @param grid     the grid to display the list of objects you like to CRUD
	 * @param readOnly if true, add, edit and delete will not be allowed. You may also call {@link #setReadOnly(boolean)} later
	 */
	public SimpleCrudGridWrapper(MessyGrid grid, FINDER finder, FilterPanel filterPanel, boolean readOnly) {
		this.grid = grid;
		this.finder = finder;
		this.readOnly = readOnly;

		final AccessFunction accessFunctionView = getAccessFunctionView();
		if (accessFunctionView != null) {
			accessFunctionViewId = accessFunctionView.getId();
			AccessFunctionManager.registerAccessFunction(accessFunctionView);
		}

		final AccessFunction accessFunctionEdit = getAccessFunctionEdit();
		if (accessFunctionEdit != null) {
			accessFunctionEditId = accessFunctionEdit.getId();
			AccessFunctionManager.registerAccessFunction(accessFunctionEdit);
		}

		this.filterPanel = filterPanel;
		if (filterPanel != null) {
			//refresh table data provider when user clicks refresh button in filter panel
			this.filterPanel.setActionListener((FilterPanel.FilterActionListener<FINDER>) f -> {
				//grid.setDataProvider(DataProviderUtils.createDataProvider(f));
				return null;
			});
			addComponent(this.filterPanel);
//			setComponentAlignment(filterPanel, Alignment.BOTTOM_LEFT);
		}

		VerticalLayout wrappedComponent = wrapComponent(grid);
		setExpandRatio(wrappedComponent, 1.0F);

		this.addButton = addButton(new AddButton(null), selectedItem -> add());
		this.detailsButton = addButton(new DetailButton(null), selectedItem -> details());
		this.deleteButton = addButton(new RemoveButton(null), selectedItem -> delete());
		this.copyButton = addButton(new CopyButton(null), selectedItem -> copy());
		copyButton.setVisible(isCopySupported());

		excelExportButton = new ExcelExportButton(grid, getExportFileCaption());
		csvExportButton = new CsvExportButton(grid, getExportFileCaption());
		if (this instanceof IExportable) {
			getMenuBar().addItem(excelExportButton.getCaption(), excelExportButton.getIcon(),
					(MenuBar.Command) selectedItem -> Page.getCurrent().getJavaScript().execute("document.getElementById('" + FILE_DOWLOAD_EXCEL_BUTTON + "').click();"));
			getMenuBar().addItem(csvExportButton.getCaption(), csvExportButton.getIcon(),
					(MenuBar.Command) selectedItem -> Page.getCurrent().getJavaScript().execute("document.getElementById('" + FILE_DOWLOAD_CSV_BUTTON + "').click();"));
		}

		// Control buttons via grid selection
		new GridSelectionComponentDisabler(grid, detailsButton).singleRowSelectionRequired(!grid.isMultiSelect()).apply();
		new GridSelectionComponentDisabler(grid, deleteButton, copyButton).selectionOk(() -> !isReadOnly()).apply();

		setEnabledButtons();

		//added "listener" variable to get ahold of listener in other classes - NEW
		listener =  grid.addItemClickListener(event -> {
			if (event.getMouseEventDetails().isDoubleClick() && detailsButton.isVisible()) {
				grid.select(event.getItem());
				details((ENTITYSTUB) event.getItem());
			}
		});

		// Hide owner columns as default for wrapper panels
		if (grid instanceof MessyGridChildEntity) {
			((MessyGridChildEntity) grid).hideOwnerColumns();
		}

		createShortcutKeyListeners();
		setMargin(false);
		setSizeFull();
		addComponent(createExcelExportMenuItem());
		addComponent(createCsvExportMenuItem());
	}

	private Button createExcelExportMenuItem() {
		Button invisibleDownloadButton = new Button();
		/*invisibleDownloadButton.setId(FILE_DOWLOAD_EXCEL_BUTTON);
		invisibleDownloadButton.addStyleName("invisible-button");

		StreamResource excelStreamResource = new StreamResource((StreamResource.StreamSource) () -> excelExportButton.doExportGrid(grid),
				excelExportButton.getExportFileName() + ".xls");

		FileDownloader downloader = new FileDownloader(excelStreamResource);
		downloader.extend(invisibleDownloadButton);*/
		return invisibleDownloadButton;
	}

	private Button createCsvExportMenuItem() {
		Button invisibleDownloadButton = new Button();
		invisibleDownloadButton.setId(FILE_DOWLOAD_CSV_BUTTON);
		invisibleDownloadButton.addStyleName("invisible-button");

		/*
		StreamResource excelStreamResource = new StreamResource((StreamResource.StreamSource) () -> csvExportButton.doExportGrid(grid),
				csvExportButton.getExportFileName() + ".csv");

		FileDownloader downloader = new FileDownloader(excelStreamResource);
		downloader.extend(invisibleDownloadButton);*/
		return invisibleDownloadButton;
	}

	/*
	//TODO Maybe use this in combination with a progressbarwindow
	private void exportInInNewThread() {
//		UI ui  = UI.getCurrent();
		CompletableFuture<Void> c = CompletableFuture.supplyAsync(() -> {
			try {
//				ui.access(() -> {

//					Notification.show("Generating document...");
//				});
				excelExportButton.setEnabled(false);
				excelExportButton.doExport();
			} catch (CancellationException e) {
			}
			return "Finished";
		}).thenAccept(s -> {
			System.out.println(s);
			excelExportButton.setEnabled(true);
//			ui.access(() -> {
//				Notification.show(s);
//			});
		});

	}*/

	protected void setEnabledButtons() {
		if (isReadOnly()) {
			enableTableSelectionButtons(false);
			addButton.setEnabled(false);
		} else {
			addButton.setEnabled(true);
		}
	}

	/**
	 * Enable/disable removeButton, editButton, detailButton and copyButton (all buttons that usually depends on a selected item)
	 */
	private void enableTableSelectionButtons(boolean enable){
		if(deleteButton != null){
			deleteButton.setEnabled(enable);
		}
		if(detailsButton != null){
			detailsButton.setEnabled(enable);
		}
		if(copyButton != null){
			copyButton.setEnabled(enable);
		}
	}

	protected abstract AccessFunction getAccessFunctionView();

	protected abstract AccessFunction getAccessFunctionEdit();

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		if (getComponentCount() == 0) {
			if (filterPanel != null) {
				addComponents(filterPanel);
			}
			wrapComponent(grid);
//			setExpandRatio(table, 1);
		}
		if (filterPanel != null) {
			filterPanel.refresh(event.getParameters());
		}
		refresh();
	}

	public boolean isAuthorized(User user, String value) {
		if (accessFunctionEditId != null)
			setReadOnly(!AccessFunctionManager.isAuthorized(user, accessFunctionEditId));

		setEnabledButtons();

		if (accessFunctionViewId != null) {
			return AccessFunctionManager.isAuthorized(user, accessFunctionViewId);
		}
		return true;
	}

	/**
	 * Generates shortcut listeners for the table
	 */
	public void createShortcutKeyListeners() {
		addShortcutListener(new ShortcutListener("ENTER-listener", ShortcutAction.KeyCode.ENTER, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null && target.equals(grid) && detailsButton.isEnabled() && detailsButton.isVisible()) {
					details();
				}
			}
		});
		addShortcutListener(new ShortcutListener("DELETE-listener", ShortcutAction.KeyCode.DELETE, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null && target.equals(grid) && deleteButton.isEnabled() && deleteButton.isVisible()) {
					delete();
				}
			}
		});
		addShortcutListener(new ShortcutListener("INSERT-listener", ShortcutAction.KeyCode.INSERT, null) {
			@Override
			public void handleAction(Object sender, Object target) {
				if (target != null && target.equals(grid) && addButton.isEnabled() && addButton.isVisible()) {
					add();
				}
			}
		});
	}

	@Override
	public void refresh() {
		if (filterPanel != null) {
			this.filterPanel.search();
		} else {
			if (finder != null)
				grid.refresh(finder.list());
			else
				grid.getDataProvider().refreshAll();
		}
	}

	protected MenuBar.MenuItem addButton(String caption, Resource icon, MenuBar.Command command) {
		return getTools().addItem(caption, icon, command);
	}

	/**
	 * Convenience method for adding menu items. Will use provided button caption and icon, but NOT the button click listener
	 */
	private MenuBar.MenuItem addButton(Button button, MenuBar.Command command) {
		return getTools().addItem(button.getCaption(), button.getIcon(), command);
	}

	/**
	 * Please override this if you want a better confirm message before deletion or no confirmation at all
	 *
	 * @param entity the object to delete
	 * @return confirmation question or null to don't ask for confirmation
	 */
	@Override
	public String getDeleteConfirmMessage(ENTITYSTUB entity) {
		return Texts.get("simpleCrudAdminPanel.delete.confirmation");
	}

	@Override
	public Set<ENTITYSTUB> getSelectedItems(){
		return grid.getSelectedItems();
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public FINDER getFinder() {
		return finder;
	}

	public void setFinder(FINDER finder) {
		this.finder = finder;
	}

	protected String getExportFileCaption() {
		return null;
	}

	/**
	 * Default implementation
	 */
	@Override
	public String getViewName() {
		return null;
	}

	public Registration getListener() {
		return listener;
	}

	public void removeListener() {
		listener.remove();
	}

	/**
	 * Used after a refresh to make sure the grid keeps the current selection
	 *
	 * @param items
	 */
	@Override
	public void setSelectedItems(Set<ENTITYSTUB> items) {
		GridSelectionModel selectionModel = grid.getSelectionModel();
		if (selectionModel instanceof MultiSelectionModel) {
			grid.asMultiSelect().setValue(items);
		} else {
			//grid.asSingleSelect().select(items.iterator().next());
		}
	}
}

