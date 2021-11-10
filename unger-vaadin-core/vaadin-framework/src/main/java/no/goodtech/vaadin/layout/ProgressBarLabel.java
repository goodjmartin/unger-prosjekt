package no.goodtech.vaadin.layout;

import com.vaadin.v7.shared.ui.colorpicker.Color;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import no.goodtech.vaadin.utils.Utils;

/**
 * En Label med bakgrunnsfarge som vises avhengig av en gitt prosent-andel
 * Nesten som en fremdriftsindikator
 * @author oystein
 * TODO: Formatering av teksten (f.eks. nummer-formatering)
 * TODO: Styring av stiler (f.eks. farge) på framdrifts-indikatoren
 */
public class ProgressBarLabel extends Label {

	public static Color COLOR_DEFAULT = new Color(0, 204, 0);
    private double share;
    private Color color;

	public ProgressBarLabel(String content, Double currentValue, Double targetValue) {
		this(content, currentValue, targetValue, COLOR_DEFAULT);
	}

	/**
	 * Opprett og vis objektet. Automatisk formatering av verdiene
	 * @param currentValue hvor langt man har kommet
	 * @param targetValue maks-verdi (dit man skal når man er ferdig)
	 * @param delimiter currentValue <delimiter> targetValue
	 * @param color farge på bakgrunn
	 */
	public ProgressBarLabel(Double currentValue, Double targetValue, String delimiter, Color color) {
		this(Utils.DECIMAL_SHORT_FORMAT.format(currentValue) + " " + delimiter + " " + Utils.DECIMAL_SHORT_FORMAT.format(targetValue),
				currentValue, targetValue, color);
	}

	/**
	 * Opprett og vis objektet
	 * @param content teksten som skal vises
	 * @param currentValue hvor langt man har kommet
	 * @param targetValue maks-verdi (dit man skal når man er ferdig)
	 * @param color farge på bakgrunn
	 */
	public ProgressBarLabel(String content, Double currentValue, Double targetValue, Color color){
		this.color = color;
		setShare(currentValue, targetValue);

		setContentMode(ContentMode.HTML);
		setWidth("100%");
		setStyleName("progressBarLabel");
		setValue(content);
	}

	public void setShare(Double currentValue, Double targetValue) {
		if (targetValue == null || targetValue == 0.0 || currentValue == null){
			share = 0;
		}else if(currentValue > targetValue) {
			share = 100;
		}else {
			share = (currentValue * 100) / targetValue;
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getShare() {
		return share;
	}
	
	@Override
	public void setValue(String content) {
		String transparentColor = "rgba(0, 0, 255, 0)";
		String colorStr = (color == null) ? COLOR_DEFAULT.getCSS() : color.getCSS();

		StringBuilder progress = new StringBuilder("background: linear-gradient(to right, "+colorStr + " " + share + "%, " + transparentColor + " " + share + "%);");
		progress.append("background: -o-linear-gradient(left, " + colorStr + " " +share + "%, " + transparentColor + " " + share + "%);");
		progress.append("background: -moz-linear-gradient(left center, " + colorStr + " " + share + "%, " + transparentColor + " " +share + "%);");
		progress.append("-webkit-gradient(linear, left top, right top, color-stop(" + share + "%,"+colorStr+ "), color-stop(" + share + "%," + transparentColor + "));");
		progress.append("border: 1px solid " + colorStr + ";");
		super.setValue("<div style='" + progress + "'>" + content + "</div></div>");
	}
}
