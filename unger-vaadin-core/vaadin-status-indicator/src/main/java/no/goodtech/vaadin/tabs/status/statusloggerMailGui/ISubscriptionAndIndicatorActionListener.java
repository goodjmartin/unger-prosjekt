package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.v7.ui.OptionGroup;
import no.goodtech.vaadin.tabs.status.model.StatusIndicator;

import java.util.Set;

public interface ISubscriptionAndIndicatorActionListener {

	/**
	 * Oppdater abonnementet med det nye valget av statusindikatorer
	 * @param selectedStatusIndicators Valgte statusindikatorer
	 */
	public void statusIndicatorsModified(Set<StatusIndicator> selectedStatusIndicators);

	/**
	 * Oppdater abonnementet med det nye valget av Cronverdi
	 * @param intervalGroup de mulige valgene av cronverdi
	 */
	public void cronExpressionModified(OptionGroup intervalGroup);

	/**
	 * Legg inn en ny e-post til listen over mottakere på abonnementet
	 * @param mail den nye e-posten
	 */
	public void emailRecipientAdded(String mail);

	/**
	 * Oppdater hendelse-intervallet med ny verdi
	 * @param maxLogEntryAge alder i ms
	 */
	public void maxLogEntryAgeModified(long maxLogEntryAge);

	/**
	 * Oppdater abonnementet med ny beskrivelse
	 * @param description beskrivelsen
	 */
	public void descriptionChanged(String description);

	/**
	 * Oppdater abonnementet med ny ID
	 * @param id den nye ID'en
	 */
	public void idChanged(String id);

	/**
	 * Fjern en e-post adresse fra listen over e-post adresser
	 * @param mail e-post adressen
	 */
	public void emailRecipientRemoved(String mail);

}
