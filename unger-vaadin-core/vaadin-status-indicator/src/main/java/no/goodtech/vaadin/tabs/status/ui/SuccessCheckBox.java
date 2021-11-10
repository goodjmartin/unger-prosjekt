package no.goodtech.vaadin.tabs.status.ui;

import com.vaadin.ui.CheckBox;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.tabs.status.common.StateType;

public class SuccessCheckBox extends CheckBox {

    public SuccessCheckBox() {
        super(ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("status.checkBox.success"));

        addStyleName("status.checkBox.success");
        setIcon(StateTypeIcon.getIcon(StateType.SUCCESS));
        setValue(true);
    }
}
