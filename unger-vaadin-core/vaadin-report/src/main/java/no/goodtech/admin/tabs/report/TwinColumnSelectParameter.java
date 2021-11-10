package no.goodtech.admin.tabs.report;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.TwinColSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;

public class TwinColumnSelectParameter extends TwinColSelect implements IReportParameterComponent {

	private static Logger logger = LoggerFactory.getLogger(TwinColumnSelectParameter.class);

	private Object parameterValue = null;

	public TwinColumnSelectParameter(final String caption, final Map<?, ?> selectionEntries) {

		// The selection entries may never be null
		assert selectionEntries != null;

		// Add a style name
		addStyleName("twinColumnSelectParameter");

		// Set the caption
		setCaption(caption);

		// Set the selection values
		for (Entry<?, ?> entry : selectionEntries.entrySet()) {
			final Object item = entry.getValue();
			addItem(item);
			final Object itemCaption = entry.getKey();
			if (itemCaption == null)
				setItemCaption(item, "");
			else
				setItemCaption(item, itemCaption.toString());
		}

	  // Add ValueChangeListener
		addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				//noinspection SuspiciousMethodCalls
				parameterValue = event.getProperty().getValue();
				logger.debug(parameterValue + " er valgt");
			}
		});

		// Set immediate rendering
		setImmediate(true);
	}

	@Override
	public String getValueAsString() {
		if (parameterValue == null)
        {
            return null;
        }
        else
        {
            String parameter = parameterValue.toString();
            parameter = parameter.replace("[","");
            parameter = parameter.replace("]","");
            return parameter;
        }
	}

	public void setValueAsString(String value) {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
