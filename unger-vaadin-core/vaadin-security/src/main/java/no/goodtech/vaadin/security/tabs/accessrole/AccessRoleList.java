package no.goodtech.vaadin.security.tabs.accessrole;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;

/**
 * Date: 4/23/11
 *
 * @author bakke
 */
public class AccessRoleList extends VerticalLayout {

    private final Table table = new Table();
    private volatile String selectedAccessRoleId = null;
    private volatile AccessRoleButtonPanel buttonPanel;

    public AccessRoleList(final IAccessRoleActionListener actionListener) {
		setSpacing(false);
		setSizeFull();
		table.setSizeFull();
		addComponent(table);

		// Create the button panel
		buttonPanel = new AccessRoleButtonPanel(actionListener);
		addComponent(buttonPanel);

		// Set a style name (so we can style rows and cells)
		table.addStyleName("accessRoleTable");

		// Enable row selection (single row)
		table.setSelectable(true);
		table.setMultiSelect(false);
		table.setImmediate(true);

		// Populate the role list
		table.setContainerDataSource(AccessRoleTable.getContainer());

		// Turn off column reordering
		table.setColumnReorderingAllowed(false);

		// Column alignment
		table.setColumnAlignment(AccessRoleTable.PROPERTY_ID, Table.Align.LEFT);
		table.setColumnAlignment(AccessRoleTable.PROPERTY_DESCRIPTION, Table.Align.LEFT);

		// Add a listener to handle row selection events
		table.addValueChangeListener(new Table.ValueChangeListener() {
			public void valueChange(Property.ValueChangeEvent event) {
				selectedAccessRoleId = (String) event.getProperty().getValue();

				// Enable and disable the Remove button and details panel as appropriate
				if (selectedAccessRoleId != null) {
					buttonPanel.enableRemoveButton(true);
				} else {
					buttonPanel.enableRemoveButton(false);
				}

                // Notify the listener
                actionListener.accessRoleSelected(selectedAccessRoleId);
			}
		});

        setExpandRatio(table, 1.0f);
    }

    public void refresh(final boolean accessRoleIsSelected) {
        table.setContainerDataSource(AccessRoleTable.getContainer());
		buttonPanel.refresh(accessRoleIsSelected);
    }

}
