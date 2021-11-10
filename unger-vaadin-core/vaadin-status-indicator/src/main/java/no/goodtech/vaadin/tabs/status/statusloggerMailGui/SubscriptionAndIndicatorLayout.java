package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.OptionGroup;
import no.goodtech.vaadin.buttons.SaveButton;
import no.goodtech.vaadin.tabs.status.model.StatusIndicator;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;

import java.util.Set;

//import com.vaadin.v7.ui.themes.Reindeer;

public class SubscriptionAndIndicatorLayout extends VerticalLayout implements ISubscriptionAndIndicatorActionListener {
    private final StatusIndicatorOptionGroup statusIndicatorOptionGroupPanel;
	private final SubscriptionDetailsPanel subscriptionOptionsVerticalLayout;

	private volatile StatusIndicatorSubscription subscription;

	/**
	 * VerticalLayout som inneholder overordnede layout-komponenter for status-indikatorer og abonnement verdier
	 * @param mailPanelActionListener listener for å kommunisere opp endringer av verdier på abonnementet
	 */
    public SubscriptionAndIndicatorLayout(final IMailPanelActionListener mailPanelActionListener) {
		subscriptionOptionsVerticalLayout = new SubscriptionDetailsPanel(this);
		statusIndicatorOptionGroupPanel = new StatusIndicatorOptionGroup(this);

		//Layout for status-indikatorer og abonnement
		HorizontalLayout subscriptionAndIndicatorPanels = new HorizontalLayout();

		//Panel som inneholder optionGroup over status-indikatorer
		Panel statusIndicatorOptionGroupContainer = new Panel();
		statusIndicatorOptionGroupContainer.setContent(statusIndicatorOptionGroupPanel);

		//legg status-indikator panelet og abonnement layout til subscriptionAndIndicatorPanels
		subscriptionAndIndicatorPanels.addComponent(subscriptionOptionsVerticalLayout);
		subscriptionAndIndicatorPanels.addComponent(statusIndicatorOptionGroupContainer);

		//Opprett overskrift for de to panelene
		Label headerLabel = new Label("Abonnement");
		//headerLabel.addStyleName(Reindeer.LABEL_H2);
		headerLabel.addStyleName(ValoTheme.LABEL_H2);

		setMargin(false);
		//Setsizeundefined for å tillate horisontal scrollbat
		statusIndicatorOptionGroupPanel.setSizeUndefined();

		//Opprett lagreknapp for abonnementet og bind det til listener
		SaveButton saveButton = new SaveButton(new SaveButton.ISaveListener() {
			@Override
			public void saveClicked() {
				//Kommuniser opp at abonnementet skal lagres
				mailPanelActionListener.saveButtonClicked(subscription);
			}
		});
		subscriptionAndIndicatorPanels.setSizeFull();

		//Oprett et VerticalLayout i dette VerticalLayout for å muliggjøre skille-linje over lagre-knappen
		VerticalLayout panelsLayout = new VerticalLayout();
		panelsLayout.addComponent(headerLabel);
		panelsLayout.addComponent(subscriptionAndIndicatorPanels);
		panelsLayout.setSpacing(false);
		//Legg til skillelinje under komponenten
		panelsLayout.addStyleName("borderBottom");

		addComponent(panelsLayout);
		//Legg til lagre-knappen under skille-linjen
		addComponent(saveButton);

		setWidth(100, Unit.PERCENTAGE);
	}

	/**
	 * Oppfrisk komponentene for abonnementet med ny data
	 * @param subscription abonnementet
	 */
    public void refreshStatusIndicatorSubscriptionPanel(StatusIndicatorSubscription subscription) {
		this.subscription = subscription;
        statusIndicatorOptionGroupPanel.refresh(subscription);
		subscriptionOptionsVerticalLayout.refreshDetailsPanel(subscription);
	}

	/**
	 * Oppdater abonnementet med det nye valget av statusindikatorer
	 *
	 * @param selectedStatusIndicators Valgte statusindikatorer
	 */
	@Override
	public void statusIndicatorsModified(Set<StatusIndicator> selectedStatusIndicators) {
		subscription.setStatusIndicators(selectedStatusIndicators);
	}

	/**
	 * Oppdater abonnementet med det nye valget av Cronverdi
	 *
	 * @param intervalGroup de mulige valgene av cronverdi
	 */
	@Override
	public void cronExpressionModified(OptionGroup intervalGroup) {
		if (intervalGroup.getValue() != null) {
			if (intervalGroup.getValue().equals("Time")) {
				subscription.setCronExpression("0 0 * * * *");
			} else if (intervalGroup.getValue().equals("Dag")) {
				subscription.setCronExpression("0 0 7 * * *");
			} else if (intervalGroup.getValue().equals("Uke")) {
				subscription.setCronExpression("0 0 7 * * MON");
			}
		}
	}

	/**
	 * Legg inn en ny e-post til listen over mottakere på abonnementet
	 *
	 * @param mail den nye e-posten
	 */
	@Override
	public void emailRecipientAdded(String mail) {
		if (!subscription.getEmailRecipients().contains(mail)) {
			subscription.addEmailRecipient(mail);
		}
	}

	/**
	 * Oppdater hendelse-intervallet med ny verdi
	 */
	@Override
	public void maxLogEntryAgeModified(long maxLogEntryAge) {
		subscription.setMaxLogEntryAge(maxLogEntryAge);
	}

	/**
	 * Oppdater abonnementet med ny beskrivelse
	 *
	 * @param description beskrivelsen
	 */
	@Override
	public void descriptionChanged(String description) {
		subscription.setDescription(description);
	}

	/**
	 * Oppdater abonnementet med ny ID
	 *
	 * @param id den nye ID'en
	 */
	@Override
	public void idChanged(String id) {
		subscription.setId(id);
	}

	/**
	 * Fjern en e-post adresse fra listen over e-post adresser
	 *
	 * @param mail e-post adressen
	 */
	@Override
	public void emailRecipientRemoved(String mail) {
		subscription.removeEmailRecipient(mail);
	}

}
