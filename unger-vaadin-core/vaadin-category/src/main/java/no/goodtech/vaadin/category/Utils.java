package no.goodtech.vaadin.category;

import com.vaadin.server.Page;

/**
 * Utils for UI
 */
public class Utils {

	public static void addIconColorStyle(Category status) {
		if (status != null && status.getColor() != null) {
			Page.Styles styles = Page.getCurrent().getStyles();

			String hexColor = String.format("#%06X", (0xFFFFFF & status.getColor()));
			String css = "." + status.getStyleName() + " { color: " + hexColor + " !important;}";
			styles.add(css);
		}
	}
}
