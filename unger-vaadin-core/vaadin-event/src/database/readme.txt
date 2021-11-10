Kopi av MaterialClassPanel jeg skrev om for å teste hendelser ligger nedenfor, mes-material-api trenger også
en dependency på mes-event for å kunne testes.

Tabellene jeg opprettet ligger i tables.sql i mes-attachment -> database
------------------------------------------------------------------------------------------------------------------------
package no.goodtech.mes.material.ui.materialClass;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import no.goodtech.mes.event.ui.EventPanel;
import no.goodtech.mes.material.MaterialClass;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.main.ITopPanelModifier;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IAdminTab;
import no.goodtech.vaadin.tabs.ILayout;

import java.util.Map;

/**
 * Dette panelet er hovedpanelet for varegrupper
 */
public class MaterialClassPanel extends HorizontalLayout implements IMaterialClassActionListener, IAdminTab {

    public static final String MATERIAL_CLASS_VIEW = "materialClassView";
    public static final String MATERIAL_CLASS_UPDATE = "materialClassUpdate";

    private final MaterialClassListPanel materialClassListPanel;
    private final EventPanel attachmentPanel;
    private volatile MaterialClass selectedMaterialClass;

    static {
        AccessFunctionManager.registerAccessFunction(new AccessFunction(MATERIAL_CLASS_VIEW, ApplicationResourceBundle.getInstance("mes-material").getString("accessFunction.materialClass.materialClassView")));
        AccessFunctionManager.registerAccessFunction(new AccessFunction(MATERIAL_CLASS_UPDATE, ApplicationResourceBundle.getInstance("mes-material").getString("accessFunction.materialClass.materialClassUpdate")));
    }

	public MaterialClassPanel() {
        setSizeFull();

        final HorizontalSplitPanel horizontal = new HorizontalSplitPanel();
        horizontal.setSplitPosition(60);
        addComponent(horizontal);

        //Opprett liste-panel
        materialClassListPanel = new MaterialClassListPanel(this);

        //Opprett detaljpanel
        attachmentPanel = new EventPanel();
        attachmentPanel.setVisible(false);

        //Legg paneler til hovedpanel
        horizontal.addComponent(materialClassListPanel);
        horizontal.addComponent(attachmentPanel);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void materialClassSelected(final MaterialClass selectedMaterialClass) {
        //Hold på valgte objekt
        this.selectedMaterialClass = selectedMaterialClass;
        //Oppdater detaljpanel
        if (selectedMaterialClass != null) {
            attachmentPanel.setVisible(true);
        } else {
            attachmentPanel.setVisible(false);
        }
        attachmentPanel.ownerEntitySelected(selectedMaterialClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void materialClassModified() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addClicked() {
        selectedMaterialClass = new MaterialClass();
        attachmentPanel.setVisible(true);
        attachmentPanel.ownerEntitySelected(selectedMaterialClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeClicked() {
        if (selectedMaterialClass != null && selectedMaterialClass.delete()) {
			Notification.show(ApplicationResourceBundle.getInstance("mes-material").getString("materialClass.notification.remove.success") + " " + selectedMaterialClass.getId());
		} else {
			Notification.show(ApplicationResourceBundle.getInstance("mes-material").getString("materialClass.notification.remove.failure") + " " + selectedMaterialClass.getId(), Notification.Type.WARNING_MESSAGE);
		}
		materialClassListPanel.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveClicked() {
        MaterialClass selectedMaterialClassCopy =  selectedMaterialClass;
        if (selectedMaterialClass != null) {
			if (selectedMaterialClass.save() != null) {
				Notification.show(ApplicationResourceBundle.getInstance("mes-material").getString("materialClass.notification.modify.success") + " " + selectedMaterialClass.getId());
			} else {
				Notification.show(ApplicationResourceBundle.getInstance("mes-material").getString("materialClass.notification.modify.failure") + " " + selectedMaterialClass.getId(), Notification.Type.WARNING_MESSAGE);
			}
        }
        materialClassListPanel.refresh();
        materialClassListPanel.setSelectedRow(selectedMaterialClassCopy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshClicked() {
        materialClassListPanel.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorized(final User user, final String value) {
        return AccessFunctionManager.isAuthorized(user, MATERIAL_CLASS_VIEW);
    }

    /**
	 * {@inheritDoc}
     */
	@Override
    public void initTab(final ITopPanelModifier topPanelModifier, final Map<String, String[]> queryParameters, final User user, final ILayout layout) {
        materialClassListPanel.init(user);
    }

	/**
     * {@inheritDoc}
	 */
	@Override
	public void refreshTab() {
		materialClassListPanel.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tabDeselected() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTabName() {
		return ApplicationResourceBundle.getInstance("mes-material").getString("adminTabSheet." + getClass().getName());
	}
}
