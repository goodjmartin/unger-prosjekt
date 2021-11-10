package no.goodtech.vaadin.chart;

import com.vaadin.addon.charts.model.style.SolidColor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ColorUtils {

	public static final String HEX_COLOR_PATTERN = "^#(([0-9a-fA-F]{2}){3}|([0-9a-fA-F]){3})$";

	/**
	 * Create a color from given color name or hex rgb value
	 * @param colorConstantNameOrHex RGB HEX value of color or name of one of the predefined CSS colors (https://www.w3schools.com/cssref/css_colors.asp)
	 * @see SolidColor
	 * @return the color or null if given color name is null or no match found
	 */
	public static com.vaadin.addon.charts.model.style.SolidColor createColor(String colorConstantNameOrHex) {
		if (colorConstantNameOrHex == null) {
			return null;
		}
		if (validateHexColor(colorConstantNameOrHex)) {
			return new SolidColor(colorConstantNameOrHex);
		}

		Field[] declaredFields = SolidColor.class.getDeclaredFields();
		for (Field field : declaredFields) {
			if (Modifier.isStatic(field.getModifiers())) {
				if (field.getName().equals(colorConstantNameOrHex.toUpperCase())) {
					try {
						return (SolidColor) field.get(null);
					} catch (IllegalAccessException e) {
					}
				}
			}
		}
		return null;
	}

	private static boolean validateHexColor(String colorConstantNameOrHex) {
		return colorConstantNameOrHex != null && colorConstantNameOrHex.startsWith("#") && colorConstantNameOrHex.matches(HEX_COLOR_PATTERN);
	}

	/**
	 * @param hexColor e.g. "#FFFFFF"
	 * @return rgb color array
	 */
	public static int[] hex2Rgb(String hexColor) {
		if (validateHexColor(hexColor)) {
			int  r =  Integer.valueOf( hexColor.substring( 1, 3 ), 16 );
			int  g =  Integer.valueOf( hexColor.substring( 3, 5 ), 16 );
			int  b =  Integer.valueOf( hexColor.substring( 5, 7 ), 16 );
			return new int[] {r, g, b};
		}
		return new int[] {255, 255, 255};
	}

	public static com.vaadin.shared.ui.colorpicker.Color hexToColorPickerColor(String hex) {
		final int[] rgb = hex2Rgb(hex);
		return new com.vaadin.shared.ui.colorpicker.Color(rgb[0], rgb[1], rgb[2]);
	}
}
