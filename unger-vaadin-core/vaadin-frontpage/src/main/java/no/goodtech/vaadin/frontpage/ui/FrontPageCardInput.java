package no.goodtech.vaadin.frontpage.ui;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Remember to set id to the components as it determines which parameter it is attached to.
 * @see no.goodtech.vaadin.frontpage.model.IFrontPageCardComponent
 */
public class FrontPageCardInput extends HorizontalLayout {
	public FrontPageCardInput() {
	}

	public Map<String, String> getArguments(){
		Map<String, String> args = new HashMap<>();
		for (Component component : this) {
			String parameter = component.getId();
			if (component instanceof AbstractField) {
				String value = String.valueOf(((AbstractField) component).getValue());
				args.put(parameter, value);
			}else if (component instanceof AbstractSingleSelect){
				String value = String.valueOf(((AbstractSingleSelect) component).getValue());
				args.put(parameter, value);
			}
		}
		return args;
	}
}
