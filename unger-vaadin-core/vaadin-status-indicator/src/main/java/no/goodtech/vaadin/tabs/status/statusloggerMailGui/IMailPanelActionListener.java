package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;

public interface IMailPanelActionListener extends AddButton.IAddListener, RemoveButton.IRemoveListener {

	/**
	 * Oppfrisk paneler for status-indikator og abonnementer med ny data
	 * @param subscription et abonnement på statusindikatorer
	 */
    public void subscriptionSelected(final StatusIndicatorSubscription subscription);

	/**
	 * Lagre endringer gjort på valgt abonnement og oppfrisk abonnement-tabellen og panelene for status-indikator og
	 * abonnementer med ny data
	 */
    public void saveButtonClicked(final StatusIndicatorSubscription subscription);

}
