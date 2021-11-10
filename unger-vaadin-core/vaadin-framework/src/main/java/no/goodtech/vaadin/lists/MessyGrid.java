package no.goodtech.vaadin.lists;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MultiSelect;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.utils.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * En tabell med standard-funksjoner som:
 * - kollapsing av kolonner
 * - oppfrisk-funksjon hvor man beholder fokus på valgt rad
 * - funksjon for å velge en rad
 * - styrt formatering av datoer og desimaltall, se {@link #setDateFormat(String)} og {@link #setDecimalFormat(DecimalFormat)}
 * - kolonner med tall blir høyre-justert
 * <p>
 * Tabellen viser i utgangspunktet alle kolonner som er tilgjengelige i containeren.
 * Du kan kollapse de kolonnene du vil med {@link #collapseColumns(String...)}.
 * <p>
 * Hvis du ønsker at tabellen skal gi deg beskjed om man trykker ENTER i tabellen,
 * bruk {@link #createShortcutKeyAwarePanelWrapper(IMessyTableActionListener)}
 *
 * @param <T> typen objekt du vil vise i tabellen
 */

public class MessyGrid<T> extends Grid<T> {

	public static final String DATE_FORMAT = Utils.DATE_FORMAT;
	public static final String DATE_TIME_FORMAT = Utils.DATETIME_FORMAT;
	protected SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
	public static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = Utils.DECIMAL_FORMAT_SYMBOLS;
	protected DecimalFormat decimalFormatter = Utils.DECIMAL_FORMAT;
	protected DecimalFormat wholeNumberFormatter = Utils.createDecimalFormat("###,###,###,##0");

	protected String rowCountColumn = null;
	protected String rowCountUnit = ApplicationResourceBundle.getInstance("vaadin-core").getString("messytable.rowcount");
	protected Integer heightByRowsWithMaxLimit;

	//private final List<HasValue.ValueChangeListener> valueChangeListeners = new ArrayList<>();
	private final boolean automaticallySelectFirstItem;

	public MessyGrid() {
		this(null, null, false);
	}

	public MessyGrid(List<T> list, final IMessyGrid<T> actionListener) {
		this(list, actionListener, true);
	}

	public MessyGrid(final IMessyGrid<T> actionListener) {
		this(null, actionListener, true);
	}

	public MessyGrid(List<T> items, final IMessyGrid<T> actionListener, final boolean automaticallySelectFirstItem) {
		this.automaticallySelectFirstItem = automaticallySelectFirstItem;

		setDataProvider(new ListDataProvider<T>(new ArrayList<>()));

		if (items != null) {
			refresh(items);
		}

		//h?yrejuster tall-kolonner
//TODO
//        for (Object propertyId : this.container.getContainerPropertyIds()) {
//			final Class<?> type = this.container.getType(propertyId);
//			if (Number.class.isAssignableFrom(type))
//        		setColumnAlignment(propertyId, Align.RIGHT);
//		}

		setSizeFull();
		setColumnReorderingAllowed(false);
//TODO        setColumnCollapsingAllowed(true);

		setMessyListener(actionListener);
	}

	/**
	 * @return true if the grid is in multi select mode and false if not
	 */
	public boolean isMultiSelect(){
		return SelectionMode.MULTI.equals(this.getSelectionModel());
	}

	public void setMessyListener(IMessyGrid<T> actionListener){
		if (actionListener != null) {
			//setSelectable(true);
			//setImmediate(true);
			//gir beskjed n?r en eller flere rader velges

			addSelectionListener(e -> {
				Set<T> selectedObjects = e.getAllSelectedItems();
				if (selectedObjects.size() == 1) {
					actionListener.objectSelected(e.getFirstSelectedItem().get());
				} else {
					actionListener.objectsSelected(selectedObjects);
				}
			});

			//gir beskjed til listener hvis noen har dobbeltklikket i tabellen
			addItemClickListener(event -> {
				if (event.getMouseEventDetails().isDoubleClick()) {
					final Object itemId = event.getItem();
					if (itemId != null) {
						actionListener.doubleClick((T) itemId);
					}
				}

			});

			addShortcutListener(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER, null) {
				public void handleAction(Object sender, Object target) {
					if (getSelectedItems().size() == 1) {
						final T selectedObject = getSelectedItems().iterator().next();
						actionListener.doubleClick(selectedObject);
					}
				}
			});

			addShortcutListener(new ShortcutListener("DELETE", ShortcutAction.KeyCode.DELETE, null) {
				public void handleAction(Object sender, Object target) {
					if (getSelectedItems().size() == 1) {
						final T selectedObject = getSelectedItems().iterator().next();
						actionListener.pleaseDelete(selectedObject);
					}
				}
			});
		}
	}


//	public void addValueChangeListener(final Property.ValueChangeListener valueChangeListener) {
//		super.addValueChangeListener(valueChangeListener);
//		if (!valueChangeListeners.contains(valueChangeListener)) {
//			valueChangeListeners.add(valueChangeListener);
//		}
//	}

	/**
	 * @return gir deg valgt objekt eller null om ingen objekt er valgt.
	 * For multi-select, se {@link #getSelectedItems()} ()}.
	 */
	public T getSelectedItem() {
		return asSingleSelect().getValue();
	}

	/**
	 * The current selection can be obtained from the Grid by getSelectedItems(), and the returned Set
	 * contains either only one item (in single-selection mode) or several items (in multi-selection mode).
	 * @return
	 */
//	public Set<T> getSelectedObject() {
//		return getSelectedItems();
//	}

	/**
	 * @return chosen objects or empty set if no objects chosen
	 * @Deprecated bruk getSelectedItems()
	 */
	@Deprecated
	public Set<T> getSelectedObjects() {
		Set<T> result = new HashSet<T>();

		GridSelectionModel<T> model = getSelectionModel();
		if (model instanceof MultiSelectionModel) {
			MultiSelect<T> multiSelect = asMultiSelect();
			if (!multiSelect.isEmpty()) {
				result.addAll(multiSelect.getSelectedItems());
			}
		} else {
			SingleSelect<T> selection = asSingleSelect();
			if (!selection.isEmpty()) {
				result.add(selection.getValue());
			}
		}
		return result;
	}

	/**
	 * Fyller tabellen med angitt liste av objekter.
	 * F?rste objekt i lista blir valgt automatisk
	 *
	 * @param objects objektene som skal vises
	 */
	public void refresh(List<? extends T> objects) {

		Set<T> activeItems = getSelectedItems();

		// Temporary disable value change listeners, since removeAllItems() triggers value change event
//        for (HasValue.ValueChangeListener valueChangeListener : valueChangeListeners) {
//            this.removeValueChangeListener(valueChangeListener);
//        }
//
		// Forget what we have previously selected
		deselectAll();
//
		// Re-populate table
		setItems((Collection<T>) objects);

//		this.removeAllItems();
//		this.addItems(objects);
//
//        // Enable value change listeners again
//        for (HasValue.ValueChangeListener valueChangeListener : valueChangeListeners) {
//            this.addValueChangeListener(valueChangeListener);
//        }
//
		// Set column footer (if specified)
		if (rowCountColumn != null) {
			FooterRow row = this.getFooter().getRow(0);
			row.getCell(rowCountColumn).setText(String.valueOf(objects.size() + " " + rowCountUnit));
		}
//
//        // Determine obect(s) to be re-selected
//        Set<T> itemsToSelect = new HashSet<>();
//        for (T activeItem : activeItems) {
//            if (containsId(activeItem)) {     // Previously selected might be removed from table
//                itemsToSelect.add((T) ((BeanItem) getItem(activeItem)).getBean());
//            }
//        }
//
//        // Automatically select first element (if non selected)
//        if (automaticallySelectFirstItem) {
//            if (itemsToSelect.size() == 0) {
//                T firstItem = (T)firstItemId();
//
//                if (firstItem != null) {
//                    itemsToSelect.add(firstItem);
//                }
//            }|
//        }
//
//        // Re-select appropriate object(s)
//        if (itemsToSelect.size() > 0) {
//            if (isMultiSelect()) {
//                setValue(itemsToSelect);
//            } else {
//                setValue(itemsToSelect.iterator().next());
//            }
//        } else {
//            // In case list is empty, we still need to trigger value change event
//            fireValueChange(false);
//        }
		if (heightByRowsWithMaxLimit != null) {
			if (objects.size() > heightByRowsWithMaxLimit) {
				setHeightByRows(heightByRowsWithMaxLimit);
			} else if (objects.size() == 0){
				setHeightByRows(1);
			} else {
				setHeightByRows(objects.size());
			}
		}
	}

	public void refresh(Set<? extends T> objects) {
		refresh(new ArrayList<>(objects));
	}

	@Override
	public ListDataProvider<T> getDataProvider() {
		return (ListDataProvider<T>) super.getDataProvider();
	}

	/**
	 * Select specified object. Previous selections will be de-selected.
	 *
	 * @param object Object to be selected
	 * @Deprecated Fysj.. Bruk componentens metoder direkte
	 */
	@Deprecated
	public void setSelectedObject(T object) {
		if (object != null) {
			//setValue(null);
			select(object);
		}
	}

	/**
	 * Skjul angitte kolonner
	 * @param columns navn p? kolonnene
	 */
//	public void collapseColumns(String... columns) {
//		for (String column : columns)
//			setColumnCollapsed(column, true);
//	}

	/**
	 * Angi format for datoer hvis du ?nsker annen formatering enn {@link #DATE_TIME_FORMAT}
	 *
	 * @param pattern formaterings-m?nster
	 */
	public void setDateFormat(String pattern) {
		dateFormatter = new SimpleDateFormat(pattern);
	}

	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	/**
	 * Angi format for desimaltall hvis du ?nsker annen formatering enn Utils.DECIMAL_FORMAT
	 */
	public void setDecimalFormat(DecimalFormat decimalFormat) {
		decimalFormatter = decimalFormat;
	}

	public DecimalFormat getDecimalFormatter() {
		return decimalFormatter;
	}

//TODO Vaadin8
//	@Override
//	protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
//		Object value = property.getValue();
//
//		//formaterer dato-, tall- og boolske kolonner iht. til spesielle regler
//		if (value != null) {
//	        Class<?> type = property.getType();
//
//			if (type == Date.class)
//				return dateFormatter.format(value);
//
//			if (type == Double.class  || type == Float.class)
//	            return decimalFormatter.format(value);
//
//	        if (value instanceof Number)
//	            return wholeNumberFormatter.format(value);
//
//	        if (value instanceof Boolean) {
//	        	Boolean booleanValue = (Boolean) value;
//        		return Texts.get("messytable.booleanValue." + booleanValue);
//	        }
//		}
//		return super.formatPropertyValue(rowId, colId, property);
//	}

//	/**
//	 * Bruk denne for ? f? beskjed hvis ENTER-tasten brukes n?r tabellen har fokus
//	 *
//	 * @param actionListener du f?r beskjed via denne
//	 * @return en wrapper rundt tabellen som du skal legge i panelet ditt
//	 */
//	public Panel createShortcutKeyAwarePanelWrapper(final IMessyTableActionListener<T> actionListener) {
//		Panel panel = new Panel(this); //m? pakke inn tabellen for ? kunne h?ndtere hurtigtaster
//		panel.setSizeFull();
//		panel.addAction(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER, null) {
//			public void handleAction(Object sender, Object target) {
//				if (getSelectedItems().size() == 1) {
//					final T selectedObject = getSelectedItems().iterator().next();
//					actionListener.doubleClick(selectedObject);
//				}
//			}
//		});
//		panel.addAction(new ShortcutListener("DELETE", ShortcutAction.KeyCode.DELETE, null) {
//			public void handleAction(Object sender, Object target) {
//				if (getSelectedItems().size() == 1) {
//					final T selectedObject = getSelectedItems().iterator().next();
//					actionListener.pleaseDelete(selectedObject);
//				}
//			}
//		});
//		return panel;
//	}
	public String getRowCountColumn() {
		return rowCountColumn;
	}

	/**
	 * Show row count in a summary row in this column. Null if you don't want to see the row count
	 */
	public void setRowCountColumn(String rowCountColumn) {
		if (rowCountColumn != null) {
			appendFooterRow();
		}
		getFooter().setVisible(rowCountColumn != null);
		this.rowCountColumn = rowCountColumn;
	}

	public String getRowCountUnit() {
		return rowCountUnit;
	}

	public void setRowCountUnit(String rowCountUnit) {
		this.rowCountUnit = rowCountUnit;
	}

	public Collection<T> getItems() {
		return getDataProvider().getItems();
	}

	/**
	 * Adjust height to current row count but not more than given limit
	 * @param heightByRowsWithMaxLimit max height (in rows)
	 */
	public void setHeightByRowsWithMaxLimit(Integer heightByRowsWithMaxLimit) {
		this.heightByRowsWithMaxLimit = heightByRowsWithMaxLimit;
	}
}
