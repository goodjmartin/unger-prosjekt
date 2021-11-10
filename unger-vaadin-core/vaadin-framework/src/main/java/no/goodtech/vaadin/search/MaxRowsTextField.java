package no.goodtech.vaadin.search;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.TextField;
import no.goodtech.vaadin.ui.Texts;

/**
 * Tekstfelt for å styre maks antall rader for et søk
 */
public class MaxRowsTextField extends TextField {

	private static final String STYLE_LIMIT_REACHED = "limitReached";
	public static final int MAX_ROWS_DEFAULT = 30;

	/**
	 * Maks antall rader kan ikke være større enn dette
	 */
    private int upperLimit = 10000;

    private Integer maxRows;

    /**
     * Oppretter tekstfelt med maks antall
     */
    public MaxRowsTextField() {
        this(MAX_ROWS_DEFAULT);
    }

    /**
     * Oppretter tekstfelt med angitt maks antall
     * @param maxRows maks antall rader; må være mindre enn {@link #upperLimit}
     */
    public MaxRowsTextField(final Integer maxRows) {
        addStyleName("maxRowsTextField");

        setWidth(80, Unit.PIXELS);
        if (maxRows == null)
        	this.maxRows = MAX_ROWS_DEFAULT;
        else
        	this.maxRows = maxRows;
        setMaxRows(this.maxRows);


		setValue(this.maxRows.toString());
		addValueChangeListener(event -> {
			final String value = event.getValue();
			if (value == null || value.trim().length() == 0) {
				setMaxRows(upperLimit);
			} else {
				try {
					Integer enteredValue = Integer.parseInt(value);
					if (enteredValue > upperLimit) {
						setValue(Integer.valueOf(upperLimit).toString());
					} else {
						setMaxRows(enteredValue);
					}
				} catch (NumberFormatException e) {
					setValue(getMaxRows().toString());   // Set input felt til gammel verdi
				} catch (ClassCastException ce) {
					setValue(getMaxRows().toString());   // Set input felt til gammel verdi
				}
			}
		});
    }

    /**
     * @return maks antall rader som er valgt, null om ikke spesifisert
     */
	public Integer getMaxRows() {
		return maxRows;
	}

	private void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}

	/**
	 * Angi maks grense for maxRows. Denne er standard satt til 10000.
	 * @param upperLimit maks grense. null => Integer.MAX_VALUE
	 */
    public void setUpperLimit(Integer upperLimit)
    {
    	if (upperLimit == null)
    		this.upperLimit = Integer.MAX_VALUE;
    	else
    		this.upperLimit = upperLimit;
    }

    /**
     * @return upper limit for row count in a search
     */
	public int getUpperLimit() {
		return upperLimit;
	}

	public void refresh(Integer count) {
		if (count != null && maxRows != null && count > maxRows) {
			setIcon(FontAwesome.EXCLAMATION_CIRCLE);
			if (count >= upperLimit) {
				setDescription(Texts.get("filterPanel.maxRows.description.upperLimitReached"), ContentMode.HTML);
			} else {
				setDescription(Texts.get("filterPanel.maxRows.description.maxRowsReached"), ContentMode.HTML);
			}
			addStyleName(STYLE_LIMIT_REACHED);
		} else {
			setIcon(null);
			removeStyleName(STYLE_LIMIT_REACHED);
			setDescription(null);
		}
	}
}
