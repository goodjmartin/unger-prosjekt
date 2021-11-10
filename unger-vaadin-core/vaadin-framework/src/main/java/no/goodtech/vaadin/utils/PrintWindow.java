package no.goodtech.vaadin.utils;

import no.goodtech.vaadin.main.InputBox;
import no.goodtech.vaadin.ui.Texts;

import javax.print.PrintService;

/**
 * A generic popup for selecting printer, choose num copies and confirm print
 */
public class PrintWindow extends InputBox<PrintForm.Response> {

	private final ActionListener listener;

	public PrintWindow(String defaultPrinterName, Integer defaultNumCopies, final ActionListener listener) {
		super(new PrintForm(defaultPrinterName, defaultNumCopies), Texts.get("printWindow.button.print.caption"),
				Texts.get("printWindow.button.cancel.caption"), Texts.get("printWindow.caption"),
				new IConfirmListener<PrintForm.Response>() {
			@Override
			public void onConfirm(PrintForm.Response response) {
				if (listener != null) {
					listener.print(response.getPrinter(), response.getNumCopies());
				}
			}
		});
		this.listener = listener;
	}

	public interface ActionListener {
		void print(PrintService printer, int numCopies);
	}
}
