package no.goodtech.vaadin.tabs.status.statusloggerMailGui;


import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;
import no.goodtech.vaadin.buttons.AddButton;
import no.goodtech.vaadin.buttons.RemoveButton;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscriptionFinder;

import javax.management.timer.Timer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Inneholder brukergrensesnittet for alle endringer på abonnementet utenom status-indikatorer
 */
public class SubscriptionDetailsPanel extends VerticalLayout {
	private static Map<String, Long> logEntryAges = new LinkedHashMap<String, Long>();
	private final ListSelect emailSelect;
	private final OptionGroup mailSendGroup;
	private final OptionGroup maxAgeGroup;
	private final TextField subscriptionID;
	private final TextField subscriptionDescription;
	private final ISubscriptionAndIndicatorActionListener subscriptionAndIndicatorActionListener;
	
	static {
		logEntryAges.put("En time", Timer.ONE_HOUR);
		logEntryAges.put("Et døgn", Timer.ONE_DAY);
		logEntryAges.put("En uke", Timer.ONE_WEEK);
	}

	/**
	 * Opprett member-variable og initialiser GUI komponenter for ID,beskrivelse,mail-intervall,indikator alder og E-poster
	 *
	 * @param subscriptionAndIndicatorActionListener kommuniserer endringer av abonnement til MailPanel
	 */
	public SubscriptionDetailsPanel(final ISubscriptionAndIndicatorActionListener subscriptionAndIndicatorActionListener) {
		this.subscriptionAndIndicatorActionListener = subscriptionAndIndicatorActionListener;

		setMargin(false);

		emailSelect = new ListSelect("E-post-adresser for varsling:");
		emailSelect.setImmediate(true);
		emailSelect.setNullSelectionAllowed(false);

		mailSendGroup = new OptionGroup("Send mail hver");
		mailSendGroup.addItem("Time");
		mailSendGroup.addItem("Dag");
		mailSendGroup.addItem("Uke");
		mailSendGroup.setImmediate(true);

		maxAgeGroup = new OptionGroup("Hendelser nyere enn");
		for (Map.Entry<String, Long> entry : logEntryAges.entrySet()) {
			final Long age = entry.getValue();
			maxAgeGroup.addItem(age);
			maxAgeGroup.setItemCaption(age, entry.getKey());
		}			
		maxAgeGroup.setImmediate(true);

		subscriptionID = new TextField("Abonnement ID");
		subscriptionID.setImmediate(true);
		subscriptionID.setWidth(200, Unit.PIXELS);
		subscriptionID.setNullRepresentation("");
		subscriptionDescription = new TextField("Abonnement beskrivelse");
		subscriptionDescription.setImmediate(true);
		subscriptionDescription.setWidth(200, Unit.PIXELS);
		subscriptionDescription.setNullRepresentation("");

		VerticalLayout subscriptionInformationpanel = buildSubscriptionInformationPanel();
		VerticalLayout maxAgePanel = buildMaxAgePanel();
		HorizontalLayout intervalPanel = buildIntervalPanel();
		VerticalLayout emailPanel = buildEmailPanel();

		subscriptionInformationpanel.setWidth(100, Unit.PERCENTAGE);
		maxAgePanel.setWidth(100, Unit.PERCENTAGE);
		emailPanel.setWidth(100, Unit.PERCENTAGE);
		intervalPanel.setWidth(100, Unit.PERCENTAGE);

		addComponent(subscriptionInformationpanel);
		addComponent(maxAgePanel);
		addComponent(intervalPanel);
		addComponent(emailPanel);
	}

