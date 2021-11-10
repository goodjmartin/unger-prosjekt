package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import no.goodtech.vaadin.login.User;
import no.goodtech.vaadin.main.ApplicationResourceBundle;
import no.goodtech.vaadin.security.AccessFunction;
import no.goodtech.vaadin.security.AccessFunctionManager;
import no.goodtech.vaadin.tabs.IMenuView;
import no.goodtech.vaadin.tabs.status.Globals;
import no.goodtech.vaadin.tabs.status.Texts;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscriptionFinder;


@UIScope
@SpringView(name = MailPanel.VIEW_ID)
public class MailPanel extends VerticalLayout implements IMenuView, IMailPanelActionListener {

	public static final String VIEW_ID = "MailPanel";
	public static final String VIEW_NAME = Texts.get("mailPanel.name");

	private final SubscriptionPanel statusIndicatorSubscriptionPanel;
	private final SubscriptionAndIndicatorLayout subscriptionAndIndicatorLayout;
	private volatile StatusIndicatorSubscription subscription = null;
	public static final String STATUS_SUBSCRIPTION_VIEW = "statusSubscriptionView";


	static {
		AccessFunctionManager.registerAccessFunction(new AccessFunction(STATUS_SUBSCRIPTION_VIEW, ApplicationResourceBundle.getInstance("vaadin-status-indicator").getString("accessFunction.statusIndicatorSubscriptionView")));
	}

	/**
	 * Opprett membervariable og sett GUI
	 */
	public MailPanel() {
		setSizeFull();
		//setMargin(false);

		//sett statusIndicatorSubscriptionPanel til å bruke all tildelt plass
		statusIndicatorSubscriptionPanel = new SubscriptionPanel(this);
		statusIndicatorSubscriptionPanel.setMargin(false);
		statusIndicatorSubscriptionPanel.setSizeFull();

		subscriptionAndIndicatorLayout = new SubscriptionAndIndicatorLayout(this);

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.addComponent(statusIndicatorSubscriptionPanel);
		statusIndicatorSubscriptionPanel.refreshContainer(new StatusIndicatorSubscriptionFinder().loadList());

		horizontalSplitPanel.addComponent(subscriptionAndIndicatorLayout);
		subscriptionAndIndicatorLayout.setVisible(false);

		addComponent(horizontalSplitPanel);
		horizontalSplitPanel.setSizeFull();
		horizontalSplitPanel.setSplitPosition(25);
	}

	/**
	 * Oppfrisk paneler for status-indikator og abonnementer med ny data
	 *
	 * @param subscription et abonnement på statusindikatorer
	 */
	@Override
	public void subscriptionSelected(final StatusIndicatorSubscription subscription) {
		if (subscription != null) {
			this.subscription = subscription;
			subscriptionAndIndicatorLayout.refreshStatusIndicatorSubscriptionPanel(this.subscription);
			subscriptionAndIndicatorLayout.setVisible(true);
		} else {
			subscriptionAndIndicatorLayout.setVisible(false);
		}
	}

	/**
	 * Lagre endringer gjort på valgt abonnement og oppfrisk abonnement-tabellen og panelene for status-indikator og
	 * abonnementer med ny data
	 */
	@Override
	public void saveButtonClicked(final StatusIndicatorSubscription subscription) {
		if ((subscription != null) && requiredFieldsValid(subscription)) {
			subscriptionAndIndicatorLayout.refreshStatusIndicatorSubscriptionPanel(subscription.save());
			statusIndicatorSubscriptionPanel.refreshContainer(new StatusIndicatorSubscriptionFinder().loadList());
			Globals.getStatusIndicatorSubscriptionScheduler().refresh();
		}
	}

	@Override
	public boolean isAuthorized(User user, String value) {
		return AccessFunctionManager.isAuthorized(user, STATUS_SUBSCRIPTION_VIEW);
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	/**
	 * Kontroller at alle påkrevde felt er skrevet inn før lagring
	 *
	 * @return true hvis alle felt er skrevet inn og ok, false hvis det er noe galt
	 */
	private boolean requiredFieldsValid(final StatusIndicatorSubscription subscription) {
		boolean valid = true;
		StatusIndicatorSubscriptionFinder statusIndicatorSubscriptionFinder = new StatusIndicatorSubscriptionFinder();
		statusIndicatorSubscriptionFinder.setId(subscription.getId());
		if (statusIndicatorSubscriptionFinder.setId(subscription.getId()).exists() && !statusIndicatorSubscriptionFinder.setId(subscription.getId()).load().getPk().equals(subscription.getPk())) {
			Notification.show("ID er allerede i bruk for et annet abonnement, velg et annet ID");
			valid = false;
		} else if ((subscription.getId() == null) || subscription.getId().trim().length() == 0) {
			Notification.show("ID må være satt før abonnement lagres");
			valid = false;
		} else if (subscription.getCronExpression() == null) {
			Notification.show("Valg for e-post skal sendes må være satt før abonnement lagres");
			valid = false;
		} else if (subscription.getEmailRecipientsAsString() == null || subscription.getEmailRecipientsAsString().isEmpty()) {
			Notification.show("Ingen mottakere registrert i abonnementet, må være minst en");
			valid = false;
		}

		return valid;
	}


	@Override
	public void addClicked() {
		this.subscription = null;
		StatusIndicatorSubscription subscription = new StatusIndicatorSubscription();
		subscriptionAndIndicatorLayout.refreshStatusIndicatorSubscriptionPanel(subscription);
		subscriptionAndIndicatorLayout.setVisible(true);
	}

	@Override
	public void removeClicked() {
		if (subscription != null) {
			subscription.delete();
			subscriptionAndIndicatorLayout.setVisible(false);
			statusIndicatorSubscriptionPanel.refreshContainer(new StatusIndicatorSubscriptionFinder().loadList());
			Globals.getStatusIndicatorSubscriptionScheduler().refresh();
			subscription = null;
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		statusIndicatorSubscriptionPanel.refreshContainer(new StatusIndicatorSubscriptionFinder().loadList());
	}
}