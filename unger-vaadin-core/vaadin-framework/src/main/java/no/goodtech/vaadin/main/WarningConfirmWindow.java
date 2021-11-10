package no.goodtech.vaadin.main;

import com.vaadin.icons.VaadinIcons;

public class WarningConfirmWindow extends ConfirmWindow {
	public WarningConfirmWindow(String text, IConfirmWindowListener confirmWindowListener) {
		super(text, confirmWindowListener);
		defineWarningWindow();
	}

	public WarningConfirmWindow(String text, String okButtonText, IConfirmWindowListener confirmWindowListener) {
		super(text, okButtonText, confirmWindowListener);
		defineWarningWindow();
	}

	public void defineWarningWindow(){
		setIcon(VaadinIcons.WARNING);
		setMessageStyle("red");
	}
}
