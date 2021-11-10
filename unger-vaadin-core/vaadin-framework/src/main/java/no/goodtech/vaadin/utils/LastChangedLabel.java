package no.goodtech.vaadin.utils;

import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;

import java.util.Date;


/**
 * Label containing "Last changed YYYY-MM-dd HH:mm by XXX"
 */
public class LastChangedLabel extends Label {
	
	public LastChangedLabel() {
		setContentMode(ContentMode.HTML);
	}
	
	public void refresh(Date date, String username) {
		StringBuilder content = new StringBuilder("<html>");
		if (date != null) {
			content.append(Utils.getText("lastChangedLabel.prefix"));
			content.append(" ");
			content.append(Utils.DATETIME_FORMATTER.format(date));
			if (username != null) {
				content.append(" ");
				content.append(Utils.getText("lastChangedLabel.by"));
				content.append(" ");
				content.append(username);
			}
		}
		content.append("</html>");
		setValue(content.toString());
	}
}
