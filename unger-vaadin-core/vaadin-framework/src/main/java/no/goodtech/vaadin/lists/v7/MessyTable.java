package no.goodtech.vaadin.lists.v7;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Panel;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.ui.Texts;
import no.goodtech.vaadin.utils.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * En tabell med standard-funksjoner som:
 * - kollapsing av kolonner
 * - oppfrisk-funksjon hvor man beholder fokus på valgt rad
 * - funksjon for å velge en rad
 * - styrt formatering av datoer og desimaltall, se {@link #setDateFormat(String)} og {@link #setDecimalFormat(DecimalFormat)}
 * - kolonner med tall blir høyre-justert
 *
 * Tabellen viser i utgangspunktet alle kolonner som er tilgjengelige i containeren.
 * Du kan kollapse de kolonnene du vil med {@link #collapseColumns(String...)}.
 *
 * Hvis du ønsker at tabellen skal gi deg beskjed om man trykker ENTER i tabellen, 
 * bruk {@link #createShortcutKeyAwarePanelWrapper(IMessyTableActionListener)}
 *
 * @param <T> typen objekt du vil vise i tabellen
 */
@Deprecated
public class MessyTable<T> extends Table {
	
	public static final String DATE_FORMAT = Utils.DATE_FORMAT;
	public static final String DATE_TIME_FORMAT = Utils.DATETIME_FORMAT;
	protected SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
    public static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = Utils.DECIMAL_FORMAT_SYMBOLS;
    protected DecimalFormat decimalFormatter = Utils.DECIMAL_FORMAT;
	protected DecimalFormat wholeNumberFormatter = Utils.createDecimalFormat("###,###,###,##0");

	protected BeanItemContainer<T> container;
	protected String rowCountColumn = null;
	protected String rowCountUnit = ApplicationResourceBundle.getInstance("vaadin-core").getString("messytable.rowcount");
    private final List<ValueChangeListener> valueChangeListeners = new ArrayList<>();
    private final boolean automaticallySelectFirstItem;

    public MessyTable(BeanItemContainer<T> container, final IMessyTableActionListener<T> actionListener) {
        this(container, actionListener, true);
    }

	public MessyTable(BeanItemContainer<T> container, final IMessyTableActionListener<T> actionListener, final boolean automaticallySelectFirstItem) {
		this.container = container;
        this.automaticallySelectFirstItem = automaticallySelectFirstItem;
        setContainerDataSource(container);

		//høyrejuster tall-kolonner
        for (Object propertyId : this.container.getContainerPropertyIds()) {
			final Class<?> type = this.container.getType(propertyId);
			if (Number.class.isAssignableFrom(type))
        		setColumnAlignment(propertyId, Align.RIGHT);
		}

		setSizeFull();
        setColumnReorderingAllowed(false);
        setColumnCollapsingAllowed(true);
        
        if (actionListener != null) {
    		setSelectable(true);
    		setImmediate(true);
            //gir beskjed når en eller flere rader velges
			addValueChangeListener(new ValueChangeListener() {
				public void valueChange(Property.ValueChangeEvent event) {
					final Object value = event.getProperty().getValue();
					if (!isMultiSelect()) {
		                T selectedObject = (T) value;
		
		                //Gi beskjed til listener om hvilke signal som er valgt
		                actionListener.objectSelected(selectedObject);
					} else {
		                Set<T> selectedObjects = new HashSet<T>();
		                Collection<?> values = (Collection<?>) value;
		                for (Object object : values)
		                	selectedObjects.add((T) object);
		                actionListener.objectsSelected(selectedObjects);
					}
				}
			});
			
			//gir beskjed til listener hvis noen har dobbeltklikket i tabellen 
			addItemClickListener(new ItemClickListener() {
                public void itemClick(ItemClickEvent event) {
                    if (event.isDoubleClick()) {
                        final Object itemId = event.getItemId();
                        if (itemId != null)
                            actionListener.doubleClick((T) itemId);
                    }
                }
            });
        }

	}

    public void addValueChangeListener(final ValueChangeListener valueChangeListener) {
        super.addValueChangeListener(valueChangeListener);
        if (!valueChangeListeners.contains(valueChangeListener)) {
            valueChangeListeners.add(valueChangeListener);
        }
    }

	/**
	 * @return gir deg valgt objekt eller null om ingen objekt er valgt.
	 * For multi-select, se {@link #getSelectedObjects()}.
	 */
	public T getSelectedObject() {
		return (T) getValue();
	}

    /**
	 * @return chosen objects or empty set if no objects chosen
	 */
	public Set<T> getSelectedObjects() {
		Set<T> result = new HashSet<T>();
		if (isMultiSelect()) {
			Set<T> selectedObjects = (Set<T>) getValue();
			result.addAll(selectedObjects);
		} else {
			T value = (T) getValue();
			if (value != null)
				result.add(value);
		}
		return result;			
	}

	/**
	 * Fyller tabellen med angitt liste av objekter.
	 * Første objekt i lista blir valgt automatisk  
	 * @param objects objektene som skal vises
	 */
	public void refresh(List<? extends T> objects) {
        // Get currently selected object(s)
		Set<T> activeItems = getSelectedObjects();

        // Temporary disable value change listeners, since removeAllItems() triggers value change event
        for (ValueChangeListener valueChangeListener : valueChangeListeners) {
            this.removeValueChangeListener(valueChangeListener);
        }

        // Forget what we have previously selected
        setValue(null);

        // Re-populate table
		this.removeAllItems();
		this.addItems(objects);

        // Enable value change listeners again
        for (ValueChangeListener valueChangeListener : valueChangeListeners) {
            this.addValueChangeListener(valueChangeListener);
        }

        // Set column footer (if specified)
		if (rowCountColumn != null) {
            setColumnFooter(rowCountColumn, String.valueOf(objects.size() + " " + rowCountUnit));
        }

        // Determine obect(s) to be re-selected
        Set<T> itemsToSelect = new HashSet<>();
        for (T activeItem : activeItems) {
            if (containsId(activeItem)) {     // Previously selected might be removed from table
                itemsToSelect.add((T) ((BeanItem) getItem(activeItem)).getBean());
            }
        }

        // Automatically select first element (if non selected)
        if (automaticallySelectFirstItem) {
            if (itemsToSelect.size() == 0) {
                T firstItem = (T)firstItemId();

                if (firstItem != null) {
                    itemsToSelect.add(firstItem);
                }
            }
        }

        // Re-select appropriate object(s)
        if (itemsToSelect.size() > 0) {
            if (isMultiSelect()) {
                setValue(itemsToSelect);
            } else {
                setValue(itemsToSelect.iterator().next());
            }
        } else {
            // In case list is empty, we still need to trigger value change event
            fireValueChange(false);
        }
	}
	
	/**
	 * Select specified object. Previous selections will be de-selected.
     *
	 * @param object Object to be selected
	 */
	public void setSelectedObject(T object) {
        if (object != null) {
            setValue(null);
            select(object);
        }
	}

	/**
	 * Skjul angitte kolonner
	 * @param columns navn på kolonnene
	 */
	public void collapseColumns(String... columns) {
		for (String column : columns)
			setColumnCollapsed(column, true);
	}
	
	/**
	 * Angi format for datoer hvis du ønsker annen formatering enn {@link #DATE_TIME_FORMAT} 
	 * @param pattern formaterings-mønster
	 */
	public void setDateFormat(String pattern) {
		dateFormatter = new SimpleDateFormat(pattern);
	}

	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	/**
	 * Angi format for desimaltall hvis du ønsker annen formatering enn Utils.DECIMAL_FORMAT 
	 */
	public void setDecimalFormat(DecimalFormat decimalFormat) {
		decimalFormatter = decimalFormat;
	}

	public DecimalFormat getDecimalFormatter() {
		return decimalFormatter;
	}

	@Override
	protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
		Object value = property.getValue();

		//formaterer dato-, tall- og boolske kolonner iht. til spesielle regler
		if (value != null) {
	        Class<?> type = property.getType();
	        
			if (type == Date.class)
				return dateFormatter.format(value);
			
			if (type == Double.class  || type == Float.class)
	            return decimalFormatter.format(value);
	        
	        if (value instanceof Number)
	            return wholeNumberFormatter.format(value);
	        
	        if (value instanceof Boolean) {
	        	Boolean booleanValue = (Boolean) value;
        		return Texts.get("messytable.booleanValue." + booleanValue);
	        }
		}
		return super.formatPropertyValue(rowId, colId, property);
	}
	
	/**
	 * Bruk denne for å få beskjed hvis ENTER-tasten brukes når tabellen har fokus
	 * @param actionListener du får beskjed via denne
	 * @return en wrapper rundt tabellen som du skal legge i panelet ditt
	 */
	public Panel createShortcutKeyAwarePanelWrapper(final IMessyTableActionListener<T> actionListener) {
		Panel panel = new Panel(this); //må pakke inn tabellen for å kunne håndtere hurtigtaster
		panel.setSizeFull();
		panel.addAction(new ShortcutListener("ENTER", KeyCode.ENTER, null) {
			public void handleAction(Object sender, Object target) {
				if (!isMultiSelect()) {
					final T selectedObject = getSelectedObject();
					actionListener.doubleClick(selectedObject);
				}
			}
		});
		panel.addAction(new ShortcutListener("DELETE", KeyCode.DELETE, null) {
			public void handleAction(Object sender, Object target) {
				if (!isMultiSelect()) {
					final T selectedObject = getSelectedObject();
					actionListener.pleaseDelete(selectedObject);
				}
			}
		});
		return panel;
	}

	public String getRowCountColumn() {
		return rowCountColumn;
	}

	/**
	 * Show row count in a summary row in this column. Null if you don't want to see the row count
	 */
	public void setRowCountColumn(String rowCountColumn) {
		setFooterVisible(rowCountColumn != null);
		this.rowCountColumn = rowCountColumn;
	}

	public String getRowCountUnit() {
		return rowCountUnit;
	}

	public void setRowCountUnit(String rowCountUnit) {
		this.rowCountUnit = rowCountUnit;
	}
}
