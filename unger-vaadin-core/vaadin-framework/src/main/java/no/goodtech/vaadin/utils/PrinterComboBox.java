package no.goodtech.vaadin.utils;

import no.goodtech.vaadin.lists.v7.MessyComboBox;
import no.goodtech.vaadin.ui.Texts;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Arrays;

/**
 * Shows available printers on current machine
 */
public class PrinterComboBox extends MessyComboBox<PrintService> {

	public PrinterComboBox(String caption) {
		super(caption);
		setNullSelectionAllowed(false);
		setRequired(true);
		setRequiredError(Texts.get("printerComboBox.requiredError"));
	}

	public void refresh() {
		refresh(Arrays.asList(PrintServiceLookup.lookupPrintServices(null, null)));
		if (getValue() == null) {
			//choose default printer if no printer chosen
			final PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
			if (defaultPrintService != null)
				select(defaultPrintService.getName());
		}
	}

	@Override
	protected String getId(PrintService item) {
		return item.getName();
	}

	@Override
	protected String getName(PrintService item) {
		return item.getName();
	}
}
