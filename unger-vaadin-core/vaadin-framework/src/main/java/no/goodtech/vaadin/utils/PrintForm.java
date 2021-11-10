package no.goodtech.vaadin.utils;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.IntegerRangeValidator;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.main.InputBox;
import no.goodtech.vaadin.ui.Texts;

import javax.print.PrintService;

/**
 * Generic form for printing of labels, where the operator can choose printer and num copies
 */
public class PrintForm extends VerticalLayout implements InputBox.ResponsiveContent<PrintForm.Response>{
	private final PrinterComboBox printerComboBox = new PrinterComboBox(Texts.get("printForm.printer.caption"));
	private final TextField numLabelsTextField = createTextField(Texts.get("printForm.numLabels.caption"));

	public interface Response {
		PrintService getPrinter();
		int getNumCopies();
	}

	public PrintForm(String defaultPrinterName, Integer defaultNumCopies) {
		numLabelsTextField.setConverter(Integer.class);
		numLabelsTextField.addValidator(new IntegerRangeValidator(Texts.get("printForm.numLabels.validationError"), 1, 100));
		numLabelsTextField.setRequired(true);
		if (defaultNumCopies != null)
			numLabelsTextField.setConvertedValue(defaultNumCopies);
		else
			numLabelsTextField.setConvertedValue(1);

		printerComboBox.refresh();

		if (defaultPrinterName != null)
			printerComboBox.select(defaultPrinterName);

		setSizeFull();
		setMargin(false);
		addComponents(printerComboBox, numLabelsTextField);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Response getResponse() {
		return new Response() {
			@Override
			public PrintService getPrinter() {
				return printerComboBox.getValue();
			}

			@Override
			public int getNumCopies() {
				return (int) numLabelsTextField.getConvertedValue();
			}
		};
	}

	public boolean commit() {
		try {
			numLabelsTextField.validate();
			printerComboBox.validate();
			return true;
		} catch (Validator.InvalidValueException e) {
			Notification.show(Texts.get("printForm.validationError"), Notification.Type.WARNING_MESSAGE);
		}
		return false;
	}

	private TextField createTextField(String caption){
		TextField field = new TextField(caption);
		field.setSizeFull();
		field.setNullRepresentation("");
		field.setImmediate(true);
		return field;
	}
}
