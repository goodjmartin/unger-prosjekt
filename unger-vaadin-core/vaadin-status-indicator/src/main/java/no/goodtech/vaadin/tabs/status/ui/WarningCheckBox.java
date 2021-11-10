package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.ui.CheckBox;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.status.common.StateType;

public class WarningCheckBox extends CheckBox {

    public WarningCheckBox() {
        super(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("status.checkBox.warning"));

        addStyleName("status.checkBox.warning");
        setIcon(StateTypeIcon.getIcon(StateType.WARNING));
        setValue(true);
    }
}
