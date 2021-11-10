package no.goodtech.vaadin.chart;

import java.util.Arrays;
import java.util.List;

import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.Theme;
import com.vaadin.addon.charts.themes.VaadinTheme;

/**
 * Provides theme colors in a sequence, and start over again when there isn't any more colors
 */
public class VaadinColorProvider {

	private final List<Color> colors; 
	private int currentColorIndex = 0;

	/**
	 * Use {@link VaadinTheme}s colors
	 */
	public VaadinColorProvider() {
		this(new VaadinTheme());
	}

	/**
	 * Use the colors of provided theme
	 * @param theme the theme you like
	 */
	public VaadinColorProvider(Theme theme) {
		colors = Arrays.asList(theme.getColors());
	}
	
	/**
	 * Use your own favourite colors
	 * @param colors the colors you like to use
	 */
    public VaadinColorProvider(Color... colors) {
    	this.colors = Arrays.asList(colors);
    }
	
	public Color getNextColor() {
		Color color = colors.get(currentColorIndex);
		
		if (currentColorIndex < colors.size() - 1)
			currentColorIndex++;
		else
			currentColorIndex = 0; //start over
		
		return color;
	}

	public String getNextColorAsHex() {
    	return getNextColor().toString();
	}
}
