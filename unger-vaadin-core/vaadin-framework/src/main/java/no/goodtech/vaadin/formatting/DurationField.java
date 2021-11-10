package no.goodtech.vaadin.formatting;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.main.ApplicationResourceBundle;

import javax.management.timer.Timer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A collection of textfields to represent a duration in milliseconds converted to for example days, hours and minutes
 */
public class DurationField extends CustomField<Long> {

	private static Map<Resolution, Long> FACTORS = new HashMap<DurationField.Resolution, Long>();
	
	static {
		FACTORS.put(Resolution.MILLIS, 1L);
		FACTORS.put(Resolution.SEC, Timer.ONE_SECOND);
		FACTORS.put(Resolution.MIN, Timer.ONE_MINUTE);
		FACTORS.put(Resolution.HOUR, Timer.ONE_HOUR);
		FACTORS.put(Resolution.DAY, Timer.ONE_DAY);
		FACTORS.put(Resolution.WEEK, Timer.ONE_WEEK);
		FACTORS.put(Resolution.MONTH, 30 * Timer.ONE_DAY);
		FACTORS.put(Resolution.YEAR, 365 * Timer.ONE_DAY);
	}
	
	public enum Resolution {
		YEAR, MONTH, WEEK, DAY, HOUR, MIN, SEC, MILLIS
	}
	
	private final Map<Resolution, TextField> fields;
	
	/**
	 * Create field with the specified resolution
	 * @param caption header
	 * @param resolutions e.g. Resolution.HOUR, Resolution.MIN if you like  
	 */
	public DurationField(String caption, Resolution... resolutions) {
		setCaption(caption);
		fields = new LinkedHashMap<Resolution, TextField>();
		
		for (Resolution resolution : resolutions)
			createTextField(resolution);
	}
	
	private TextField createTextField(Resolution resolution) {
		TextField field = new TextField();
		field.setInputPrompt(ApplicationResourceBundle.getInstance("vaadin-core").getString("durationField.inputPrompt." + resolution));
		field.setConverter(Integer.class);
		field.setWidth("100%");
		field.setNullRepresentation("");
		field.setImmediate(true);
		fields.put(resolution, field);
		return field;
	}

	protected Component initContent() {
		HorizontalLayout content = new HorizontalLayout();
		content.setMargin(false);
		content.setWidth("100%");
		content.addComponents(fields.values().toArray(new Component[1]));
		return content;
	}

	public Class<? extends Long> getType() {
		return Long.class;
	}
	
	protected void setInternalValue(Long newValue) {
		super.setInternalValue(newValue);
		Long rest = newValue;
		for (Resolution resolution : Resolution.values()) {
			final TextField field = fields.get(resolution);
			if (field != null) {
				final Long factor = FACTORS.get(resolution);
				if (rest == null || rest == 0L) {
					field.setValue(null);
				} else {
					final long value = rest / factor;
					if (value > 0) {
						field.setValue(String.valueOf(value));
						rest = rest % factor;
					} else {
						field.setValue(null);
					}					
				}
			}
		}
	}
	
	protected Long getInternalValue() {
		long sum = 0L;
		for (Map.Entry<Resolution, TextField> entry : fields.entrySet()) {
			final Resolution resolution = entry.getKey();
			final TextField field = entry.getValue();
			String stringValue = field.getValue();
			if (stringValue != null && stringValue.trim().length() > 0) {
				long value = Long.valueOf(stringValue);
				sum += value * FACTORS.get(resolution);
			}
		}
		if (sum == 0L)
			return null;
		return sum;
	}

	/**
	 * For unit testing
	 */
	Map<Resolution, TextField> getFields() {
		return fields;
	}
}
