package no.goodtech.vaadin.tabs.status.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.management.timer.Timer;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import no.cronus.common.utils.CollectionUtils;
import no.goodtech.persistence.entity.AbstractEntityImpl;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.scheduling.support.CronTrigger;

/**
 * Inneholder varslings-innstillinger for en eller flere status-indikator som har gått bananas
 * Tidspunkt for varsling angis via et cron-uttrykk se {@link #setCronExpression(String)}
 *
 * @author oystein
 */
@Entity
@Table(name = "StatusIndicatorSubscription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StatusIndicatorSubscription extends AbstractEntityImpl<StatusIndicatorSubscription> implements StatusIndicatorSubscriptionStub {

	/**
	 * Skilletegn mellom e-post-adresser, se {@link #getEmailRecipientsAsString()}
	 */
	public static final String EMAIL_DELIMITER = ",";

	private String id;

	private String description;

	private String emailRecipients;

	@ManyToMany
	@JoinTable(
			name = "StatusIndicatorSubscription_StatusIndicator",
			joinColumns = @JoinColumn(name = "statusIndicatorSubscription_pk", referencedColumnName = "pk"),
			inverseJoinColumns = @JoinColumn(name = "statusIndicator_pk", referencedColumnName = "pk")
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<StatusIndicator> statusIndicators = new HashSet<StatusIndicator>();

	private Long maxLogEntryAge = Timer.ONE_DAY;

	private String cronExpression;

	@Override
	public String getNiceClassName() {
		return "Status-indikator-varsler";
	}

	/**
	 * Opprett en varslingstjeneste
	 */
	public StatusIndicatorSubscription() {
	}


	public String getId() {
		return id;
	}

	/**
	 * Endre ID
	 *
	 * @param id ny ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Endre beskrivelse
	 *
	 * @param description ny beskrivelse
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public Set<String> getEmailRecipients() {
		Set<String> result = new HashSet<String>();
		if (emailRecipients != null)
			result.addAll(Arrays.asList(emailRecipients.split(EMAIL_DELIMITER)));
		return result;
	}

	public String getEmailRecipientsAsString() {
		return emailRecipients;
	}

	/**
	 * Legg til EN e-post-adresse til den som skal få varsling
	 *
	 * @param emailRecipient e-post-adresse hvis denne er tom/blank, skjer ingenting
	 */
	public void addEmailRecipient(String emailRecipient) {
		if (emailRecipient != null) {
			String trimmedEmailRecipient = emailRecipient.trim();
			if (trimmedEmailRecipient.length() > 0) {
				if (!getEmailRecipients().contains(trimmedEmailRecipient)) {
					if (emailRecipients == null)
						emailRecipients = "";
					else if (emailRecipients.length() > 0)
						this.emailRecipients += EMAIL_DELIMITER;
					this.emailRecipients += emailRecipient;
				}
			}
		}
	}

	/**
	 * Legg til EN e-post-adresse til den som skal få varsling
	 *
	 * @param emailRecipient e-post-adresse hvis denne er tom/blank, skjer ingenting
	 */
	public void removeEmailRecipient(String emailRecipient) {
		System.out.println("EMAILRECIPIENT: " + emailRecipient);
		if (emailRecipient != null) {
			String trimmedEmailRecipient = emailRecipient.trim();
			if (trimmedEmailRecipient.length() > 0) {
				if (emailRecipients != null && getEmailRecipients().contains(trimmedEmailRecipient)) {
					if (emailRecipients.length() > 0) {
						if (emailRecipients.contains(EMAIL_DELIMITER)) {
							if (emailRecipients.contains((EMAIL_DELIMITER + emailRecipient))) {
								this.emailRecipients = this.emailRecipients.replace((EMAIL_DELIMITER + emailRecipient), "");
							} else if (emailRecipients.contains((emailRecipient + EMAIL_DELIMITER))) {
								this.emailRecipients = this.emailRecipients.replace((emailRecipient + EMAIL_DELIMITER), "");
							}
						} else {
							this.emailRecipients = this.emailRecipients.replace((emailRecipient), "");
						}
					}
				}
			}
		}
	}

	/**
	 * Angi e-post-adresser til de som som skal få varsling
	 *
	 * @param emailRecipients e-post-adresse skill adressene med komma
	 */
	public void setEmailRecipients(String emailRecipients) {
		this.emailRecipients = emailRecipients;
	}

	/**
	 * Angi e-post-adresser til de som som skal få varsling
	 *
	 * @param emailRecipients e-post-adresser
	 */
	public void setEmailRecipients(Collection<String> emailRecipients) {
		this.emailRecipients = null;
		for (String emailRecipient : emailRecipients)
			addEmailRecipient(emailRecipient);
	}

	/**
	 * @return status-indikatorer som gir varsling
	 */
	public Set<StatusIndicatorStub> getStatusIndicators() {
		return CollectionUtils.cast(statusIndicators, StatusIndicatorStub.class);
	}

	/**
	 * @param statusIndicators indikatorene du vil bli varslet om
	 */
	public void setStatusIndicators(Set<StatusIndicator> statusIndicators) {
		this.statusIndicators = statusIndicators;
	}

	/**
	 * Legg til en indikator som du vil bli varslet om
	 *
	 * @param statusIndicator indikatoren du vil bli varslet om
	 */
	public void addStatusIndicator(StatusIndicator statusIndicator) {
		this.statusIndicators.add(statusIndicator);
	}

	@Override
	public StatusIndicatorSubscription load() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusIndicatorSubscription save() {
		return super.save();
	}

	public long getMaxLogEntryAge() {
		return maxLogEntryAge;
	}

	/**
	 * Angi hvor langt tilbake i loggen jeg skal sjekke (i millisekunder)
	 *
	 * @param maxLogEntryAge grense for alder på logge-meldinger som skal føre til varsling
	 */
	public void setMaxLogEntryAge(long maxLogEntryAge) {
		this.maxLogEntryAge = maxLogEntryAge;
	}

	@Override
	public void lazyLoad() {
		statusIndicators.size();
	}

	@Override
	public String toString() {
		return "[Varsling for indikator: " + getStatusIndicatorsNames() + "]";
	}

	/**
	 * @return en kommaseparert liste over navn på status-indikatorene
	 */
	public String getStatusIndicatorsNames() {
		StringBuilder indicatorNames = new StringBuilder();
		for (StatusIndicatorStub statusIndicator : statusIndicators) {
			indicatorNames.append(statusIndicator.getName());
			indicatorNames.append(", ");
		}
		if (indicatorNames.length() > 0)
			indicatorNames.delete(indicatorNames.length() - 2, indicatorNames.length()); //fjerner siste komma
		return indicatorNames.toString();
	}

	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Angi innstillinger for når du vil bli varslet
	 *
	 * @param cronExpression en liste av tidspunkter på <a href="http://en.wikipedia.org/wiki/Cron#Format">cron-format</a>
	 * @see CronTrigger
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}


}
