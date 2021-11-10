package no.goodtech.vaadin.utils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class OkOrNotLabel extends Label {

	private final boolean ok;

	public OkOrNotLabel(boolean ok) {
		this.ok = ok;
		setContentMode(ContentMode.HTML);
		setValue(ok ? VaadinIcons.CHECK.getHtml() : VaadinIcons.BAN.getHtml());
		setStyleName((ok ? "ok" : "notOk") + " v-align-center");
	}

	public boolean isOk() {
		return ok;
	}
}
