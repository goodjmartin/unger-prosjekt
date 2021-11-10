package no.goodtech.vaadin.tabs.status.statusloggerMailGui;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import no.goodtech.vaadin.tabs.status.model.StatusIndicatorSubscription;

import java.util.List;

/**
 * Abonnement-tabell container
 */
public class StatusIndicatorSubscriptionContainer extends IndexedContainer {
   	//ID til abonnementet
    public static final Object PROPERTY_SUBSCRIPTION_ID = "ID";

	//Beskrivelse til abonnementet
	public static final Object PROPERTY_DESCRIPTION = "Beskrivelse";

     public StatusIndicatorSubscriptionContainer()
     {
         addContainerProperty(PROPERTY_SUBSCRIPTION_ID, String.class, null);
         addContainerProperty(PROPERTY_DESCRIPTION, String.class, null);
     }

	/**
	 * Oppfrisk abonnement-tabellen med alle status-indikator abonnementer
	 * @param statusLoggerRepository repository over status-indikator abonnementer
	 */
    public void refresh(List<StatusIndicatorSubscription> statusLoggerRepository) {
        removeAllItems();
        for(StatusIndicatorSubscription statusIndicatorSubscription : statusLoggerRepository)
        {
          Item item = addItem(statusIndicatorSubscription);
          item.getItemProperty(PROPERTY_SUBSCRIPTION_ID).setValue(statusIndicatorSubscription.getId());
          item.getItemProperty(PROPERTY_DESCRIPTION).setValue(statusIndicatorSubscription.getDescription());
        }
    }
}
