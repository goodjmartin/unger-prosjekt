package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.OptionGroup;
import no.goodtech.vaadin.tabs.status.model.StatusIndicator;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorFinder;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorStub;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * OptionGroup med checkboxer for hvilke status-indikatorer abonnementet skal bindes til
 */
public class StatusIndicatorOptionGroup extends OptionGroup {

	//Abonnementet
    private volatile StatusIndicatorSubscription newOrSelectedStatusIndicatorSubscription;
    //Mulige status-indikatorer
	private volatile List<StatusIndicator> availableStatusIndicators;

    private volatile Boolean checkBoxesInitialized = false;

    public StatusIndicatorOptionGroup(final ISubscriptionAndIndicatorActionListener subscriptionAndIndicatorActionListener) {
        super("Status-indikatorer");
        addStyleName("accessRoles");
        setMultiSelect(true);
        setNullSelectionAllowed(true);
        setImmediate(true);
        addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (checkBoxesInitialized) {
                    newOrSelectedStatusIndicatorSubscription.setStatusIndicators(getSelectedStatusIndicators());
					subscriptionAndIndicatorActionListener.statusIndicatorsModified(getSelectedStatusIndicators());
                }
            }
        });
    }

    /**
     * This method will return the set of selected access roles.
     *
     * @return The set of selected access roles
     */
    private Set<StatusIndicator> getSelectedStatusIndicators() {
        Set<StatusIndicator> selectedAccessRoles = new HashSet<StatusIndicator>();

        for (StatusIndicator statusIndicator : availableStatusIndicators) {
            if (isSelected(statusIndicator.getId())) {
                selectedAccessRoles.add(statusIndicator);
            }
        }
      return selectedAccessRoles;
    }

    public void refresh(final StatusIndicatorSubscription statusIndicatorSubscription) {
        // Keep the selected (or new) access role
        this.newOrSelectedStatusIndicatorSubscription = statusIndicatorSubscription;

        // Disable checkBox.valueChange() method
        checkBoxesInitialized = false;

        // Re-populate the access role list (in case it has changed)
        removeAllItems();
		StatusIndicatorFinder statusIndicatorFinder = new StatusIndicatorFinder();
		statusIndicatorFinder.setOrderById(true);
        availableStatusIndicators = statusIndicatorFinder.loadList();
        if (availableStatusIndicators != null) {
            for (StatusIndicator statusIndicator : availableStatusIndicators) {
                addItem(statusIndicator.getId());
            }
        }

        // Already assigned access roles should be marked as such
        for (StatusIndicatorStub statusIndicator : statusIndicatorSubscription.getStatusIndicators()) {
            select(statusIndicator.getId());
        }

        // Enable checkBox.valueChange() method
        checkBoxesInitialized = true;
    }
}
