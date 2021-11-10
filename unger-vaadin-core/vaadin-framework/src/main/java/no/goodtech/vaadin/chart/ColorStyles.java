package no.goodtech.vaadin.chart;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.server.Page;

public class ColorStyles {
	
	private Set<String> styles = new HashSet<String>();

	/**
	 * Add a color for an icon
	 */
	public void addIconColorStyle(String stylename, Integer rgb) {
		if (!styles.contains(stylename)) {
			styles.add(stylename);
			String hexColor = String.format("#%06X", (0xFFFFFF & rgb));
			String css = "." + stylename + " { color: " + hexColor + " !important;}";
			Page.Styles styles = Page.getCurrent().getStyles();
			styles.add(css);
		}
	}
}