	/**
	 * Bygg VerticalLayout for abonnement-ID og Abonnement beskrivelse, og bind tap av fokus til en listener
	 * @return VerticalLayout med to tekstfelt, et for ID og et for beskrivelse
	 */
	private VerticalLayout buildSubscriptionInformationPanel() {
		VerticalLayout subscriptionDetails = new VerticalLayout();
		subscriptionDetails.addStyleName("borderTop");

		subscriptionDetails.addComponent(subscriptionID);
		subscriptionDetails.addComponent(subscriptionDescription);

		//lagre verdien i tekstfeltet hvis den nye verdien ikk er tom og den nye verdien ikke er lik den forrige
		subscriptionID.addValueChangeListener(new TextField.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				subscriptionAndIndicatorActionListener.idChanged((String) event.getProperty().getValue());
			}
		});

		//lagre verdien i tekstfeltet hvis den nye verdien ikk er tom og den nye verdien ikke er lik den forrige
		subscriptionDescription.addValueChangeListener(new TextField.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				subscriptionAndIndicatorActionListener.descriptionChanged((String) event.getProperty().getValue());
			}
		});
		return subscriptionDetails;
	}

	/**
	 * Bygg VerticalLayout for e-post
	 * lager et VerticalLayout med komponenter for å liste mail adresser registrert på abonnementet, en knapp for å
	 * fjerne en mail-adresse, en knapp for å legge til ny mail, og et tekstfelt for å skrive inn ny mail
	 *
	 * @return VerticalLayout populert med komponenter bundet til listeners
	 */
	private VerticalLayout buildEmailPanel() {
		VerticalLayout emailPanel = new VerticalLayout();
		emailPanel.addStyleName("borderTop");

		//Tekstfelt for nye e-post adresser
		final TextField emailTextField = new TextField();
		emailTextField.addStyleName("emailTextField");
		emailTextField.setWidth(200, Unit.PIXELS);
		emailTextField.setNullSettingAllowed(true);
		emailTextField.setCaption("Ny e-post-adresse:");

		//Validator for kontroll av tekst-streng i e-post feltet
		final EmailValidator emailValidator = new EmailValidator("Jeg er ikke helt fornøyd med den e-post-adressen");
		emailTextField.addValidator(emailValidator);

		//Knapp for å legge til ny e-post
		AddButton addButton = new AddButton(new AddButton.IAddListener() {
			@Override
			public void addClicked() {
				if (emailTextField.getValue() != null && emailTextField.getValue() != null && emailValidator.isValid(emailTextField.getValue()))
					subscriptionAndIndicatorActionListener.emailRecipientAdded(emailTextField.getValue());
				emailSelect.addItem(emailTextField.getValue());
			}
		});

		//Knapp for å slette e-post
		RemoveButton removeButton = new RemoveButton(new RemoveButton.IRemoveListener() {
			@Override
			public void removeClicked() {
				if (emailSelect.getValue() != null) {
					subscriptionAndIndicatorActionListener.emailRecipientRemoved((String) emailSelect.getValue());
					emailSelect.removeItem(emailSelect.getValue());
				}
			}
		});

		//Opprett et 2x2 rutenett med legg til/fjern knapper, liste over e-poster og tekstfelt for nye e-poster
		final GridLayout grid = new GridLayout(2, 2);

		grid.addComponent(emailSelect, 0, 0);
		grid.addComponent(emailTextField, 0, 1);
		grid.addComponent(removeButton, 1, 0);
		grid.addComponent(addButton, 1, 1);

		grid.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
		grid.setComponentAlignment(removeButton, Alignment.BOTTOM_RIGHT);
		grid.setMargin(new MarginInfo(false, true, false, false));
		emailPanel.addComponent(grid);
		return emailPanel;
	}

	/**
	 * bygg HorizontalLayout for hendelse-intervall
	 * @return HorizontalLayout for hendelse-intervall med komponenter bundet til listener metoder
	 */
	private HorizontalLayout buildIntervalPanel() {
		HorizontalLayout intervalPanel = new HorizontalLayout();
		intervalPanel.addStyleName("borderTop");
		//Kommuniser opp endringer av cron-uttrykk
		mailSendGroup.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				subscriptionAndIndicatorActionListener.cronExpressionModified(mailSendGroup);
			}
		});
		intervalPanel.addComponent(mailSendGroup);
		return intervalPanel;
	}

	/**
	 * Bygg panel for valg av maks-alder på statusindikator-hendelser det blir varslet på
	 * inneholder et VerticalLayout med en OptionGroup bundet til en ValueChangeListener
	 *
	 * @return VerticalLayout med komponenter bundet til en listener metode
	 */
	private VerticalLayout buildMaxAgePanel() {
		VerticalLayout maxAgePanel = new VerticalLayout();
		maxAgePanel.addStyleName("borderTop");
		maxAgePanel.addComponent(maxAgeGroup);

		maxAgeGroup.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				subscriptionAndIndicatorActionListener.maxLogEntryAgeModified((Long) event.getProperty().getValue());
			}
		});
		return maxAgePanel;
	}

	/**
	 * Oppfrisk alle komponenter med ny data fra abonnementet
	 * @param subscription abonnementet
	 */
	public void refreshDetailsPanel(StatusIndicatorSubscription subscription) {
		displayStatusIndicatorSubscriptionID(subscription.getId());
		displayStatusIndicatorSubscriptionDescription(subscription.getDescription());
		selectMailIntervalBasedOnCronExpression(subscription.getCronExpression());
		maxAgeGroup.select(subscription.getMaxLogEntryAge());
		StatusIndicatorSubscriptionFinder statIndFinder = new StatusIndicatorSubscriptionFinder();
		emailSelect.removeAllItems();
		if( subscription.getId()!=null && !subscription.getId().isEmpty())
		{
			for (String mail : statIndFinder.setId(subscription.getId()).load().getEmailRecipients()) {
				emailSelect.addItem(mail);
			}
		}
	}

	/**
	 * Vis abonnement ID i tekstfeltet for id
	 * @param id id til abonnementet
	 */
	private void displayStatusIndicatorSubscriptionID(final String id) {
		subscriptionID.setValue(id);
	}

	/**
	 * Vis abonnement beskrivelse i tekstfeltet for beskrivelse
	 * @param description beskrivelsen av abonnementet
	 */
	private void displayStatusIndicatorSubscriptionDescription(final String description) {
		subscriptionDescription.setValue(description);
	}

	/**
	 *  Velg checkbox for mail-intervall på grunnlag av cron-uttrykket til abonnementet
	 * @param cronExpression cron utrykket
	 */
	private void selectMailIntervalBasedOnCronExpression(String cronExpression) {
		if (cronExpression != null) {
			if (cronExpression.equals("0 0 * * * *")) {
				mailSendGroup.select("Time");
			} else if (cronExpression.equals("0 0 7 * * *")) {
				mailSendGroup.select("Dag");
			} else if (cronExpression.equals("0 0 7 * * MON")) {
				mailSendGroup.select("Uke");
			}
		}
		//Hvis det ikke er satt en rett cronverdi, fjern tidligere valg
		else {
			mailSendGroup.setValue(null);
		}
	}
}
