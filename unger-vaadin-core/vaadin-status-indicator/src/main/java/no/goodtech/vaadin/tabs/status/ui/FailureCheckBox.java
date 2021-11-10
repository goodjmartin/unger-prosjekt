package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.ui.CheckBox;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.status.common.StateType;

public class FailureCheckBox extends CheckBox {

    public FailureCheckBox() {
        super(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("status.checkBox.failure"));

        addStyleName("status.checkBox.failure");
        setIcon(StateTypeIcon.getIcon(StateType.FAILURE));
        setValue(true);
    }
}
